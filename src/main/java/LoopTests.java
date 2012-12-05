import org.junit.Test;

public class LoopTests extends AbstractTest {

	public static void main(String[] args) {
		testSafeLoop();
	}

	public static void testSafeLoop() {
		int[] array = new int[8];
		for (int i = 0; i < 8; i++) {
			array[i] = i;
		}
	}

	@Test
	public void _testSafeLoop() {
		assertAnalysis("testSafeLoop");
	}

	public static void testSafeLoopLength() {
		int[] array = new int[8];
		for (int i = 0; i < array.length; i++) {
			array[i] = i;
		}
	}

	@Test
	public void _testSafeLoopLength() {
		assertAnalysis("testSafeLoopLength");
	}

	public static void testUnsafeLoopLower() {
		int[] array = new int[8];
		for (int i = -1; i < 8; i++) {
			array[i] = i;
		}
	}

	@Test
	public void _testUnsafeLoopLower() {
		assertAnalysis("testUnsafeLoopLower");
	}

	public static void testUnsafeLoopUpperLeq() {
		int[] array = new int[8];
		for (int i = 0; i <= 8; i++) {
			array[i] = i;
		}
	}

	@Test
	public void _testUnsafeLoopUpperLeq() {
		assertAnalysis("testUnsafeLoopUpperLeq");
	}

	public static void testUnsafeLoopUpperLt() {
		int[] array = new int[8];
		for (int i = 0; i < 9; i++) {
			array[i] = i;
		}
	}

	@Test
	public void _testUnsafeLoopUpperLt() {
		assertAnalysis("testUnsafeLoopUpperLt");
	}

	public static void testSafeLoopNe() {
		int[] array = new int[8];
		for (int i = 7; i != -1; i--) {
			array[i] = i;
		}
	}

	@Test
	public void _testSafeLoopNe() {
		assertAnalysis("testSafeLoopNe");
	}

	public static void testUnsafeLoopLowerNe() {
		int[] array = new int[8];
		for (int i = 7; i != -2; i--) {
			array[i] = i;
		}
	}

	@Test
	public void _testUnsafeLoopLowerNe() {
		assertAnalysis("testUnsafeLoopLowerNe");
	}

	public static void testSafeLoopGt() {
		int[] array = new int[8];
		for (int i = 7; i > -1; i--) {
			array[i] = i;
		}
	}

	@Test
	public void _testSafeLoopGt() {
		assertAnalysis("testSafeLoopGt");
	}

	public static void testUnsafeLoopLowerGt() {
		int[] array = new int[8];
		for (int i = 7; i > -2; i--) {
			array[i] = i;
		}
	}

	@Test
	public void _testUnsafeLoopLowerGt() {
		assertAnalysis("testUnsafeLoopLowerGt");
	}

	public static void testSafeLoopGe() {
		int[] array = new int[8];
		for (int i = 7; i >= 0; i--) {
			array[i] = i;
		}
	}

	@Test
	public void _testSafeLoopGe() {
		assertAnalysis("testSafeLoopGe");
	}

	public static void testUnsafeLoopLowerGe() {
		int[] array = new int[8];
		for (int i = 7; i >= -1; i--) {
			array[i] = i;
		}
	}

	@Test
	public void _testUnsafeLoopLowerGe() {
		assertAnalysis("testUnsafeLoopLowerGe");
	}

	public static void testUnsafeLoopLength() {
		int[] array = new int[8];
		for (int i = 0; i <= array.length; i++) {
			array[i] = i;
		}
	}

	@Test
	public void _testUnsafeLoopLength() {
		assertAnalysis("testUnsafeLoopLength");
	}

}
