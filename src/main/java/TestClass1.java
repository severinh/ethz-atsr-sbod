import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestClass1 {

	private final BufferOverflowDetector detector;

	public TestClass1() {
		detector = new BufferOverflowDetector(TestClass1.class.getName());
	}

	// This is a program the tool will try to prove.
	// Every method that has name starting with test must be analyzed and
	// reported.

	// No method will get parameters other than objects. The global pointer
	// analysis will take care of the objects.
	// The interval analysis needs to be done only on local variables.
	public static void main(String[] args) {
		// Call test1 so that the pointer analysis can work.
		test1();

		try {
			test4();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void test1() {
		int[] code = new int[7];
		for (int i = 0; i < 7; ++i) {
			code[i] = (i * 3) % 7;
		}
		test3(code);
		test2(code);
	}

	@Test
	public void _test1() {
		assertSafe("test1");
	}

	public static void test2(int[] code) {
		// The pointer analysis should be able to tell you that code was
		// allocated with size 7 and prove this method.
		int sum = 0;
		for (int i = 7; i >= 0; --i) {
			sum += code[i];
		}
		System.out.println("Sum = " + sum);
	}

	@Test
	public void _test2() {
		assertSafe("test2");
	}

	public static void test3(int[] code) {
		int[] revcode = new int[7];
		for (int i = 0; i < 7; ++i) {
			// For this code, the analysis may be imprecise.
			// We may not be able to tell if the contents of an array fits into
			// bounds.
			revcode[code[i]] = i;
		}
	}

	@Test
	public void _test3() {
		assertMaybeUnsafe("test3");
	}

	public static void test4() {
		int[] code = new int[7];
		for (int i = 0; i < 8; ++i) {
			code[i] = i;
		}
	}

	@Test
	public void _test4() {
		assertMaybeUnsafe("test4");
	}

	private void assertSafe(String methodName) {
		AnalysisResult result = detector.analyzeMethod(methodName);
		assertTrue(result.isSafe());
	}

	private void assertMaybeUnsafe(String methodName) {
		AnalysisResult result = detector.analyzeMethod(methodName);
		assertFalse(result.isSafe());
	}

}
