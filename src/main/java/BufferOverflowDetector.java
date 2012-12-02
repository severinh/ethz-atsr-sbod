import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import soot.EntryPoints;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.spark.SparkTransformer;
import soot.toolkits.graph.BriefUnitGraph;

public class BufferOverflowDetector {

	public static Logger LOG = Logger.getLogger(BufferOverflowDetector.class
			.getName());

	/**
	 * Report a method as safe.
	 * 
	 * @param className
	 *            name of the class
	 * @param methodName
	 *            name of the safe method
	 */
	public static void reportMethodIsSafe(String className, String methodName) {
		System.out.printf("***$$$***(:=:) %s.%s is SAFE\n", className,
				methodName);
	}

	/**
	 * Report a method as potentially unsafe.
	 * 
	 * @param className
	 *            name of the class
	 * @param methodName
	 *            name of the potentially unsafe method
	 */
	public static void reportMethodMaybeUnsafe(String className,
			String methodName) {
		System.out.printf("***$$$***):=:( %s.%s is UNSAFE ???\n", className,
				methodName);
	}

	public static void main(String[] args) {
		soot.options.Options.v().set_allow_phantom_refs(true);
		soot.options.Options
				.v()
				.set_soot_classpath(
						soot.options.Options.v().soot_classpath()
								+ ":/home/severinh/Documents/ETH/9/ATSR/sbod/target/classes");
		for (String analyzedClass : args) {
			LOG.info("Analyzing " + analyzedClass + "...");
			SootClass c = loadClass(analyzedClass, true);
			soot.Scene.v().loadNecessaryClasses();
			soot.Scene.v().setEntryPoints(EntryPoints.v().all());

			HashMap<String, Analysis> perMethodIntervalAnalysis = new HashMap<String, Analysis>();

			for (SootMethod method : c.getMethods()) {
				if (!method.getName().startsWith("test"))
					continue;

				LOG.info("Analyzing method " + method.getName() + "...");
				Analysis analysis = new Analysis(new BriefUnitGraph(
						method.retrieveActiveBody()));
				LOG.info(String.format(
						"Running intervals analysis on %s.%s...",
						analyzedClass, method.getName()));
				analysis.run();
				perMethodIntervalAnalysis.put(method.getName(), analysis);
			}

			/*
			 * // The pointer analysis is slow. Disable it while you work on the
			 * intervals. setSparkPointsToAnalysis(); soot.PointsToAnalysis pta
			 * = Scene.v().getPointsToAnalysis(); for (SootMethod method :
			 * c.getMethods()) { if (!method.getName().startsWith("test"))
			 * continue; JimpleBody body =
			 * (JimpleBody)method.retrieveActiveBody(); for (Local local :
			 * body.getLocals()) { if (local.getType() instanceof RefLikeType) {
			 * PointsToSet points_to = pta.reachingObjects(local); // TODO:
			 * Decide how to use the points_to set and check if a method is
			 * safe. System.out.println(local.getName() + " points to " +
			 * points_to); } } }
			 */

			// We didn't finish doing any work, so we just report we can't prove
			// anything:
			for (SootMethod method : c.getMethods()) {
				if (!method.getName().startsWith("test")) {
					continue;
				}
				reportMethodMaybeUnsafe(analyzedClass, method.getName());
			}
		}
	}

	// Magic for the soot pointer analysis. We may suggest other magic. Do not
	// change.
	static {
		soot.options.Options.v().set_keep_line_number(true);
		soot.options.Options.v().set_whole_program(true);
		soot.options.Options.v().setPhaseOption("cg", "verbose:true");
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

	public static void setup() {
		Logger root = Logger.getRootLogger();
		if (root.getAllAppenders().hasMoreElements()) {
			// Logger is already initialized
			return;
		}
		DOMConfigurator.configure(BufferOverflowDetector.class.getResource("/log4j.xml"));
	}

}
