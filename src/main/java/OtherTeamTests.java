import org.junit.Test;

public class OtherTeamTests extends AbstractTest {

	public static void main(String[] a) {
		new OtherTeamTests().test1();
	}

	@Safe
	public void test1() {
		int[] a = new int[2];
		int i = 1;
		System.out.println(a[i]);
		test2();
		test3();
		test4();
		test5();
	}

	@Test
	public void _test1() {
		assertAnalysis("test1");
	}

	@Unsafe
	public void test2() {
		int i = 2;
		int[] a = new int[i];
		System.out.println(a[i]);
	}

	@Test
	public void _test2() {
		assertAnalysis("test2");
	}

	@Safe
	public void test3() {
		int i = 2;
		int[] a = new int[i];
		int j = 0 * a[0];
		System.out.println(a[j]);
	}

	@Test
	public void _test3() {
		assertAnalysis("test3");
	}

	@Safe
	public void test4() {
		int i = 2;
		if (i == 3) {
			i = 1;
		} else {
			i = 4;
		}
		int[] a = new int[i];
		System.out.println(a[i - 1]);
	}

	@Test
	public void _test4() {
		assertAnalysis("test4");
	}

	@Unsafe
	public void test5() {
		int[] a = new int[0];
		System.out.println(a[0]);
	}

	@Test
	public void _test5() {
		assertAnalysis("test5");
	}

	@Unsafe
	public void test6() {
		int[] a = new int[1];
		int i = 0;
		i = Math.max(i, 5);
		System.out.println(a[i]);
	}

	@Test
	public void _test6() {
		assertAnalysis("test6");
	}

	@Safe
	public void test7() {
		int[] a = new int[5];
		a = new int[8];
		System.out.println(a[7]);
	}

	@Test
	public void _test7() {
		assertAnalysis("test7");
	}

	@Unsafe
	public void test8(boolean b) {
		int[] a = new int[5];
		if (b) {
			a = new int[8];
		}
		System.out.println(a[7]);
	}

	@Test
	public void _test8() {
		assertAnalysis("test8");
	}

	@Safe
	public void test9(boolean b) {
		int[] a = new int[5];
		if (b) {
			a = new int[8];
		}
		System.out.println(a[4]);
	}

	@Test
	public void _test9() {
		assertAnalysis("test9");
	}

	@Safe
	public void test10(boolean b) {
		int[] a = new int[5];
		int i = 2;
		int j = 1;
		System.out.println(a[j << i]);
	}

	@Test
	public void _test10() {
		assertAnalysis("test10");
	}

	@Safe
	public void test11(boolean b) {
		int[] a = new int[5];
		int i = 3;
		System.out.println(a[i / i]);
	}

	@Test
	public void _test11() {
		assertAnalysis("test11");
	}

	@Safe
	public void test12(boolean b) {
		int[] a = new int[5];
		int i = 3;
		int j = 2;
		System.out.println(a[b ? i : j]);
	}

	@Test
	public void _test12() {
		assertAnalysis("test12");
	}

	@Unsafe
	public void test13(boolean b) {
		int[] a = new int[3];
		int i = 3;
		int j = 2;
		System.out.println(a[b ? i : j]);
	}

	@Test
	public void _test13() {
		assertAnalysis("test13");
	}

	@Safe
	public void test14(boolean b) {
		int[] a = new int[5];
		int j = -1;
		System.out.println(a[~j]);
	}

	@Test
	public void _test14() {
		assertAnalysis("test14");
	}

	@Safe
	public void test15(boolean b) {
		int n = 100000;
		int[] a = new int[n];
		for (int i = 0; i <= n - 1; i++) {
			System.out.println(a[i]);
		}
	}

	@Test
	public void _test15() {
		assertAnalysis("test15");
	}

	@Safe
	public void test16(boolean b) {
		int[] a = new int[10];
		int n = a[0];
		if (b && n < 10 && n >= 0) {
			System.out.println(a[n]);
		}
	}

	@Test
	public void _test16() {
		assertAnalysis("test16");
	}

	@Unsafe
	public void test17(boolean b) {
		int[] a = new int[10];
		int n = a[0];
		if (b && n < 10) {
			System.out.println(a[n]);
		}
	}

	@Test
	public void _test17() {
		assertAnalysis("test17");
	}

	@Safe
	public void test18(boolean b) {
		int n = 100000;
		int[] a = new int[n];
		for (int i = 0; i < n; i++) {
			System.out.println(a[i]);
		}
	}

	@Test
	public void _test18() {
		assertAnalysis("test18");
	}

	@Safe
	public void test19(boolean b) {
		int n = 100000;
		int[] a = new int[n];
		int t = a[0];
		if (t <= a.length - 1 && t >= 0) {
			System.out.println(a[t]);
		}
	}

	@Test
	public void _test19() {
		assertAnalysis("test19");
	}

	@Safe
	public void test20(boolean b) {
		int n = 100000;
		int[] a = new int[n];
		System.out.println(a[a.length - 10]);
	}

	@Test
	public void _test20() {
		assertAnalysis("test20");
	}

	@Safe
	public void test21(int i) {
		int[] a = new int[3];
		int j = -1;
		if (i < Integer.MIN_VALUE) {
			j = a[j];
		} else {
			j = 0;
		}

		System.out.println(a[j]);
	}

	@Test
	public void _test21() {
		assertAnalysis("test21");
	}
}
