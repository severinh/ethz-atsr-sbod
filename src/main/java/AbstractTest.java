import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AbstractTest {

	private final BufferOverflowDetector detector;

	public AbstractTest() {
		detector = new BufferOverflowDetector(getClass().getName());
	}

	protected void assertSafe(String methodName) {
		AnalysisResult result = detector.analyzeMethod(methodName);
		assertTrue("Method " + methodName
				+ " is safe, but was not detected as such", result.isSafe());
	}

	protected void assertMaybeUnsafe(String methodName) {
		AnalysisResult result = detector.analyzeMethod(methodName);
		assertFalse("Method " + methodName
				+ " is potentially unsafe, but was not detected as such",
				result.isSafe());
	}

}
