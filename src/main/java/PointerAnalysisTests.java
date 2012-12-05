import org.junit.Test;

public class PointerAnalysisTests extends AbstractTest {

	public static void main(String[] args) {
		testSafeA5Const();
		testSafeA5Branch();
		testUnsafeA5Const();
		testUnsafeA5Branch();
	}

	public static int[] allocSize5Array() {
		return new int[5];
	}

	public static void testSafeA5Const() {
		int[] a = allocSize5Array();
		a[0] = 1;
		a[4] = 3;
	}

	@Test
	public void _testSafeA5Const() {
		assertAnalysis("testSafeA5Const");
	}

	public static void testSafeA5Branch() {
		int[] a = allocSize5Array();
		int t = getAnyInt();
		if (0 <= t && t < 5) {
			a[t] = 3;
		}
	}

	@Test
	public void _testSafeA5Branch() {
		assertAnalysis("testSafeA5Branch");
	}

	public static void testUnsafeA5Const() {
		int[] a = allocSize5Array();
		a[7] = 1;
	}

	@Test
	public void _testUnsafeA5Const() {
		assertAnalysis("testUnsafeA5Const");
	}

	public static void testUnsafeA5Branch() {
		int[] a = allocSize5Array();
		int t = getAnyInt();
		if (17 <= t && t < 20) {
			a[t] = 3;
		}
	}

	@Test
	public void _testUnsafeA5Branch() {
		assertAnalysis("testUnsafeA5Branch");
	}
	
	public static int[] allocSize5VarArray() {
		int s;
		if(getAnyInt() > 0) {
			s = 5;
		} else {
			s = 5;
		}
		
		return new int[s];
	}
	
	public static void testSafeA5VarConst() {
		int[] a = allocSize5VarArray();
		a[4] = 3;
		
		int[] b = allocSize5VarArray();
		b[4] = 7;
	}
	
	@Test
	public void _testA5VarConst() {
		assertAnalysis("testSafeA5VarConst");
	}
	
	public static void testSafeA5VarBranch() {
		int[] a = allocSize5VarArray();
		int t = getAnyInt();
		if(0 <= t && t < 5){
			a[t] = 3;
		}
	}
	
	@Test
	public void _testA5VarBranch() {
		assertAnalysis("testSafeA5VarBranch");
	}
	
	public static void testUnsafeA5VarBranch() {
		int[] a = allocSize5VarArray();
		int t = getAnyInt();
		if(5 <= t && t < 7){
			a[t] = 3;
		}
	}
	
	@Test
	public void _testUnsafeA5VarConst() {
		assertAnalysis("testUnsafeA5VarConst");
	}
	
	public static void testUnsafeA5VarConst() {
		int[] a = allocSize5VarArray();
		a[5] = 3;
	}
	
	@Test
	public void _testUnsafeA5VarBranch() {
		assertAnalysis("testUnsafeA5VarBranch");
	}
	
	public static void testSafeA5VarBottom() {
		int[] a = allocSize5VarArray();
		int t = getAnyInt();
		if(5 <= t && t < 5){
			a[t] = 3;
		}
	}
	
	@Test
	public void _testSafeA5VarBottom() {
		assertAnalysis("testSafeA5VarBottom");
	}

}
