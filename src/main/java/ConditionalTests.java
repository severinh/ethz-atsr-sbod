import org.junit.Test;

public class ConditionalTests extends AbstractTest {

	public static void main(String[] args) {
		testSafeDeadCode1();
		testSafeDeadCode2();
		testSafeDeadCode3();
		testSafeDeadCodeNested();
		testUnsafe();
		testSafeDeadCode4(getAnyInt());
	}

	@Safe
	public static void testSafeDeadCode1() {
		boolean flag = true;
		int[] array = new int[2];
		int index = 0;
		if (flag) {
			index = 1;
		} else {
			index = -1;
		}
		System.out.println(array[index]);
	}

	@Test
	public void _testSafeDeadCode1() {
		assertAnalysis("testSafeDeadCode1");
	}

	@Safe
	public static void testSafeDeadCode2() {
		int[] array = new int[2];
		int index = 0;
		int value = 42;
		int sameValue = 42;
		if (value <= sameValue) {
			index = 1;
		} else {
			index = -1;
		}
		System.out.println(array[index]);
	}

	@Test
	public void _testSafeDeadCode2() {
		assertAnalysis("testSafeDeadCode2");
	}

	@Safe
	public static void testSafeDeadCode3() {
		int[] array = new int[2];
		int index = 0;
		boolean flag = true;
		boolean otherFlag = true;
		if (flag && otherFlag) {
			index = 1;
		} else {
			index = -1;
		}
		System.out.println(array[index]);
	}

	@Test
	public void _testSafeDeadCode3() {
		assertAnalysis("testSafeDeadCode3");
	}

	@Safe
	public static void testSafeDeadCodeNested() {
		int[] array = new int[2];
		int index = 0;
		int value = 42;
		int sameValue = 42;
		if (value <= sameValue) {
			index = 1;
		} else {
			if (value <= sameValue) {
				index = -1;
			}
		}
		System.out.println(array[index]);
	}

	@Test
	public void _testSafeDeadCodeNested() {
		assertAnalysis("testSafeDeadCodeNested");
	}

	@Unsafe
	public static void testUnsafe() {
		int[] array = new int[2];
		// Flag could be either false or true
		boolean flag = getAnyInt() == 123;
		int index = 0;
		if (flag) {
			index = 1;
		} else {
			index = -1;
		}
		System.out.println(array[index]);
	}

	@Test
	public void _testUnsafe() {
		assertAnalysis("testUnsafe");
	}

	@Safe
	public static void testSafeDeadCode4(int i) {
		int[] array = new int[10];
		int index = -1;
		if (i < Integer.MIN_VALUE) {
			// This branch will never be taken, so the unsafe array access
			// should not be a reason for concern
			index = array[index];
		} else {
			index = 0;
		}
		System.out.println(array[index]);
	}

	@Test
	public void _testSafeDeadCode4() {
		assertAnalysis("testSafeDeadCode4");
	}

}
