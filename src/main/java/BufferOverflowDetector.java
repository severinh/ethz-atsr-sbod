import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import soot.Body;
import soot.EntryPoints;
import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.RefLikeType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.options.Options;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

/**
 * Detects potential {@link ArrayIndexOutOfBoundsException}s.
 */
public class BufferOverflowDetector {

	// Whether to use points-to-analysis or not (expensive)
	private static final boolean USE_POINTS_TO_ANALYSIS = true;
	private static final String TEST_METHOD_PREFIX = "test";
	static final Logger LOG = Logger.getLogger(BufferOverflowDetector.class
			.getName());

	// Cached method analyses
	private final Map<SootMethod, Analysis> methodAnalyses;

	public BufferOverflowDetector(String mainClassName) {
		this.methodAnalyses = new HashMap<SootMethod, Analysis>();

		SootClass mainSootClass = Scene.v().loadClassAndSupport(mainClassName);
		mainSootClass.setApplicationClass();
		Scene.v().setMainClass(mainSootClass);
		Scene.v().loadNecessaryClasses();
		Scene.v().setEntryPoints(EntryPoints.v().all());

		if (USE_POINTS_TO_ANALYSIS) {
			setSparkPointsToAnalysis();
		}
	}

	/**
	 * Returns the list of {@link SootMethod}s in the given class whose name
	 * begins with 'test'.
	 * 
	 * @return the list of methods
	 */
	private List<SootMethod> getTestMethods(SootClass sootClass) {
		List<SootMethod> testMethods = new ArrayList<SootMethod>();
		for (SootMethod method : sootClass.getMethods()) {
			if (method.getName().startsWith(TEST_METHOD_PREFIX)) {
				testMethods.add(method);
			}
		}
		return testMethods;
	}

	/**
	 * Runs an {@link Analysis} for each method in the given class and caches
	 * the {@link Analysis} objects.
	 */
	private void analyzeClass(SootClass sootClass) {
		LOG.info("Analyzing " + sootClass.getName() + "...");
		List<SootMethod> testMethods = getTestMethods(sootClass);

		for (SootMethod method : testMethods) {
			String methodName = method.getName();

			LOG.info("Analyzing method " + methodName + "...");
			Body body = method.retrieveActiveBody();
			UnitGraph graph = new BriefUnitGraph(body);
			Analysis analysis = new Analysis(graph);
			analysis.run();

			methodAnalyses.put(method, analysis);
		}
	}

	/**
	 * Returns the {@link Analysis} for a given method. If it has not been
	 * computed yet, it performs an analysis of all methods in the declaring
	 * class.
	 * 
	 * @param sootMethod
	 * @return the analysis
	 */
	public Analysis getMethodAnalysis(SootMethod sootMethod) {
		SootClass sootClass = sootMethod.getDeclaringClass();
		if (!methodAnalyses.containsKey(sootMethod)) {
			analyzeClass(sootClass);
		}
		Analysis analysis = methodAnalyses.get(sootMethod);
		return analysis;
	}

	/**
	 * Checks each array access of a method for the possibility of an
	 * {@link ArrayIndexOutOfBoundsException}.
	 * 
	 * @param sootMethod
	 *            the method to search for unsafe statements
	 * @return the analysis result
	 */
	public AnalysisResult getAnalysisResult(SootMethod sootMethod) {
		Analysis analysis = getMethodAnalysis(sootMethod);
		String methodName = sootMethod.getName();

		// Maps each static or non-static array fields as well as array
		// parameters to an array size interval
		IntervalPerVar context = new IntervalPerVar();

		if (USE_POINTS_TO_ANALYSIS) {
			PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
			JimpleBody body = (JimpleBody) sootMethod.retrieveActiveBody();

			for (Local local : body.getLocals()) {
				if (local.getType() instanceof RefLikeType) {
					PointsToSet pointsToSet = pta.reachingObjects(local);
					LOG.info(local.getName() + " points to " + pointsToSet);

					if (pointsToSet instanceof PointsToSetInternal) {
						PointsToSetInternal ptsi = (PointsToSetInternal) pointsToSet;
						ptsi.forall(new ArraySizeIntervalCollector(this, local,
								context, methodName));
					} else {
						LOG.warn("Cannot iterate over nodes in points to set.");
					}
				}
			}
		}

		Stmt firstUnsafeStatement = analysis.getFirstUnsafeStatement(context);
		AnalysisResult result = new AnalysisResult(sootMethod,
				firstUnsafeStatement);
		return result;
	}

