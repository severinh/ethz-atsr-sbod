import org.junit.Test;

/**
 * Holds tests where the array sizes and indices are arithmetic expressions.
 * There are no control flow branches in the test methods.
 */
public class ArithmeticTests extends AbstractTest {

	public static void main(String[] args) {
		testSafeComputedIndices();
		testUnsafeComputedIndicesPlus();
		testUnsafeComputedIndicesSub();
		testUnsafeComputedIndicesMul();
		testSafeLeftShift();
	}

	@Safe
	public static void testSafeComputedIndices() {
		int[] array = new int[8];
		int minusOne = -1;
		int one = 1;
		int two = 2;
		int three = 3;
		int six = 6;
		int eight = 8;
		array[one + six] = 1;
		System.out.println(array[one + six]);
		array[eight - one] = 2;
		System.out.println(array[eight - one]);
		array[-minusOne] = 3;
		System.out.println(array[-minusOne]);
		array[two * three] = 3;
		System.out.println(array[two * three]);
	}

	@Test
	public void _testSafeComputedIndices() {
		assertAnalysis("testSafeComputedIndices");
	}

	@Unsafe
	public static void testUnsafeComputedIndicesPlus() {
		int[] array = new int[8];
		int one = 1;
		int seven = 7;
		array[one + seven] = 1;
		System.out.println(array[0]);
	}

	@Test
	public void _testUnsafeComputedIndicesPlus() {
		assertAnalysis("testUnsafeComputedIndicesPlus");
	}

	@Unsafe
	public static void testUnsafeComputedIndicesNeg() {
		int[] array = new int[8];
		int one = 1;
		array[-one] = 1;
		System.out.println(array[0]);
	}

	@Test
	public void _testUnsafeComputedIndicesNeg() {
		assertAnalysis("testUnsafeComputedIndicesNeg");
	}

	@Unsafe
	public static void testUnsafeComputedIndicesSub() {
		int[] array = new int[8];
		int nine = 9;
		int one = 1;
		array[nine - one] = 1;
		System.out.println(array[0]);
	}

	@Test
	public void _testUnsafeComputedIndicesSub() {
		assertAnalysis("testUnsafeComputedIndicesSub");
	}

	@Unsafe
	public static void testUnsafeComputedIndicesSub2() {
		int[] array = new int[8];
		int eight = 8;
		int nine = 9;
		array[eight - nine] = 1;
		System.out.println(array[0]);
	}

	@Test
	public void _testUnsafeComputedIndicesSub2() {
		assertAnalysis("testUnsafeComputedIndicesSub2");
	}

	@Unsafe
	public static void testUnsafeComputedIndicesMul() {
		int[] array = new int[8];
		int two = 2;
		int four = 4;
		array[two * four] = 1;
		System.out.println(array[0]);
	}

	@Test
	public void _testUnsafeComputedIndicesMul() {
		assertAnalysis("testUnsafeComputedIndicesMul");
	}

	@Safe
	public static void testSafeLeftShift() {
		int[] array = new int[8];
		int index = 0;

		index = 3;
		index = index << 1; // index == 1
		System.out.println(array[index]);

		index = 1;
		index = index << 2; // index == 4
		System.out.println(array[index]);

		index = -1;
		index = index << 2; // index == -4
		index *= -1; // index == 4
		System.out.println(array[index]);
	}

	@Test
	public void _testSafeLeftShift() {
		assertAnalysis("testSafeLeftShift");
	}

}
