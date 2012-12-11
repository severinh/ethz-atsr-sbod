import org.junit.Test;

public class SoftDeadlineTests extends AbstractTest {

	@Safe
	public static void testPreciseEqual1() {
		int[] a = new int[5];
		int x = 1;
		if (x != 1) {
			a[128] = 1;
		}
	}

	@Test
	public void _testPreciseEqual1() {
		assertAnalysis("testPreciseEqual1");
	}

	@Safe
	public static void testPreciseEqual2() {
		int[] a = new int[5];
		for (int i = 0; i <= 5; ++i) {
			if (i != 5) {
				a[i] = 1;
			}
		}
	}

	@Test
	public void _testPreciseEqual2() {
		assertAnalysis("testPreciseEqual2");
	}

	@Safe
	public static void testPreciseEqual3() {
		int[] a = new int[5];
		for (int i = 0; i <= 5; ++i) {
			if (i != 0) {
				a[i - 1] = 1;
			}
		}
	}

	@Test
	public void _testPreciseEqual3() {
		assertAnalysis("testPreciseEqual3");
	}

	// Test method with OK array accesses
	@Safe
	public static void testO_OK_0() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			ptr[x1] = 1;
		}
	}

	@Test
	public void _testO_OK_0() {
		assertAnalysis("testO_OK_0");
	}

	// Test method with AboveEnd array accesses
	@Unsafe
	public static void testO_AboveEnd_1() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			int index2 = x1 + 1;
			ptr[index2] = 1;
		}
	}

	@Test
	public void _testO_AboveEnd_1() {
		assertAnalysis("testO_AboveEnd_1");
	}

	// Test method with Negative array accesses
	@Unsafe
	public static void testO_Negative_2() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			int index2 = x1 + -1;
			ptr[index2] = 1;
		}
	}

	@Test
	public void _testO_Negative_2() {
		assertAnalysis("testO_Negative_2");
	}

	// Test method with NegativeAboveEnd array accesses
	@Unsafe
	public static void testO_NegativeAboveEnd_3() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			int index2 = 0;
			if (x1 == 0) {
				index2 = -1;
			}
			if (x1 == 7) {
				index2 = 8;
			}
			if (x1 == 6) {
				index2 = 8;
			}
			if (x1 == -1) {
				index2 = -2;
			}
			if (x1 == 8) {
				index2 = 9;
			}
			ptr[index2] = 1;
		}
	}

	@Test
	public void _testO_NegativeAboveEnd_3() {
		assertAnalysis("testO_NegativeAboveEnd_3");
	}

	// Test method with OK array accesses
	@Safe
	public static void testOL_OK_4() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				int index4 = 0;
				if (y3 == 0) {
					index4 = 0;
				}
				if (y3 == 49) {
					index4 = 7;
				}
				if (y3 == 36) {
					index4 = 7;
				}
				if (y3 == -1) {
					index4 = -1;
				}
				if (y3 == 50) {
					index4 = 8;
				}
				ptr[index4] = 1;
			}
		}
	}

	@Test
	public void _testOL_OK_4() {
		assertAnalysis("testOL_OK_4");
	}

	// Test method with AboveEnd array accesses
	@Unsafe
	public static void testOL_AboveEnd_5() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				int index4 = 0;
				if (y3 == 0) {
					index4 = 1;
				}
				if (y3 == 49) {
					index4 = 8;
				}
				if (y3 == 36) {
					index4 = 8;
				}
				if (y3 == -1) {
					index4 = 0;
				}
				if (y3 == 50) {
					index4 = 9;
				}
				ptr[index4] = 1;
			}
		}
	}

	@Test
	public void _testOL_AboveEnd_5() {
		assertAnalysis("testOL_AboveEnd_5");
	}

	// Test method with Negative array accesses
	@Unsafe
	public static void testOL_Negative_6() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				int index4 = 0;
				if (y3 == 0) {
					index4 = -1;
				}
				if (y3 == 49) {
					index4 = 6;
				}
				if (y3 == 36) {
					index4 = 6;
				}
				if (y3 == -1) {
					index4 = -2;
				}
				if (y3 == 50) {
					index4 = 7;
				}
				ptr[index4] = 1;
			}
		}
	}

	@Test
	public void _testOL_Negative_6() {
		assertAnalysis("testOL_Negative_6");
	}

	// Test method with NegativeAboveEnd array accesses
	@Unsafe
	public static void testOL_NegativeAboveEnd_7() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				int index4 = 0;
				if (y3 == 0) {
					index4 = -1;
				}
				if (y3 == 49) {
					index4 = 8;
				}
				if (y3 == 36) {
					index4 = 8;
				}
				if (y3 == -1) {
					index4 = -2;
				}
				if (y3 == 50) {
					index4 = 9;
				}
				ptr[index4] = 1;
			}
		}
	}

	@Test
	public void _testOL_NegativeAboveEnd_7() {
		assertAnalysis("testOL_NegativeAboveEnd_7");
	}

	// Test method with OK array accesses
	@Safe
	public static void testOLO_OK_8() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				for (int i4 = 0; i4 < 8; ++i4) {
					int x5 = i4 | y3;
					// x5 = [0,55] (checked values 0 55 38)
					int index6 = 0;
					if (x5 == 0) {
						index6 = 0;
					}
					if (x5 == 55) {
						index6 = 7;
					}
					if (x5 == 38) {
						index6 = 7;
					}
					if (x5 == -1) {
						index6 = -1;
					}
					if (x5 == 56) {
						index6 = 8;
					}
					ptr[index6] = 1;
				}
			}
		}
	}

	@Test
	public void _testOLO_OK_8() {
		assertAnalysis("testOLO_OK_8");
	}

	// Test method with AboveEnd array accesses
	@Unsafe
	public static void testOLO_AboveEnd_9() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				for (int i4 = 0; i4 < 8; ++i4) {
					int x5 = i4 | y3;
					// x5 = [0,55] (checked values 0 55 38)
					int index6 = 0;
					if (x5 == 0) {
						index6 = 1;
					}
					if (x5 == 55) {
						index6 = 8;
					}
					if (x5 == 38) {
						index6 = 8;
					}
					if (x5 == -1) {
						index6 = 0;
					}
					if (x5 == 56) {
						index6 = 9;
					}
					ptr[index6] = 1;
				}
			}
		}
	}

	@Test
	public void _testOLO_AboveEnd_9() {
		assertAnalysis("testOLO_AboveEnd_9");
	}

	// Test method with Negative array accesses
	@Unsafe
	public static void testOLO_Negative_10() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				for (int i4 = 0; i4 < 8; ++i4) {
					int x5 = i4 | y3;
					// x5 = [0,55] (checked values 0 55 38)
					int index6 = 0;
					if (x5 == 0) {
						index6 = -1;
					}
					if (x5 == 55) {
						index6 = 6;
					}
					if (x5 == 38) {
						index6 = 6;
					}
					if (x5 == -1) {
						index6 = -2;
					}
					if (x5 == 56) {
						index6 = 7;
					}
					ptr[index6] = 1;
				}
			}
		}
	}

	@Test
	public void _testOLO_Negative_10() {
		assertAnalysis("testOLO_Negative_10");
	}

	// Test method with NegativeAboveEnd array accesses
	@Unsafe
	public static void testOLO_NegativeAboveEnd_11() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				for (int i4 = 0; i4 < 8; ++i4) {
					int x5 = i4 | y3;
					// x5 = [0,55] (checked values 0 55 38)
					int index6 = 0;
					if (x5 == 0) {
						index6 = -1;
					}
					if (x5 == 55) {
						index6 = 8;
					}
					if (x5 == 38) {
						index6 = 8;
					}
					if (x5 == -1) {
						index6 = -2;
					}
					if (x5 == 56) {
						index6 = 9;
					}
					ptr[index6] = 1;
				}
			}
		}
	}

	@Test
	public void _testOLO_NegativeAboveEnd_11() {
		assertAnalysis("testOLO_NegativeAboveEnd_11");
	}

	// Test method with OK array accesses
	@Safe
	public static void testOLOO_OK_12() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				for (int i4 = 0; i4 < 8; ++i4) {
					int x5 = i4 | y3;
					// x5 = [0,55] (checked values 0 55 38)
					for (int i6 = 0; i6 < 8; ++i6) {
						int x7 = i6 | x5;
						// x7 = [0,55] (checked values 0 55 38)
						int index8 = 0;
						if (x7 == 0) {
							index8 = 0;
						}
						if (x7 == 55) {
							index8 = 7;
						}
						if (x7 == 38) {
							index8 = 7;
						}
						if (x7 == -1) {
							index8 = -1;
						}
						if (x7 == 56) {
							index8 = 8;
						}
						ptr[index8] = 1;
					}
				}
			}
		}
	}

	@Test
	public void _testOLOO_OK_12() {
		assertAnalysis("testOLOO_OK_12");
	}

	// Test method with AboveEnd array accesses
	@Unsafe
	public static void testOLOO_AboveEnd_13() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				for (int i4 = 0; i4 < 8; ++i4) {
					int x5 = i4 | y3;
					// x5 = [0,55] (checked values 0 55 38)
					for (int i6 = 0; i6 < 8; ++i6) {
						int x7 = i6 | x5;
						// x7 = [0,55] (checked values 0 55 38)
						int index8 = 0;
						if (x7 == 0) {
							index8 = 1;
						}
						if (x7 == 55) {
							index8 = 8;
						}
						if (x7 == 38) {
							index8 = 8;
						}
						if (x7 == -1) {
							index8 = 0;
						}
						if (x7 == 56) {
							index8 = 9;
						}
						ptr[index8] = 1;
					}
				}
			}
		}
	}

	@Test
	public void _testOLOO_AboveEnd_13() {
		assertAnalysis("testOLOO_AboveEnd_13");
	}

	// Test method with Negative array accesses
	@Unsafe
	public static void testOLOO_Negative_14() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				for (int i4 = 0; i4 < 8; ++i4) {
					int x5 = i4 | y3;
					// x5 = [0,55] (checked values 0 55 38)
					for (int i6 = 0; i6 < 8; ++i6) {
						int x7 = i6 | x5;
						// x7 = [0,55] (checked values 0 55 38)
						int index8 = 0;
						if (x7 == 0) {
							index8 = -1;
						}
						if (x7 == 55) {
							index8 = 6;
						}
						if (x7 == 38) {
							index8 = 6;
						}
						if (x7 == -1) {
							index8 = -2;
						}
						if (x7 == 56) {
							index8 = 7;
						}
						ptr[index8] = 1;
					}
				}
			}
		}
	}

	@Test
	public void _testOLOO_Negative_14() {
		assertAnalysis("testOLOO_Negative_14");
	}

	// Test method with NegativeAboveEnd array accesses
	@Unsafe
	public static void testOLOO_NegativeAboveEnd_15() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				for (int i4 = 0; i4 < 8; ++i4) {
					int x5 = i4 | y3;
					// x5 = [0,55] (checked values 0 55 38)
					for (int i6 = 0; i6 < 8; ++i6) {
						int x7 = i6 | x5;
						// x7 = [0,55] (checked values 0 55 38)
						int index8 = 0;
						if (x7 == 0) {
							index8 = -1;
						}
						if (x7 == 55) {
							index8 = 8;
						}
						if (x7 == 38) {
							index8 = 8;
						}
						if (x7 == -1) {
							index8 = -2;
						}
						if (x7 == 56) {
							index8 = 9;
						}
						ptr[index8] = 1;
					}
				}
			}
		}
	}

	@Test
	public void _testOLOO_NegativeAboveEnd_15() {
		assertAnalysis("testOLOO_NegativeAboveEnd_15");
	}

	// Test method with OK array accesses
	@Safe
	public static void testOLL_OK_16() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				for (int x4 = 0; x4 < 1000000000; ++x4) {
					int y5 = (x4 == 32) ? (y3 * y3) : (2 * y3);
					// y5 = [0,2401] (checked values 0 2401 1296)
					int index6 = 0;
					if (y5 == 0) {
						index6 = 0;
					}
					if (y5 == 2401) {
						index6 = 7;
					}
					if (y5 == 1296) {
						index6 = 7;
					}
					if (y5 == -1) {
						index6 = -1;
					}
					if (y5 == 2402) {
						index6 = 8;
					}
					ptr[index6] = 1;
				}
			}
		}
	}

	@Test
	public void _testOLL_OK_16() {
		assertAnalysis("testOLL_OK_16");
	}

	// Test method with AboveEnd array accesses
	@Unsafe
	public static void testOLL_AboveEnd_17() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				for (int x4 = 0; x4 < 1000000000; ++x4) {
					int y5 = (x4 == 32) ? (y3 * y3) : (2 * y3);
					// y5 = [0,2401] (checked values 0 2401 1296)
					int index6 = 0;
					if (y5 == 0) {
						index6 = 1;
					}
					if (y5 == 2401) {
						index6 = 8;
					}
					if (y5 == 1296) {
						index6 = 8;
					}
					if (y5 == -1) {
						index6 = 0;
					}
					if (y5 == 2402) {
						index6 = 9;
					}
					ptr[index6] = 1;
				}
			}
		}
	}

	@Test
	public void _testOLL_AboveEnd_17() {
		assertAnalysis("testOLL_AboveEnd_17");
	}

	// Test method with Negative array accesses
	@Unsafe
	public static void testOLL_Negative_18() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				for (int x4 = 0; x4 < 1000000000; ++x4) {
					int y5 = (x4 == 32) ? (y3 * y3) : (2 * y3);
					// y5 = [0,2401] (checked values 0 2401 1296)
					int index6 = 0;
					if (y5 == 0) {
						index6 = -1;
					}
					if (y5 == 2401) {
						index6 = 6;
					}
					if (y5 == 1296) {
						index6 = 6;
					}
					if (y5 == -1) {
						index6 = -2;
					}
					if (y5 == 2402) {
						index6 = 7;
					}
					ptr[index6] = 1;
				}
			}
		}
	}

	@Test
	public void _testOLL_Negative_18() {
		assertAnalysis("testOLL_Negative_18");
	}

	// Test method with NegativeAboveEnd array accesses
	@Unsafe
	public static void testOLL_NegativeAboveEnd_19() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				for (int x4 = 0; x4 < 1000000000; ++x4) {
					int y5 = (x4 == 32) ? (y3 * y3) : (2 * y3);
					// y5 = [0,2401] (checked values 0 2401 1296)
					int index6 = 0;
					if (y5 == 0) {
						index6 = -1;
					}
					if (y5 == 2401) {
						index6 = 8;
					}
					if (y5 == 1296) {
						index6 = 8;
					}
					if (y5 == -1) {
						index6 = -2;
					}
					if (y5 == 2402) {
						index6 = 9;
					}
					ptr[index6] = 1;
				}
			}
		}
	}

	@Test
	public void _testOLL_NegativeAboveEnd_19() {
		assertAnalysis("testOLL_NegativeAboveEnd_19");
	}

	// Test method with OK array accesses
	@Safe
	public static void testOLLL_OK_20() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				for (int x4 = 0; x4 < 1000000000; ++x4) {
					int y5 = (x4 == 32) ? (y3 * y3) : (2 * y3);
					// y5 = [0,2401] (checked values 0 2401 1296)
					for (int x6 = 0; x6 < 1000000000; ++x6) {
						int y7 = (x6 == 32) ? (y5 * y5) : (2 * y5);
						// y7 = [0,5764801] (checked values 0 5764801 1679616)
						int index8 = 0;
						if (y7 == 0) {
							index8 = 0;
						}
						if (y7 == 5764801) {
							index8 = 7;
						}
						if (y7 == 1679616) {
							index8 = 7;
						}
						if (y7 == -1) {
							index8 = -1;
						}
						if (y7 == 5764802) {
							index8 = 8;
						}
						ptr[index8] = 1;
					}
				}
			}
		}
	}

	@Test
	public void _testOLLL_OK_20() {
		assertAnalysis("testOLLL_OK_20");
	}

	// Test method with AboveEnd array accesses
	@Unsafe
	public static void testOLLL_AboveEnd_21() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				for (int x4 = 0; x4 < 1000000000; ++x4) {
					int y5 = (x4 == 32) ? (y3 * y3) : (2 * y3);
					// y5 = [0,2401] (checked values 0 2401 1296)
					for (int x6 = 0; x6 < 1000000000; ++x6) {
						int y7 = (x6 == 32) ? (y5 * y5) : (2 * y5);
						// y7 = [0,5764801] (checked values 0 5764801 1679616)
						int index8 = 0;
						if (y7 == 0) {
							index8 = 1;
						}
						if (y7 == 5764801) {
							index8 = 8;
						}
						if (y7 == 1679616) {
							index8 = 8;
						}
						if (y7 == -1) {
							index8 = 0;
						}
						if (y7 == 5764802) {
							index8 = 9;
						}
						ptr[index8] = 1;
					}
				}
			}
		}
	}

	@Test
	public void _testOLLL_AboveEnd_21() {
		assertAnalysis("testOLLL_AboveEnd_21");
	}

	// Test method with Negative array accesses
	@Unsafe
	public static void testOLLL_Negative_22() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				for (int x4 = 0; x4 < 1000000000; ++x4) {
					int y5 = (x4 == 32) ? (y3 * y3) : (2 * y3);
					// y5 = [0,2401] (checked values 0 2401 1296)
					for (int x6 = 0; x6 < 1000000000; ++x6) {
						int y7 = (x6 == 32) ? (y5 * y5) : (2 * y5);
						// y7 = [0,5764801] (checked values 0 5764801 1679616)
						int index8 = 0;
						if (y7 == 0) {
							index8 = -1;
						}
						if (y7 == 5764801) {
							index8 = 6;
						}
						if (y7 == 1679616) {
							index8 = 6;
						}
						if (y7 == -1) {
							index8 = -2;
						}
						if (y7 == 5764802) {
							index8 = 7;
						}
						ptr[index8] = 1;
					}
				}
			}
		}
	}

	@Test
	public void _testOLLL_Negative_22() {
		assertAnalysis("testOLLL_Negative_22");
	}

	// Test method with NegativeAboveEnd array accesses
	@Unsafe
	public static void testOLLL_NegativeAboveEnd_23() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				for (int x4 = 0; x4 < 1000000000; ++x4) {
					int y5 = (x4 == 32) ? (y3 * y3) : (2 * y3);
					// y5 = [0,2401] (checked values 0 2401 1296)
					for (int x6 = 0; x6 < 1000000000; ++x6) {
						int y7 = (x6 == 32) ? (y5 * y5) : (2 * y5);
						// y7 = [0,5764801] (checked values 0 5764801 1679616)
						int index8 = 0;
						if (y7 == 0) {
							index8 = -1;
						}
						if (y7 == 5764801) {
							index8 = 8;
						}
						if (y7 == 1679616) {
							index8 = 8;
						}
						if (y7 == -1) {
							index8 = -2;
						}
						if (y7 == 5764802) {
							index8 = 9;
						}
						ptr[index8] = 1;
					}
				}
			}
		}
	}

	@Test
	public void _testOLLL_NegativeAboveEnd_23() {
		assertAnalysis("testOLLL_NegativeAboveEnd_23");
	}

	// Test method with OK array accesses
	@Safe
	public static void testOLLOLL_OK_24() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				for (int x4 = 0; x4 < 1000000000; ++x4) {
					int y5 = (x4 == 32) ? (y3 * y3) : (2 * y3);
					// y5 = [0,2401] (checked values 0 2401 1296)
					for (int i6 = 0; i6 < 8; ++i6) {
						int x7 = i6 | y5;
						// x7 = [-2147483648,2147483647] (checked values 0 2407
						// 1302)
						for (int x8 = 0; x8 < 1000000000; ++x8) {
							int y9 = (x8 == 32) ? (x7 * x7) : (2 * x7);
							// y9 = [-2147483648,2147483647] (checked values 0
							// 5793649 1695204)
							for (int x10 = 0; x10 < 1000000000; ++x10) {
								int y11 = (x10 == 32) ? (y9 * y9) : (2 * y9);
								// y11 = [-2147483648,2147483647] (checked
								// values 0 1199316961 383480592)
								int index12 = 0;
								if (y11 == 0) {
									index12 = 0;
								}
								if (y11 == 1199316961) {
									index12 = 7;
								}
								if (y11 == 383480592) {
									index12 = 7;
								}
								ptr[index12] = 1;
							}
						}
					}
				}
			}
		}
	}

	@Test
	public void _testOLLOLL_OK_24() {
		assertAnalysis("testOLLOLL_OK_24");
	}

	// Test method with AboveEnd array accesses
	@Unsafe
	public static void testOLLOLL_AboveEnd_25() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				for (int x4 = 0; x4 < 1000000000; ++x4) {
					int y5 = (x4 == 32) ? (y3 * y3) : (2 * y3);
					// y5 = [0,2401] (checked values 0 2401 1296)
					for (int i6 = 0; i6 < 8; ++i6) {
						int x7 = i6 | y5;
						// x7 = [-2147483648,2147483647] (checked values 0 2407
						// 1302)
						for (int x8 = 0; x8 < 1000000000; ++x8) {
							int y9 = (x8 == 32) ? (x7 * x7) : (2 * x7);
							// y9 = [-2147483648,2147483647] (checked values 0
							// 5793649 1695204)
							for (int x10 = 0; x10 < 1000000000; ++x10) {
								int y11 = (x10 == 32) ? (y9 * y9) : (2 * y9);
								// y11 = [-2147483648,2147483647] (checked
								// values 0 1199316961 383480592)
								int index12 = 0;
								if (y11 == 0) {
									index12 = 1;
								}
								if (y11 == 1199316961) {
									index12 = 8;
								}
								if (y11 == 383480592) {
									index12 = 8;
								}
								ptr[index12] = 1;
							}
						}
					}
				}
			}
		}
	}

	@Test
	public void _testOLLOLL_AboveEnd_25() {
		assertAnalysis("testOLLOLL_AboveEnd_25");
	}

	// Test method with Negative array accesses
	@Unsafe
	public static void testOLLOLL_Negative_26() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				for (int x4 = 0; x4 < 1000000000; ++x4) {
					int y5 = (x4 == 32) ? (y3 * y3) : (2 * y3);
					// y5 = [0,2401] (checked values 0 2401 1296)
					for (int i6 = 0; i6 < 8; ++i6) {
						int x7 = i6 | y5;
						// x7 = [-2147483648,2147483647] (checked values 0 2407
						// 1302)
						for (int x8 = 0; x8 < 1000000000; ++x8) {
							int y9 = (x8 == 32) ? (x7 * x7) : (2 * x7);
							// y9 = [-2147483648,2147483647] (checked values 0
							// 5793649 1695204)
							for (int x10 = 0; x10 < 1000000000; ++x10) {
								int y11 = (x10 == 32) ? (y9 * y9) : (2 * y9);
								// y11 = [-2147483648,2147483647] (checked
								// values 0 1199316961 383480592)
								int index12 = 0;
								if (y11 == 0) {
									index12 = -1;
								}
								if (y11 == 1199316961) {
									index12 = 6;
								}
								if (y11 == 383480592) {
									index12 = 6;
								}
								ptr[index12] = 1;
							}
						}
					}
				}
			}
		}
	}

	@Test
	public void _testOLLOLL_Negative_26() {
		assertAnalysis("testOLLOLL_Negative_26");
	}

	// Test method with NegativeAboveEnd array accesses
	@Unsafe
	public static void testOLLOLL_NegativeAboveEnd_27() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int x2 = 0; x2 < 1000000000; ++x2) {
				int y3 = (x2 == 32) ? (x1 * x1) : (2 * x1);
				// y3 = [0,49] (checked values 0 49 36)
				for (int x4 = 0; x4 < 1000000000; ++x4) {
					int y5 = (x4 == 32) ? (y3 * y3) : (2 * y3);
					// y5 = [0,2401] (checked values 0 2401 1296)
					for (int i6 = 0; i6 < 8; ++i6) {
						int x7 = i6 | y5;
						// x7 = [-2147483648,2147483647] (checked values 0 2407
						// 1302)
						for (int x8 = 0; x8 < 1000000000; ++x8) {
							int y9 = (x8 == 32) ? (x7 * x7) : (2 * x7);
							// y9 = [-2147483648,2147483647] (checked values 0
							// 5793649 1695204)
							for (int x10 = 0; x10 < 1000000000; ++x10) {
								int y11 = (x10 == 32) ? (y9 * y9) : (2 * y9);
								// y11 = [-2147483648,2147483647] (checked
								// values 0 1199316961 383480592)
								int index12 = 0;
								if (y11 == 0) {
									index12 = -1;
								}
								if (y11 == 1199316961) {
									index12 = 8;
								}
								if (y11 == 383480592) {
									index12 = 8;
								}
								ptr[index12] = 1;
							}
						}
					}
				}
			}
		}
	}

	@Test
	public void _testOLLOLL_NegativeAboveEnd_27() {
		assertAnalysis("testOLLOLL_NegativeAboveEnd_27");
	}

	// Test method with OK array accesses
	@Safe
	public static void testOOO_OK_28() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int i2 = 0; i2 < 8; ++i2) {
				int x3 = i2 | x1;
				// x3 = [0,7] (checked values 0 7 6)
				for (int i4 = 0; i4 < 8; ++i4) {
					int x5 = i4 | x3;
					// x5 = [0,7] (checked values 0 7 6)
					ptr[x5] = 1;
				}
			}
		}
	}

	@Test
	public void _testOOO_OK_28() {
		assertAnalysis("testOOO_OK_28");
	}

	// Test method with AboveEnd array accesses
	@Unsafe
	public static void testOOO_AboveEnd_29() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int i2 = 0; i2 < 8; ++i2) {
				int x3 = i2 | x1;
				// x3 = [0,7] (checked values 0 7 6)
				for (int i4 = 0; i4 < 8; ++i4) {
					int x5 = i4 | x3;
					// x5 = [0,7] (checked values 0 7 6)
					int index6 = x5 + 1;
					ptr[index6] = 1;
				}
			}
		}
	}

	@Test
	public void _testOOO_AboveEnd_29() {
		assertAnalysis("testOOO_AboveEnd_29");
	}

	// Test method with Negative array accesses
	@Unsafe
	public static void testOOO_Negative_30() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int i2 = 0; i2 < 8; ++i2) {
				int x3 = i2 | x1;
				// x3 = [0,7] (checked values 0 7 6)
				for (int i4 = 0; i4 < 8; ++i4) {
					int x5 = i4 | x3;
					// x5 = [0,7] (checked values 0 7 6)
					int index6 = x5 + -1;
					ptr[index6] = 1;
				}
			}
		}
	}

	@Test
	public void _testOOO_Negative_30() {
		assertAnalysis("testOOO_Negative_30");
	}

	// Test method with NegativeAboveEnd array accesses
	@Unsafe
	public static void testOOO_NegativeAboveEnd_31() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int i2 = 0; i2 < 8; ++i2) {
				int x3 = i2 | x1;
				// x3 = [0,7] (checked values 0 7 6)
				for (int i4 = 0; i4 < 8; ++i4) {
					int x5 = i4 | x3;
					// x5 = [0,7] (checked values 0 7 6)
					int index6 = 0;
					if (x5 == 0) {
						index6 = -1;
					}
					if (x5 == 7) {
						index6 = 8;
					}
					if (x5 == 6) {
						index6 = 8;
					}
					if (x5 == -1) {
						index6 = -2;
					}
					if (x5 == 8) {
						index6 = 9;
					}
					ptr[index6] = 1;
				}
			}
		}
	}

	@Test
	public void _testOOO_NegativeAboveEnd_31() {
		assertAnalysis("testOOO_NegativeAboveEnd_31");
	}

	// Test method with OK array accesses
	@Safe
	public static void testOOOOOOOOOOO_OK_32() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int i2 = 0; i2 < 8; ++i2) {
				int x3 = i2 | x1;
				// x3 = [0,7] (checked values 0 7 6)
				for (int i4 = 0; i4 < 8; ++i4) {
					int x5 = i4 | x3;
					// x5 = [0,7] (checked values 0 7 6)
					for (int i6 = 0; i6 < 8; ++i6) {
						int x7 = i6 | x5;
						// x7 = [0,7] (checked values 0 7 6)
						for (int i8 = 0; i8 < 8; ++i8) {
							int x9 = i8 | x7;
							// x9 = [0,7] (checked values 0 7 6)
							for (int i10 = 0; i10 < 8; ++i10) {
								int x11 = i10 | x9;
								// x11 = [0,7] (checked values 0 7 6)
								for (int i12 = 0; i12 < 8; ++i12) {
									int x13 = i12 | x11;
									// x13 = [0,7] (checked values 0 7 6)
									for (int i14 = 0; i14 < 8; ++i14) {
										int x15 = i14 | x13;
										// x15 = [0,7] (checked values 0 7 6)
										for (int i16 = 0; i16 < 8; ++i16) {
											int x17 = i16 | x15;
											// x17 = [0,7] (checked values 0 7
											// 6)
											for (int i18 = 0; i18 < 8; ++i18) {
												int x19 = i18 | x17;
												// x19 = [0,7] (checked values 0
												// 7 6)
												for (int i20 = 0; i20 < 8; ++i20) {
													int x21 = i20 | x19;
													// x21 = [0,7] (checked
													// values 0 7 6)
													ptr[x21] = 1;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Test
	public void _testOOOOOOOOOOO_OK_32() {
		assertAnalysis("testOOOOOOOOOOO_OK_32");
	}

	// Test method with AboveEnd array accesses
	@Unsafe
	public static void testOOOOOOOOOOO_AboveEnd_33() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int i2 = 0; i2 < 8; ++i2) {
				int x3 = i2 | x1;
				// x3 = [0,7] (checked values 0 7 6)
				for (int i4 = 0; i4 < 8; ++i4) {
					int x5 = i4 | x3;
					// x5 = [0,7] (checked values 0 7 6)
					for (int i6 = 0; i6 < 8; ++i6) {
						int x7 = i6 | x5;
						// x7 = [0,7] (checked values 0 7 6)
						for (int i8 = 0; i8 < 8; ++i8) {
							int x9 = i8 | x7;
							// x9 = [0,7] (checked values 0 7 6)
							for (int i10 = 0; i10 < 8; ++i10) {
								int x11 = i10 | x9;
								// x11 = [0,7] (checked values 0 7 6)
								for (int i12 = 0; i12 < 8; ++i12) {
									int x13 = i12 | x11;
									// x13 = [0,7] (checked values 0 7 6)
									for (int i14 = 0; i14 < 8; ++i14) {
										int x15 = i14 | x13;
										// x15 = [0,7] (checked values 0 7 6)
										for (int i16 = 0; i16 < 8; ++i16) {
											int x17 = i16 | x15;
											// x17 = [0,7] (checked values 0 7
											// 6)
											for (int i18 = 0; i18 < 8; ++i18) {
												int x19 = i18 | x17;
												// x19 = [0,7] (checked values 0
												// 7 6)
												for (int i20 = 0; i20 < 8; ++i20) {
													int x21 = i20 | x19;
													// x21 = [0,7] (checked
													// values 0 7 6)
													int index22 = x21 + 1;
													ptr[index22] = 1;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Test
	public void _testOOOOOOOOOOO_AboveEnd_33() {
		assertAnalysis("testOOOOOOOOOOO_AboveEnd_33");
	}

	// Test method with Negative array accesses
	@Unsafe
	public static void testOOOOOOOOOOO_Negative_34() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int i2 = 0; i2 < 8; ++i2) {
				int x3 = i2 | x1;
				// x3 = [0,7] (checked values 0 7 6)
				for (int i4 = 0; i4 < 8; ++i4) {
					int x5 = i4 | x3;
					// x5 = [0,7] (checked values 0 7 6)
					for (int i6 = 0; i6 < 8; ++i6) {
						int x7 = i6 | x5;
						// x7 = [0,7] (checked values 0 7 6)
						for (int i8 = 0; i8 < 8; ++i8) {
							int x9 = i8 | x7;
							// x9 = [0,7] (checked values 0 7 6)
							for (int i10 = 0; i10 < 8; ++i10) {
								int x11 = i10 | x9;
								// x11 = [0,7] (checked values 0 7 6)
								for (int i12 = 0; i12 < 8; ++i12) {
									int x13 = i12 | x11;
									// x13 = [0,7] (checked values 0 7 6)
									for (int i14 = 0; i14 < 8; ++i14) {
										int x15 = i14 | x13;
										// x15 = [0,7] (checked values 0 7 6)
										for (int i16 = 0; i16 < 8; ++i16) {
											int x17 = i16 | x15;
											// x17 = [0,7] (checked values 0 7
											// 6)
											for (int i18 = 0; i18 < 8; ++i18) {
												int x19 = i18 | x17;
												// x19 = [0,7] (checked values 0
												// 7 6)
												for (int i20 = 0; i20 < 8; ++i20) {
													int x21 = i20 | x19;
													// x21 = [0,7] (checked
													// values 0 7 6)
													int index22 = x21 + -1;
													ptr[index22] = 1;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Test
	public void _testOOOOOOOOOOO_Negative_34() {
		assertAnalysis("testOOOOOOOOOOO_Negative_34");
	}

	// Test method with NegativeAboveEnd array accesses
	@Unsafe
	public static void testOOOOOOOOOOO_NegativeAboveEnd_35() {
		int[] ptr = new int[8];
		int start = 0;
		// start = [0,0] (checked values 0 0 0)
		for (int i0 = 0; i0 < 8; ++i0) {
			int x1 = i0 | start;
			// x1 = [0,7] (checked values 0 7 6)
			for (int i2 = 0; i2 < 8; ++i2) {
				int x3 = i2 | x1;
				// x3 = [0,7] (checked values 0 7 6)
				for (int i4 = 0; i4 < 8; ++i4) {
					int x5 = i4 | x3;
					// x5 = [0,7] (checked values 0 7 6)
					for (int i6 = 0; i6 < 8; ++i6) {
						int x7 = i6 | x5;
						// x7 = [0,7] (checked values 0 7 6)
						for (int i8 = 0; i8 < 8; ++i8) {
							int x9 = i8 | x7;
							// x9 = [0,7] (checked values 0 7 6)
							for (int i10 = 0; i10 < 8; ++i10) {
								int x11 = i10 | x9;
								// x11 = [0,7] (checked values 0 7 6)
								for (int i12 = 0; i12 < 8; ++i12) {
									int x13 = i12 | x11;
									// x13 = [0,7] (checked values 0 7 6)
									for (int i14 = 0; i14 < 8; ++i14) {
										int x15 = i14 | x13;
										// x15 = [0,7] (checked values 0 7 6)
										for (int i16 = 0; i16 < 8; ++i16) {
											int x17 = i16 | x15;
											// x17 = [0,7] (checked values 0 7
											// 6)
											for (int i18 = 0; i18 < 8; ++i18) {
												int x19 = i18 | x17;
												// x19 = [0,7] (checked values 0
												// 7 6)
												for (int i20 = 0; i20 < 8; ++i20) {
													int x21 = i20 | x19;
													// x21 = [0,7] (checked
													// values 0 7 6)
													int index22 = 0;
													if (x21 == 0) {
														index22 = -1;
													}
													if (x21 == 7) {
														index22 = 8;
													}
													if (x21 == 6) {
														index22 = 8;
													}
													if (x21 == -1) {
														index22 = -2;
													}
													if (x21 == 8) {
														index22 = 9;
													}
													ptr[index22] = 1;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Test
	public void _testOOOOOOOOOOO_NegativeAboveEnd_35() {
		assertAnalysis("testOOOOOOOOOOO_NegativeAboveEnd_35");
	}

	public static void main(String[] args) {
		testPreciseEqual1();
		testPreciseEqual2();
		testPreciseEqual3();
		testOLLL_Negative_22();
		testOLO_OK_8();
		testOL_Negative_6();
		testOOO_Negative_30();
		testOLLL_NegativeAboveEnd_23();
		testOL_NegativeAboveEnd_7();
		testOL_OK_4();
		testOOOOOOOOOOO_Negative_34();
		testOLLOLL_AboveEnd_25();
		testOLLOLL_OK_24();
		testOOOOOOOOOOO_NegativeAboveEnd_35();
		testOOO_OK_28();
		testOLLOLL_Negative_26();
		testOOOOOOOOOOO_AboveEnd_33();
		testOLO_Negative_10();
		testO_OK_0();
		testO_NegativeAboveEnd_3();
		testOLO_NegativeAboveEnd_11();
		testOOOOOOOOOOO_OK_32();
		testOOO_NegativeAboveEnd_31();
		testOLOO_NegativeAboveEnd_15();
		testO_Negative_2();
		testOLLL_OK_20();
		testOLLL_AboveEnd_21();
		testOLOO_OK_12();
		testOLL_Negative_18();
		testOL_AboveEnd_5();
		testOLO_AboveEnd_9();
		testOLLOLL_NegativeAboveEnd_27();
		testOLOO_Negative_14();
		testOLL_OK_16();
		testOLL_AboveEnd_17();
		testOOO_AboveEnd_29();
		testOLOO_AboveEnd_13();
		testOLL_NegativeAboveEnd_19();
		testO_AboveEnd_1();
	}

}