	public AnalysisResult getAnalysisResult(String className, String methodName) {
		SootClass sootClass = Scene.v().getSootClass(className);
		SootMethod sootMethod = sootClass.getMethodByName(methodName);
		AnalysisResult result = getAnalysisResult(sootMethod);
		return result;
	}

	public List<AnalysisResult> getAnalysisResults(String className) {
		List<AnalysisResult> results = new ArrayList<AnalysisResult>();
		SootClass sootClass = Scene.v().getSootClass(className);
		for (SootMethod sootMethod : getTestMethods(sootClass)) {
			results.add(getAnalysisResult(sootMethod));
		}
		return results;
	}

	/**
	 * Expects a list of class names to analyze.
	 * 
	 * Only the first class is used as the main class as there cannot be
	 * multiple main classes in a Soot {@link Scene}.
	 * 
	 * @param classNames
	 *            a list of class names
	 */
	public static void main(String[] classNames) {
		String mainClassName = classNames[0];
		BufferOverflowDetector detector = new BufferOverflowDetector(
				mainClassName);
		for (String className : classNames) {
			for (AnalysisResult result : detector.getAnalysisResults(className)) {
				System.out.println(result.toCanonicalString());
			}
		}
	}

	static {
		Options.v().set_keep_line_number(true);
		Options.v().set_whole_program(true);
		Options.v().set_app(true);
		Options.v().setPhaseOption("cg", "verbose:true");
		Options.v().setPhaseOption("cg", "all-reachable:true");
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_output_format(Options.output_format_none);

		// Perform a whole-program analysis, but without the JDK
		// It is unsound, but makes testing blazing fast
		Options.v().set_no_bodies_for_excluded(true);
		// Soot choked because the following two classes were not loaded
		// For JDKs other than OpenJDK 7, other classes might be needed
		Scene.v().addBasicClass("sun.misc.ClassFileTransformer");
		Scene.v().addBasicClass("java.io.ObjectStreamClass$MemberSignature");

		setupLogging();
	}

	static void setSparkPointsToAnalysis() {
		LOG.debug("[spark] Starting points-to analysis...");

		HashMap<String, String> flags = new HashMap<String, String>();
		flags.put("enabled", "true");
		flags.put("verbose", "true");
		flags.put("ignore-types", "false");
		flags.put("force-gc", "false");
		flags.put("pre-jimplify", "false");
		flags.put("vta", "false");
		flags.put("rta", "false");
		flags.put("field-based", "false");
		flags.put("types-for-sites", "false");
		flags.put("merge-stringbuffer", "true");
		flags.put("string-constants", "false");
		flags.put("simulate-natives", "true");
		flags.put("simple-edges-bidirectional", "false");
		flags.put("on-fly-cg", "true");
		flags.put("simplify-offline", "false");
		flags.put("simplify-sccs", "false");
		flags.put("ignore-types-for-sccs", "false");
		flags.put("propagator", "worklist");
		flags.put("set-impl", "double");
		flags.put("double-set-old", "hybrid");
		flags.put("double-set-new", "hybrid");
		flags.put("dump-html", "false");
		flags.put("dump-pag", "false");
		flags.put("dump-solution", "false");
		flags.put("topo-sort", "false");
		flags.put("dump-types", "true");
		flags.put("class-method-var", "true");
		flags.put("dump-answer", "false");
		flags.put("add-tags", "false");
		flags.put("set-mass", "false");

		SparkTransformer.v().transform("", flags);
		LOG.debug("[spark] Pointer analysis Done!");
	}

	public static void setupLogging() {
		Logger root = Logger.getRootLogger();
		if (root.getAllAppenders().hasMoreElements()) {
			// Logger is already initialized
			return;
		}
		DOMConfigurator.configure(BufferOverflowDetector.class
				.getResource("/log4j.xml"));
	}

}
