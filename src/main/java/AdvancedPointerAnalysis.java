import org.junit.Test;

public class AdvancedPointerAnalysis extends AbstractTest {
	public static void main(String[] args) {
		testSafeId();
		testUnsafeId1();
		testUnsafeId2();
		testUnsafeId3();
		
		testSafeCountBackwards();
		testUnsafeCountBackwards();
		testUnsafeExchange();
		testUnsafeFlipFlop0();
		testSafeExchange();
		
		testSafeAccessInCall();
		testUnsafeAccessInCall();
		testUnsafeAccessExpression();
		
		testUnsafeCellCouldBeNull();
	}
	
	// We need a separate identity function for each test case, because
	// otherwise pointer-analysis will coalesce all input arrays to
	// id to one pointer set.
	
	public static int[] id0(int[] xs) {
		return xs;
	}
	
	public static int[] id1(int[] xs) {
		return xs;
	}
	
	public static int[] id2(int[] xs) {
		return xs;
	}
	
	public static int[] id3(int[] xs) {
		return xs;
	}
	
	public static int[] id4(int[] xs) {
		return xs;
	}
	
	@Safe
	public static void testSafeId() {
		int s = getAnyInt();
		int K = getAnyInt();
		if(16 < K && K < 1025){
			if( 1024*1024 < s && s <= 1024*1024+K){
				int[] a = new int[s];
				int[] b = id0(a);
				int i = getAnyInt();
				if(0 <= i && i < K*2*512) {
					b[i] = i*s;
				}
			}
		}
	}
	
	@Test
	public void _testSafeId() {
		assertAnalysis("testSafeId");
	}
	
	@Unsafe
	public static void testUnsafeId1() {
		int s = getAnyInt();
		int K = getAnyInt();
		if(16 < K && K < 1025){
			if(0 < s && s < K*1024){
				int[] a = new int[s];
				int[] b = id1(a);
				int i = getAnyInt();
				if(0 <= i && i < K*2*512) {
					b[i] = i*s;
				}
			}
		}
	}
	
	@Test
	public void _testUnsafeId1() {
		assertAnalysis("testUnsafeId1");
	}
	
	@Unsafe
	public static void testUnsafeId2() {
		int s = getAnyInt();
		int K = getAnyInt();
		if(16 < K && K < 1025){
			if(0 < s && s <= 1024*1024){
				int[] a = new int[s];
				int[] b = id2(a);
				int i = getAnyInt();
				if(0 <= i && i < K*K) {
					b[i] = i*s;
				}
			}
		}
	}
	
	@Test
	public void _testUnsafeId2() {
		assertAnalysis("testUnsafeId2");
	}
	
	@Unsafe
	public static void testUnsafeId3() {
		int s = getAnyInt();
		int K = getAnyInt();
		if(16 < K && K < 1025){
			if(0 < s && s < K*1024){
				int[] a = new int[s];
				int[] b = id3(a);
				int[] c = id4(b);
				int i = getAnyInt();
				if(K <= i && i < K*K) {
					c[i] = i*s;
				}
			}
		}
	}
	
