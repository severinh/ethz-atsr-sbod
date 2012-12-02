public class AnalysisResult {

	private final String className;
	private final String methodName;
	private final boolean isSafe;

	public AnalysisResult(String className, String methodName, boolean isSafe) {
		super();
		this.className = className;
		this.methodName = methodName;
		this.isSafe = isSafe;
	}

	public String getClassName() {
		return className;
	}

	public String getMethodName() {
		return methodName;
	}

	public boolean isSafe() {
		return isSafe;
	}

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
