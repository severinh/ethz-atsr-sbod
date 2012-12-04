import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Base class for unit tests that provides convenience assertion methods.
 * 
 * Use public static methods with the prefix 'testSafe' or 'testUnsafe' for
 * methods to be analyzed by Soot. Then, create corresponding '_test' method
 * with the @Test annotation and either call{@link #assertSafe(String)} or
 * {@link #assertMaybeUnsafe(String)} in it.
 * 
 * Do not forget to add a main method that calls the 'test*' methods. Soot needs
 * it for points-to analysis.
 */
public abstract class AbstractTest {

	private final BufferOverflowDetector detector;

	public AbstractTest() {
		detector = new BufferOverflowDetector(getClass().getName());
	}

	/**
	 * Asserts that a given method in the test class is deemed safe.
	 * 
	 * @param methodName
	 *            name of the method in the subclass
	 */
	protected void assertSafe(String methodName) {
		AnalysisResult result = detector.getAnalysisResult(methodName);
		assertTrue("Method " + methodName
				+ " is safe, but was not detected as such", result.isSafe());
	}

	/**
	 * Asserts that a given method in the test class is deemed potentially
	 * unsafe.
	 * 
	 * @param methodName
	 *            name of the method in the subclass
	 */
	protected void assertMaybeUnsafe(String methodName) {
		AnalysisResult result = detector.getAnalysisResult(methodName);
		assertFalse("Method " + methodName
				+ " is potentially unsafe, but was not detected as such",
				result.isSafe());
	}

}
