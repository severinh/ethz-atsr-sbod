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
		assertSafe("testSafeDeadCode1");
	}

}
