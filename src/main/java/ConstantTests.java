import org.junit.Test;

/**
 * Holds tests where the array sizes and indices are simple integer constants.
 * There are no control flow branches in the test methods.
 */
public class ConstantTests extends AbstractTest {

	public static void main(String[] args) {
		testSafeAccesses();
		testUnsafeWrites1();
		testUnsafeWrites2();
		testUnsafeReads1();
		testUnsafeReads2();
	}

	@Safe
	public static void testSafeAccesses() {
		int[] array = new int[2];
		array[0] = 0;
		array[1] = 1;
		System.out.println(array[0] + array[1]);
	}

	@Test
	public void _testSafeAccesses() {
		assertAnalysis("testSafeAccesses");
	}

	@Unsafe
	public static void testUnsafeWrites1() {
		int[] array = new int[2];
		array[-1] = 0;
		System.out.println(array[0]);
	}

	@Test
	public void _testUnsafeWrites1() {
		assertAnalysis("testUnsafeWrites1");
	}

	@Unsafe
	public static void testUnsafeWrites2() {
		int[] array = new int[2];
		array[2] = 1;
		System.out.println(array[0]);
	}

	@Test
	public void _testUnsafeWrites2() {
		assertAnalysis("testUnsafeWrites2");
	}

	@Unsafe
	public static void testUnsafeReads1() {
		int[] array = new int[2];
		System.out.println(array[-1]);
	}

	@Test
	public void _testUnsafeReads1() {
		assertAnalysis("testUnsafeReads1");
	}

	@Unsafe
	public static void testUnsafeReads2() {
		int[] array = new int[2];
		System.out.println(array[2]);
	}

	@Test
	public void _testUnsafeReads2() {
		assertAnalysis("testUnsafeReads2");
	}

}