	@Test
	public void _testUnsafeId3() {
		assertAnalysis("testUnsafeId3");
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void cellExchange0(Cell0 a, Cell0 b) {
		int[] t;
		t = a.f;
		a.f = b.get();
		b.set(t);
	}
	
	public static void cellExchange2(Cell2 a, Cell2 b) {
		int[] t;
		t = a.f;
		a.f = b.get();
		b.set(t);
	}
	
	public static void cellExchange3(Cell3 a, Cell3 b) {
		int[] t;
		t = a.f;
		a.f = b.get();
		b.set(t);
	}
	
	public static int[] id5(int[] xs) {
		return xs;
	}
	
	@Safe
	public static void testSafeExchange() {
		Cell0 a = new Cell0(new int[32]);
		Cell0 b = new Cell0(new int[32]);
		b.f = new int[16];
		
		cellExchange0(a,b);
		
		a.f[15] = 4;
	}
	
	@Test
	public void _testSafeExchange() {
		assertAnalysis("testSafeExchange");
	}
	
	@Unsafe
	public static void testUnsafeExchange() {
		Cell2 a = new Cell2(new int[32]);
		Cell2 b = new Cell2(new int[32]);
		b.f = new int[16];
		
		// There is nothing wrong with this program, but due to
		// the way the abstraction in points-to-analysis works
		// we coalesce the points-to-sets for a.f and b.f into one
		
		cellExchange2(a,b);
		
		a.f[15] = 4;
		
		cellExchange2(a,b);
		
		a.get()[31] = 3;
	}
	
	@Test
	public void _testUnsafeExchange() {
		assertAnalysis("testUnsafeExchange");
	}
	
	@Unsafe
	public static void testUnsafeCountBackwards() {
		// Again, nothing wrong with this program
		// Our analysis is simply not precise enough
		for(int i = 10; i > 0; i--) {
			int[] a = id5(new int[i]);
			a[i-1] = 3;
		}
	}
	
	@Test
	public void _testUnsafeCountBackwards() {
		assertAnalysis("testUnsafeCountBackwards");
	}
	
	@Safe
	public static void testSafeCountBackwards() {
		for(int i = 10; i > 0; i--) {
			int[] a = id5(new int[i]);
			a[0] = 3;
		}
	}
	
	@Test
	public void _testSafeCountBackwards() {
		assertAnalysis("testSafeCountBackwards");
	}
	
	@Unsafe
	public static void testUnsafeFlipFlop0() {
		Cell3 a = new Cell3(new int[32]);
		Cell3 b = new Cell3(new int[32]);
		for(int i = 10; i > 0; i--) {
			b.f = new int[i];
			cellExchange3(a, b);
			a.f[i-1] = 1;
		}
	}
	
	@Test
	public void _testUnsafeFlipFlop0() {
		assertAnalysis("testUnsafeFlipFlop0");
	}
	
	public static class CellNull {
		public CellNull(int[] f) {
			this.f = f;
		}
		
		public int[] f;

		public int[] get() {
			return f;
		}

		public void set(int[] f) {
			this.f = f;
		}
		
	}
	
	public static class Cell0 {
		public Cell0(int[] f) {
			this.f = f;
		}
		
		public int[] f;

		public int[] get() {
			return f;
		}

		public void set(int[] f) {
			this.f = f;
		}
		
	}
	
	public static class Cell2 {
		public Cell2(int[] f) {
			this.f = f;
		}
		
		public int[] f;

		public int[] get() {
			return f;
		}

		public void set(int[] f) {
			this.f = f;
		}
		
	}
	
	public static class Cell3 {
		public Cell3(int[] f) {
			this.f = f;
		}
		
		public int[] f;

		public int[] get() {
			return f;
		}

		public void set(int[] f) {
			this.f = f;
		}
		
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	public static void someMethod0(int a) {
		
	}
	
	@Safe
	public static void testSafeAccessInCall() {
		int[] a = new int[5];
		int x;
		someMethod0(a[4]);
		x = 4+a[4];
		someMethod0(x);
	}
	
	@Test
	public void _testSafeAccessInCall() {
		assertAnalysis("testSafeAccessInCall");
	}
	
	@Unsafe 
	public static void testUnsafeAccessInCall() {
		int[] a = new int[5];
		someMethod0(a[5]);
	}
	
	@Test
	public void _testUnsafeAccessInCall() {
		assertAnalysis("testUnsafeAccessInCall");
	}
	
	@Unsafe 
	public static void testUnsafeAccessExpression() {
		int[] a = new int[5];
		int x = 4+(3*(a[5]));
		someMethod0(x);
	}
	
	@Test
	public void _testUnsafeAccessExpression() {
		assertAnalysis("testUnsafeAccessExpression");
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////
	
	@Unsafe
	public static void testUnsafeCellCouldBeNull() {
		CellNull x = new CellNull(null);
		x.f[5] = 3;
	}
	
	@Test
	public void _testUnsafeCellCouldBeNull() {
		assertAnalysis("testUnsafeCellCouldBeNull");
	}
}
