import org.junit.Test;

public class PointerAnalysisTests extends AbstractTest {

	public static void main(String[] args) {
		testSafeA5Const();
		testSafeA5Branch();
	}

	public static int[] allocSize5Array() {
		return new int[5];
	}
	
	public static int topint() { return 0; }
	public static int topint2() { return 999999; }
	
	public static void testSafeA5Const() {
		int[] a = allocSize5Array();
		a[0] = 1;
		a[4] = 3;
	}
	
	@Test
	public void _testSafeA5Const() {
		assertSafe("testSafeA5Const");
	}
	
	public static void testSafeA5Branch() {
		int[] a = allocSize5Array();
		int t = topint();
		if(0 <= t && t < 5) {
			a[t] = 3;
		}
	}
	
	@Test
	public void _testSafeA5Branch() {
		assertSafe("testSafeA5Branch");
	}
	
	public static void testUnsafeA5Const() {
		int[] a = allocSize5Array();
		a[7] = 1;
	}
	
	@Test
	public void _testUnsafeA5Const() {
		assertMaybeUnsafe("testUnsafeA5Const");
	}
	
	public static void testUnsafeA5Branch() {
		int[] a = allocSize5Array();
		int t = topint2();
		if(17 <= t && t < 20) {
			a[t] = 3;
		}
	}
	
	@Test
	public void _testUnsafeA5Branch() {
		assertMaybeUnsafe("testUnsafeA5Branch");
	}

}
