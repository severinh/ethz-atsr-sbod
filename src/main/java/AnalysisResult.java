import soot.jimple.Stmt;

/**
 * Encapsulates the analysis result of an individual method and provides output
 * functionality.
 */
public class AnalysisResult {

	private final String className;
	private final String methodName;
	private final Stmt unsafeStatement;

	public AnalysisResult(String className, String methodName,
			Stmt unsafeStatement) {
		super();
		this.className = className;
		this.methodName = methodName;
		this.unsafeStatement = unsafeStatement;
	}

	public String getClassName() {
		return className;
	}

	public String getMethodName() {
		return methodName;
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
