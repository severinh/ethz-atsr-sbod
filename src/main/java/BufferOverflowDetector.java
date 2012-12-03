import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import soot.Body;
import soot.EntryPoints;
import soot.Local;
import soot.PointsToSet;
import soot.RefLikeType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.JimpleBody;
import soot.jimple.spark.SparkTransformer;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class BufferOverflowDetector {

	private static final boolean USE_POINTS_TO_ANALYSIS = false;
	private static final Logger LOG = Logger
			.getLogger(BufferOverflowDetector.class.getName());

	private final String className;
	private final SootClass sootClass;
	private final Map<String, Analysis> methodAnalyses;

	public BufferOverflowDetector(String className) {
		this.className = className;
		this.sootClass = loadClass(className, true);
		this.methodAnalyses = new HashMap<String, Analysis>();

		soot.Scene.v().loadNecessaryClasses();
		soot.Scene.v().setEntryPoints(EntryPoints.v().all());

		analyzeClass();
	}

	private List<SootMethod> getTestMethods() {
		List<SootMethod> testMethods = new ArrayList<SootMethod>();
		for (SootMethod method : sootClass.getMethods()) {
			if (method.getName().startsWith("test")) {
				testMethods.add(method);
			}
		}
		return testMethods;
	}

	private void analyzeClass() {
		LOG.info("Analyzing " + className + "...");
		List<SootMethod> testMethods = getTestMethods();

		if (USE_POINTS_TO_ANALYSIS) {
			setSparkPointsToAnalysis();
			soot.PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
			for (SootMethod method : testMethods) {
				JimpleBody body = (JimpleBody) method.retrieveActiveBody();
				for (Local local : body.getLocals()) {
					if (local.getType() instanceof RefLikeType) {
						PointsToSet points_to = pta.reachingObjects(local);
						// TODO: Decide how to use the points_to set and
						// check if a method is safe.
						LOG.info(local.getName() + " points to " + points_to);
					}
				}
			}
		}

		for (SootMethod method : testMethods) {
			String methodName = method.getName();

			LOG.info("Analyzing method " + methodName + "...");
			Body body = method.retrieveActiveBody();
			UnitGraph graph = new BriefUnitGraph(body);
			Analysis analysis = new Analysis(graph);
			analysis.run();

			LOG.info(String.format("Running intervals analysis on %s.%s...",
					className, methodName));

			methodAnalyses.put(methodName, analysis);
		}
	}

	public AnalysisResult getAnalysisResult(String methodName) {
		Analysis analysis = methodAnalyses.get(methodName);
		boolean isSafe = analysis.isSafe();
		AnalysisResult result = new AnalysisResult(className, methodName,
				isSafe);
		return result;
	}

	public List<AnalysisResult> getAnalysisResults() {
		List<AnalysisResult> results = new ArrayList<AnalysisResult>();
		for (SootMethod method : getTestMethods()) {
			results.add(getAnalysisResult(method.getName()));
		}
		return results;
	}

	public static void main(String[] args) {
		for (String className : args) {
			BufferOverflowDetector detector = new BufferOverflowDetector(
					className);
			for (AnalysisResult result : detector.getAnalysisResults()) {
				if (result.getMethodName().startsWith("test")) {
					System.out.println(result.toCanonicalString());
				}
			}
		}
	}

	// Magic for the soot pointer analysis. We may suggest other magic. Do not
	// change.
	static {
		soot.options.Options.v().set_keep_line_number(true);
		soot.options.Options.v().set_whole_program(true);
		soot.options.Options.v().setPhaseOption("cg", "verbose:true");
		soot.options.Options.v().set_allow_phantom_refs(true);
		String existingClasspath = soot.options.Options.v().soot_classpath();
		String testClasspath = "/home/severinh/Documents/ETH/9/ATSR/sbod/target/classes";
		String newClasspath = existingClasspath + ":" + testClasspath;
		soot.options.Options.v().set_soot_classpath(newClasspath);
		setupLogging();
	}

	private static SootClass loadClass(String name, boolean isMainClass) {
		SootClass sootClass = Scene.v().loadClassAndSupport(name);
		sootClass.setApplicationClass();
		if (isMainClass) {
			Scene.v().setMainClass(sootClass);
		}
		return sootClass;
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
