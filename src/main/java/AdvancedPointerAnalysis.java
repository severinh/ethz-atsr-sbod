import org.junit.Test;

public class AdvancedPointerAnalysis extends AbstractTest {
	public static void main(String[] args) {
		testSafeId();
		testUnsafeId1();
		testUnsafeId2();
		testUnsafeId3();
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
}
