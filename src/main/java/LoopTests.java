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
		assertSafe("testSafeLoop");
	}

}
