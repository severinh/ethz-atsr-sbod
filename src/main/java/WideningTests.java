import org.junit.Test;

public class WideningTests extends AbstractTest {

	public static void main(String[] args) {
		testSimple();
	}

	@Safe
	public static void testSimple() {
		int n = 100000;
		int[] array = new int[n];
		for (int i = 0; i < n; i++) {
			System.out.println(array[i]);
		}
	}

	@Test
	public void _testSimple() {
		assertAnalysis("testSimple");
	}

}
