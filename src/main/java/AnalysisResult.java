import soot.SootClass;
import soot.SootMethod;
import soot.jimple.Stmt;

/**
 * Encapsulates the analysis result of an individual method and provides output
 * functionality.
 */
public class AnalysisResult {

	private final SootMethod sootMethod;
	private final Stmt unsafeStatement;

	public AnalysisResult(SootMethod sootMethod, Stmt unsafeStatement) {
		super();
		this.sootMethod = sootMethod;
		this.unsafeStatement = unsafeStatement;
	}

	public SootMethod getSootMethod() {
		return sootMethod;
	}

	public SootClass getSootClass() {
		return getSootMethod().getDeclaringClass();
	}

	public String getClassName() {
		return getSootClass().getName();
	}

	public String getMethodName() {
		return getSootMethod().getName();
	}

	public boolean isSafe() {
		return getUnsafeStmt() == null;
	}

	public Stmt getUnsafeStmt() {
		return unsafeStatement;
	}

	/**
	 * Returns the String as seen in the project template to indicate whether a
	 * method is safe or potentially unsafe.
	 * 
	 * @return the canonical output String
	 */
	public String toCanonicalString() {
		String format = null;
		if (isSafe()) {
			format = "***$$$***(:=:) %s.%s is SAFE";
		} else {
			format = "***$$$***):=:( %s.%s is UNSAFE ???";
		}
		String result = String.format(format, getClassName(), getMethodName());
		return result;
	}

}
