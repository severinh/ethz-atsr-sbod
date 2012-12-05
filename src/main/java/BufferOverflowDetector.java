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
import soot.Value;
import soot.jimple.IntConstant;
import soot.jimple.JimpleBody;
import soot.jimple.NewArrayExpr;
import soot.jimple.Stmt;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.options.Options;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

/**
 * Detects potential {@link ArrayIndexOutOfBoundsException}s within a single
 * class.
 */
public class BufferOverflowDetector {

	// Whether to use points-to-analysis or not (expensive)
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

		Scene.v().loadNecessaryClasses();
		Scene.v().setEntryPoints(EntryPoints.v().all());
		
		analyzeClass();
	}

	/**
	 * Returns the list of {@link SootMethod}s in the class whose name begins
	 * with 'test'.
	 * 
	 * @return the list of methods
	 */
	private List<SootMethod> getTestMethods() {
		List<SootMethod> testMethods = new ArrayList<SootMethod>();
		for (SootMethod method : sootClass.getMethods()) {
			if (method.getName().startsWith("test")) {
				testMethods.add(method);
			}
		}
		return testMethods;
	}

	/**
	 * Runs an {@link Analysis} for each method in the class and caches the
	 * {@link Analysis} objects.
	 */
	private void analyzeClass() {
		LOG.info("Analyzing " + className + "...");
		List<SootMethod> testMethods = getTestMethods();

		for (SootMethod method : testMethods) {
			String methodName = method.getName();

			LOG.info("Analyzing method " + methodName + "...");
			Body body = method.retrieveActiveBody();
			UnitGraph graph = new BriefUnitGraph(body);
			Analysis analysis = new Analysis(graph);
			analysis.run();

			methodAnalyses.put(methodName, analysis);
		}

		if (USE_POINTS_TO_ANALYSIS) {
			setSparkPointsToAnalysis();
		}
	}

	/**
	 * Checks each array access of a method for the possibility of an
	 * {@link ArrayIndexOutOfBoundsException}.
	 * 
	 * @param methodName
	 *            the name of the method
	 * @return the analysis result
	 */
	public AnalysisResult getAnalysisResult(final String methodName) {
		Analysis analysis = methodAnalyses.get(methodName);

		// Maps each static or non-static array fields as well as array
		// parameters to an array size interval
		final IntervalPerVar context = new IntervalPerVar();

		if (USE_POINTS_TO_ANALYSIS) {
			PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
			SootMethod method = sootClass.getMethodByName(methodName);
			JimpleBody body = (JimpleBody) method.retrieveActiveBody();

			for (Local local : body.getLocals()) {
				if (local.getType() instanceof RefLikeType) {
					PointsToSet pointsToSet = pta.reachingObjects(local);
					
					// TODO: Read the array size intervals from the Analysis
					// objects for the method that the arrays are initialized
					// in. Merge the intervals and store it in the context.
					LOG.info(local.getName() + " points to " + pointsToSet);
					
					if(pointsToSet instanceof PointsToSetInternal){
						PointsToSetInternal ptsi = (PointsToSetInternal) pointsToSet;
						final Local localPtr = local; // capture variable local for use in closure
						ptsi.forall(new P2SetVisitor() {
							@Override
							public void visit(Node n) {
								if(n instanceof AllocNode){
									AllocNode allocNode = (AllocNode) n;
									Object newExprRaw = allocNode.getNewExpr();
									if(newExprRaw instanceof NewArrayExpr){
										NewArrayExpr alloc = (NewArrayExpr) newExprRaw;
										Value lenVal = alloc.getSize();
										// Retrieve analysis of method containing the allocation node.
										Analysis allocEnv = methodAnalyses.get(allocNode.getMethod());
										if(lenVal instanceof IntConstant){
											context.putIntervalForVar(localPtr.getName(), Interval.of(((IntConstant)lenVal).value));
										} else if(allocEnv != null) {
											// The allocation is in a method that we have analysed. 
											// We should be able to give an interval for the array size.
											Interval size = allocEnv.getAllocationNodeMap().get(alloc);
											if(size != null){
												context.putIntervalForVar(localPtr.getName(), size);
												LOG.debug("Pointer analysis indicates that the array referred to by " 
														+ localPtr.getName() + " in method " + methodName 
														+ ", allocated as " + alloc + " in method " 
														+ allocNode.getMethod().getName() 
														+ " has size " + size + ".");
											} else {
												context.putIntervalForVar(localPtr.getName(), Interval.NON_NEGATIVE);
												LOG.debug("No size on record for allocation site of the array referred to by " 
														+ localPtr.getName() + " in method " + methodName 
														+ ", allocated as " + alloc + " in method " 
														+ allocNode.getMethod().getName() + ".");
											}
										} else {
											// The allocation is in a method that we have not analysed. Will use TOP for array size.
											context.putIntervalForVar(localPtr.getName(), Interval.NON_NEGATIVE);
											LOG.warn("Missing analysis for method " + allocNode.getMethod() + 
													" containing array allocation site " + alloc + 
													". Lookup triggered by reference " + localPtr + " in method " + methodName + ".");
										}
									}
								} else {
									LOG.warn("Non-allocation node in points-to analysis results for pointer " 
											+ localPtr + " in method " 
											+ methodName + ".");
									context.putIntervalForVar(localPtr.getName(), Interval.NON_NEGATIVE);
								}
							}
						});
					} else {
						LOG.warn("Cannot iterate over nodes in points to set.");
					}
				}
			}
		}

		Stmt firstUnsafeStatement = analysis.getFirstUnsafeStatement(context);
		AnalysisResult result = new AnalysisResult(className, methodName,
				firstUnsafeStatement);
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
				System.out.println(result.toCanonicalString());
			}
		}
	}

	static {
		Options.v().set_keep_line_number(true);
		Options.v().set_whole_program(true);
		Options.v().set_app(true);
		Options.v().setPhaseOption("cg", "verbose:true");
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_output_format(Options.output_format_none);
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
		// flags.put("verbose", "true");
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
