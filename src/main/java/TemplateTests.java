import org.junit.Test;

/**
 * Various tests from the project template.
 */
public class TemplateTests extends AbstractTest {

	public static void main(String[] args) {
		// Call test1 so that the pointer analysis can work
		testSafe1();

		try {
			testUnsafe4();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void testSafe1() {
		int[] code = new int[7];
		for (int i = 0; i < 7; ++i) {
			code[i] = (i * 3) % 7;
		}
		testUnsafe3(code);
		testSafe2(code);
	}

	@Test
	public void _testSafe1() {
		assertAnalysis("testSafe1");
	}

	public static void testSafe2(int[] code) {
		// The pointer analysis should be able to tell you that code was
		// allocated with size 7 and prove this method.
		int sum = 0;
		for (int i = 7; i >= 0; --i) {
			sum += code[i];
		}
		System.out.println("Sum = " + sum);
	}

	@Test
	public void _testSafe2() {
		assertAnalysis("testSafe2");
	}

	public static void testUnsafe3(int[] code) {
		int[] revcode = new int[7];
		for (int i = 0; i < 7; ++i) {
			// For this code, the analysis may be imprecise. We may not be able
			// to tell if the contents of an array fits into bounds.
			revcode[code[i]] = i;
		}
	}

	@Test
	public void _testUnsafe3() {
		assertAnalysis("testUnsafe3");
	}

	public static void testUnsafe4() {
		int[] code = new int[7];
		for (int i = 0; i < 8; ++i) {
			code[i] = i;
		}
	}

	@Test
	public void _testUnsafe4() {
		assertAnalysis("testUnsafe4");
	}

}
