import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Base class for unit tests that provides convenience assertion methods.
 */
public class AbstractTest {

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
