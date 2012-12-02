import java.util.HashMap;

import soot.EntryPoints;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.spark.SparkTransformer;
import soot.toolkits.graph.BriefUnitGraph;

public class BufferOverflowDetector {
	// Use this method to report safe methods.
	public static void reportMethodIsSafe(String className, String methodName) {
		System.out.printf("***$$$***(:=:) %s.%s is SAFE\n", className,
				methodName);
	}

	// Use this method to report maybe unsafe methods.
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
			System.out.println(analyzedClass);
			SootClass c = loadClass(analyzedClass, true);
			soot.Scene.v().loadNecessaryClasses();
			soot.Scene.v().setEntryPoints(EntryPoints.v().all());

			HashMap<String, Analysis> perMethodIntervalAnalysis = new HashMap<String, Analysis>();

			for (SootMethod method : c.getMethods()) {
				if (!method.getName().startsWith("test"))
					continue;

				System.out
						.println("***********************************************************");
				System.out.println("Testing method: " + method.getName());
				Analysis analysis = new Analysis(new BriefUnitGraph(
						method.retrieveActiveBody()));
				System.out.printf("Running intervals analysis on %s.%s",
						analyzedClass, method.getName());
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
				if (!method.getName().startsWith("test"))
					continue;
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

	private static SootClass loadClass(String name, boolean main) {
		SootClass c = Scene.v().loadClassAndSupport(name);
		c.setApplicationClass();
		if (main)
			Scene.v().setMainClass(c);
		return c;
	}

	static void setSparkPointsToAnalysis() {
		System.out.println("[spark] Starting points-to analysis ...");
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
		System.out.println("[spark] Pointer analysis Done!");
	}
}
