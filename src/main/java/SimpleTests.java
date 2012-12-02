import org.junit.Test;

public class SimpleTests extends AbstractTest {

	public static void main(String[] args) {

	}

	public void testSafeAccesses() {
		int[] array = new int[2];
		array[0] = 0;
		array[1] = 1;
		System.out.println("First element: " + array[0]);
		System.out.println("Second element: " + array[1]);
	}

	@Test
	public void _testSafeAccesses() {
		assertSafe("testSafeAccesses");
	}

}
