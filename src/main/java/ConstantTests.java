import org.junit.Test;

public class ConstantTests extends AbstractTest {

	public static void main(String[] args) {
		testSafeAccesses();
		testUnsafeWrites1();
		testUnsafeWrites2();
		testUnsafeReads1();
		testUnsafeReads2();
	}

	public static void testSafeAccesses() {
		int[] array = new int[2];
		array[0] = 0;
		array[1] = 1;
		System.out.println(array[0] + array[1]);
	}

	@Test
	public void _testSafeAccesses() {
		assertSafe("testSafeAccesses");
	}

	public static void testUnsafeWrites1() {
		int[] array = new int[2];
		array[-1] = 0;
		System.out.println(array[0]);
	}

	@Test
	public void _testUnsafeWrites1() {
		assertMaybeUnsafe("testUnsafeWrites1");
	}

	public static void testUnsafeWrites2() {
		int[] array = new int[2];
		array[2] = 1;
		System.out.println(array[0]);
	}

	@Test
	public void _testUnsafeWrites2() {
		assertMaybeUnsafe("testUnsafeWrites2");
	}

	public static void testUnsafeReads1() {
		int[] array = new int[2];
		System.out.println(array[-1]);
	}

	@Test
	public void _testUnsafeReads1() {
		assertMaybeUnsafe("testUnsafeReads1");
	}

	public static void testUnsafeReads2() {
		int[] array = new int[2];
		System.out.println(array[2]);
	}

	@Test
	public void _testUnsafeReads2() {
		assertMaybeUnsafe("testUnsafeReads2");
	}

}
