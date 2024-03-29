import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

/**
 * Base class for unit tests that provides convenience assertion methods.
 * 
 * Use public static methods with the prefix 'testSafe' or 'testUnsafe' for
 * methods to be analyzed by Soot. Then, create corresponding '_test' method
 * with the @Test annotation and call{@link #assertAnalysis(String)}.
 * 
 * Do not forget to add a main method that calls the 'test*' methods. Soot needs
 * it for points-to analysis. Also, add a call to the main method of the test
 * class to {@link AllTests#main(String[])}.
 */
public abstract class AbstractTest {

	// Global BufferOverflowDetector, such that Soot performs the points-to
	// analysis only once. Our analysis is then only performed on-demand.
	private static final BufferOverflowDetector DETECTOR;

	static {
		String mainClassName = AllTests.class.getName();
		DETECTOR = new BufferOverflowDetector(mainClassName);
	}

	public AbstractTest() {
	}

	/**
	 * Asserts that the {@link BufferOverflowDetector} is both sound and correct
	 * w.r.t. to the analysis of given method.
	 * 
	 * The method name must contain string "Safe" if the method is safe in the
	 * concrete, meaning that no {@link ArrayIndexOutOfBoundsException} can
	 * occur, or "Unsafe" if an {@link ArrayIndexOutOfBoundsException} can occur
	 * in a concrete execution.
	 * 
	 * @param methodName
	 *            name of the method in the subclass
	 */
	protected void assertAnalysis(String methodName) {
		String className = getClass().getName();
		AnalysisResult result = DETECTOR.getAnalysisResult(className,
				methodName);
		Method method = null;
		for (Method someMethod : getClass().getMethods()) {
			if (someMethod.getName().equals(methodName)) {
				method = someMethod;
				break;
			}
		}

		if (method == null) {
			throw new IllegalArgumentException("could not find method "
					+ methodName + " through reflection");
		}

		if (method.getAnnotation(Safe.class) != null) {
			assertTrue("IMPRECISION: Method " + methodName
					+ " is safe in the concrete, but was not detected as such",
					result.isSafe());
		} else if (method.getAnnotation(Unsafe.class) != null) {
			assertFalse(
					"UNSOUNDNESS: Method "
							+ methodName
							+ " is unsafe in the concrete, but was not detected as such",
					result.isSafe());
		} else {
			throw new IllegalArgumentException("method " + methodName
					+ " must either have the annotation @Safe or @Unsafe");
		}
	}

	/**
	 * Returns a random integer. Determined emprically by rolling a dice.
	 * 
	 * Our analysis assumes that a method with an integer return value always
	 * returns {@link Interval#TOP}. This helper method can be used in
	 * unit tests.
	 * 
	 * @return random integer
	 */
	protected static int getAnyInt() {
		return 4;
	}

}
