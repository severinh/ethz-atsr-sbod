import org.junit.Test;

public class SimpleArithmeticTests extends AbstractTest {

	public static void main(String[] args) {
		testSafeComputedIndices();
	}

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
		assertSafe("testSafeComputedIndices");
	}

	public static void testUnsafeComputedIndices1() {
		int[] array = new int[8];
		int one = 1;
		int seven = 7;
		array[one + seven] = 1;
		System.out.println(array[0]);
	}

	@Test
	public void _testUnsafeComputedIndices1() {
		assertMaybeUnsafe("testUnsafeComputedIndices1");
	}

	public static void testUnsafeComputedIndices2() {
		int[] array = new int[8];
		int one = 1;
		array[-one] = 1;
		System.out.println(array[0]);
	}

	@Test
	public void _testUnsafeComputedIndices2() {
		assertMaybeUnsafe("testUnsafeComputedIndices2");
	}

	public static void testUnsafeComputedIndices3() {
		int[] array = new int[8];
		int nine = 9;
		int one = 1;
		array[nine - one] = 1;
		System.out.println(array[0]);
	}

	@Test
	public void _testUnsafeComputedIndices3() {
		assertMaybeUnsafe("testUnsafeComputedIndices3");
	}

	public static void testUnsafeComputedIndices4() {
		int[] array = new int[8];
		int eight = 8;
		int nine = 9;
		array[eight - nine] = 1;
		System.out.println(array[0]);
	}

	@Test
	public void _testUnsafeComputedIndices4() {
		assertMaybeUnsafe("testUnsafeComputedIndices4");
	}

	public static void testUnsafeComputedIndices5() {
		int[] array = new int[8];
		int two = 2;
		int four = 4;
		array[two * four] = 1;
		System.out.println(array[0]);
	}

	@Test
	public void _testUnsafeComputedIndices5() {
		assertMaybeUnsafe("testUnsafeComputedIndices5");
	}

}
