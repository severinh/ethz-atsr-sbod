import org.junit.Test;

public class PointerAnalysisTests extends AbstractTest {

	public static void main(String[] args) {
		testSafeA5Const();
		testSafeA5Branch();
		testUnsafeA5Const();
		testUnsafeA5Branch();
		
		testSafeA5VarBottom();
		testSafeA5VarBranch();
		testSafeA5VarConst();
		testUnsafeA5VarConst();
		testUnsafeA5VarBranch();
		
		testSafeSomeArrayEasy();
		testUnsafeSomeArrayEasy();
		
		testSafeNonC();
		testUnsafeNonC();
		
		testUnsafeForeignArrayLength();
		testUnsafeArrayMightBeEmpty();
	}

	public static int[] allocAnyArray() {
		int t = getAnyInt();
		if(0 <= t) {
			return new int[t];
		} else {
			return new int[4];
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	// Simple allocation function
	
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
	
	//////////////////////////////////////////////////////////////////////////////////
	// Allocation function using a variable (very simple)
	
	public static void testSafeA5VarConst() {
		int[] a = allocSize5VarArray();
		a[4] = 3;
		
		int[] b = allocSize5VarArray();
		b[4] = 7;
	}
	
	@Test
	public void _testSafeA5VarConst() {
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
	public void _testSafeA5VarBranch() {
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

	/////////////////////////////////////////////////////////////////////////////////////////
	// Allocation method that creates an array with a size in an interval (easy)
	
	public static int[] allocSomeArrayEasy() {
		int t = getAnyInt();
		if(5 <= t && t <= 10) {
			return new int[t];
		} else {
			return new int[7];
		}
	}
	
	public static void testSafeSomeArrayEasy() {
		int[] a = allocSomeArrayEasy();
		int i = getAnyInt();
		if(2 <= i && i < 5-1){
			a[i] = 7;
			a[i+1] = 8;
		} else if(i == 5) {
			a[4] = 9;
		}
	}
	
	@Test
	public void _testSafeSomeArrayEasy() {
		assertAnalysis("testSafeSomeArrayEasy");
	}
	
	public static void testUnsafeSomeArrayEasy() {
		int[] a = allocSomeArrayEasy();
		int i = getAnyInt();
		if(2 <= i && i < 5){
			a[i] = 7;
			a[i+1] = 8;
		}
	}
	
	@Test
	public void _testUnsafeSomeArrayEasy() {
		assertAnalysis("testUnsafeSomeArrayEasy");
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	// Allocation method that creates an array with a size in an interval (non-contiguous)
	
	public static int[] allocNonC() {
		int t = getAnyInt();
		if(20 <= t && t <= 30) {
			return new int[t];
		} else if(5 < t && t < 10) {
			return new int[t];
		} else {
			return new int[255];
		}
	}
	
	public static void testSafeNonC() {
		int[] a = allocNonC();
		int i = getAnyInt();
		if(2 <= i && i < 6){
			a[i] = 77;
		}
	}
	
	@Test
	public void _testSafeNonC(){
		assertAnalysis("testSafeNonC");
	}
	
	public static void testUnsafeNonC() {
		int[] a = allocNonC();
		int t = getAnyInt();
		if(5 < t && t < 10){
			a[t] = 88;
		}
	}
	
	@Test
	public void _testUnsafeNonC(){
		assertAnalysis("testUnsafeNonC");
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// array.length is top for extra-procedurally (yes, that is a word now) allocated arrays
	
	public static void testUnsafeForeignArrayLength() {
		int[] a = allocAnyArray();
		a[a.length-1] = 7; 	// If we wanted to support this without inter-procedural analysis of 
							// intervals, we'd have to introduce some kind of symbolic representation
							// of array length fields. Things would get hairy very fast. So we didn't.
	}
	
	@Test 
	public void _testUnsafeForeignArrayLength() {
		assertAnalysis("testUnsafeForeignArrayLength");
	}
	
	public static void testUnsafeArrayMightBeEmpty() {
		int[] a = allocAnyArray();
		a[0] = 7;
	}
	
	@Test
	public void _testUnsafeArrayMightBeEmpty() {
		assertAnalysis("testUnsafeArrayMightBeEmpty");
	}
}
