import org.junit.Test;

public class ConditionalTests extends AbstractTest {

	public static void main(String[] args) {
		testSafeDeadCode1();
	}

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

	public static void testUnsafe() {
		int[] array = new int[2];
		// Flag could be either false or true
		boolean flag = array.hashCode() == 123;
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

}
