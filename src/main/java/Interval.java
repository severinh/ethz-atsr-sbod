import org.apache.log4j.Logger;

/**
 * Represents an immutable element of the interval domain.
 */
public class Interval {

	private static final Logger LOG = Logger.getLogger(Interval.class);

	public static final Interval TOP = new Interval(Integer.MIN_VALUE,
			Integer.MAX_VALUE);
	public static final Interval BOTTOM = new Interval(1, 0);
	public static final Interval NON_NEGATIVE = new Interval(0,
			Integer.MAX_VALUE);

	/**
	 * The maximum number of combinations of values that
	 * {@link #binOp(Interval, Interval, BinOp)} is allowed to compute in the
	 * concrete.
	 */
	public static final int MAX_PRECISE_COMBINATIONS = 100;
	
	private final int lower;
	private final int upper;

	/**
	 * The constructor is deliberately private, such that clients are forced to
	 * use the static constructor methods.
	 * 
	 * @param lower
	 *            the lower boundary of the interval
	 * @param upper
	 *            the upper boundary of the interval
	 */
	private Interval(int lower, int upper) {
		this.lower = lower;
		this.upper = upper;
	}

	public static Interval of(int value) {
		return new Interval(value, value);
	}

	public static Interval of(int lower, int upper) {
		if (lower > upper) {
			return BOTTOM;
		} else {
			return new Interval(lower, upper);
		}
	}

	public static Interval of(long lower, long upper) {
		if (lower > upper) {
			return BOTTOM;
		} else {
			// Check if lower and upper are part of the same integer "window"
			// ...] [Integer.MIN_VALUE, Integer.MAX_VALUE] [...
			// First, shift the values because we consider as windows
			// ...] [0, Integer.MAX_VALUE - Integer.MIN_VALUE + 1] [...
			long lowerShifted = lower - (long) Integer.MIN_VALUE;
			long upperShifted = upper - (long) Integer.MIN_VALUE;
			long windowSize = (long) Integer.MAX_VALUE - (long) Integer.MIN_VALUE + 1l;
			
			if (lowerShifted < 0 && upperShifted >= 0) {
				// Clearly not in the same window
				return TOP;
			} else {
				// When lowerShifted and upperShifted are negative, we need
				// to handle it in a special way, such that the negative
				// windows start at 0 rather than -1.
				if (lowerShifted < 0) {
					lowerShifted++;
					upperShifted++;
				}

				// Check if they are in the same window
				if (lowerShifted / windowSize != upperShifted / windowSize) {
					return TOP;
				}
			}

			Interval interval = Interval.of((int) lower, (int) upper);
			return interval;
		}
	}

	public int getLower() {
		return lower;
	}

	public int getUpper() {
		return upper;
	}

	public boolean isBottom() {
		return equals(BOTTOM);
	}

	public boolean isTop() {
		return equals(TOP);
	}

	public boolean isSingleValue() {
		boolean result = getLower() == getUpper();
		return result;
	}

	public boolean isNonNegative() {
		boolean result = getLower() >= 0;
		return result;
	}

	/**
	 * Returns the number of distinct values in this interval. For instance, the
	 * interval [2, 4] contains 3 values.
	 * 
	 * @return the number of values in the interval
	 */
	public long getValueCount() {
		if (isBottom()) {
			return 0;
		} else {
			return (long) getUpper() - (long) getLower() + 1;
		}
	}

	public boolean contains(int value) {
		if (isBottom()) {
			return false;
		} else {
			return getLower() <= value && value <= getUpper();
		}
	}

	@Override
	public String toString() {
		if (equals(TOP)) {
			return "TOP";
		} else if (equals(BOTTOM)) {
			return "BOTTOM";
		} else {
			return String.format("[%d,%d]", getLower(), getUpper());
		}
	}

	static abstract class BinOp {

		public abstract int op(int firstValue, int rightValue);
		public Interval fallback(Interval leftInterval, Interval rightInterval) {
			return Interval.TOP;
		}

	}

	/**
	 * Tries to compute the most precise interval resulting from a binary
	 * expression.
	 * 
	 * @param leftInterval
	 * @param rightInterval
	 * @param binOp
	 * @return
	 */
	public static Interval binOp(Interval leftInterval, Interval rightInterval,
			BinOp binOp) {
		if (leftInterval.isBottom() || rightInterval.isBottom()) {
			return Interval.BOTTOM;
		}

		long leftValueCount = leftInterval.getValueCount();
		long rightValueCount = rightInterval.getValueCount();

		// Do a preliminary safety check in order to avoid a potential
		// long overflow due to the multiplication following it
		if (leftValueCount > MAX_PRECISE_COMBINATIONS
				|| rightValueCount > MAX_PRECISE_COMBINATIONS) {
			return binOp.fallback(leftInterval, rightInterval);
		}

		long combinationCount = leftValueCount * rightValueCount;

		if (combinationCount > MAX_PRECISE_COMBINATIONS) {
			return binOp.fallback(leftInterval, rightInterval);
		}

		int lower = Integer.MAX_VALUE;
		int upper = Integer.MIN_VALUE;

		for (long leftValue = leftInterval.getLower(); leftValue <= leftInterval
				.getUpper(); leftValue++) {
			for (long rightValue = rightInterval.getLower(); rightValue <= rightInterval
					.getUpper(); rightValue++) {
				int value = binOp.op((int) leftValue, (int) rightValue);
				lower = Math.min(lower, value);
				upper = Math.max(upper, value);
			}
		}

		Interval result = Interval.of(lower, upper);
		return result;
	}

	public static Interval plus(Interval leftInterval, Interval rightInterval) {
		// Performance optimization for the case where it is certain that no
		// overflow occurs
		if (!leftInterval.isBottom() && !rightInterval.isBottom()) {
			long lower = ((long) leftInterval.getLower())
					+ ((long) rightInterval.getLower());
			long upper = ((long) leftInterval.getUpper())
					+ ((long) rightInterval.getUpper());
			if (Integer.MIN_VALUE <= lower && lower <= Integer.MAX_VALUE
					&& Integer.MIN_VALUE <= upper && upper <= Integer.MAX_VALUE) {
				return Interval.of(lower, upper);
			}
		}
		
		return binOp(leftInterval, rightInterval, new BinOp() {

			@Override
			public int op(int firstValue, int rightValue) {
				return firstValue + rightValue;
			}
			
			@Override
			public Interval fallback(Interval leftInterval, Interval rightInterval) {
				long firstLower = leftInterval.getLower();
				long secondLower = rightInterval.getLower();
				long firstUpper = leftInterval.getUpper();
				long secondUpper = rightInterval.getUpper();
				long lower = firstLower + secondLower;
				long upper = firstUpper + secondUpper;
				return Interval.of(lower, upper);
			}
		});
	}

	public static Interval sub(Interval leftInterval, Interval rightInterval) {
		return binOp(leftInterval, rightInterval, new BinOp() {

			@Override
			public int op(int firstValue, int rightValue) {
				return firstValue - rightValue;
			}

			@Override
			public Interval fallback(Interval leftInterval, Interval rightInterval) {
				long firstLower = leftInterval.getLower();
				long secondLower = rightInterval.getLower();
				long firstUpper = leftInterval.getUpper();
				long secondUpper = rightInterval.getUpper();
				long lower = firstLower - secondLower;
				long upper = firstUpper - secondUpper;
				return Interval.of(lower, upper);
			}
		});
	}

	public static Interval mul(Interval leftInterval, Interval rightInterval) {
		return binOp(leftInterval, rightInterval, new BinOp() {

			@Override
			public int op(int firstValue, int rightValue) {
				return firstValue * rightValue;
			}

			@Override
			public Interval fallback(Interval leftInterval, Interval rightInterval) {
				long a = leftInterval.getLower();
				long b = leftInterval.getUpper();
				long c = rightInterval.getLower();
				long d = rightInterval.getUpper();
				long e = Math.min(a * c, Math.min(a * d, Math.min(b * c, b * d)));
				long f = Math.max(a * c, Math.max(a * d, Math.max(b * c, b * d)));
				return Interval.of(e, f);
			}
		});
	}

	public static Interval div(Interval leftInterval, Interval rightInterval) {
		return binOp(leftInterval, rightInterval, new BinOp() {

			@Override
			public int op(int leftValue, int rightValue) {
				return leftValue / rightValue;
			}
			
			@Override
			public Interval fallback(Interval leftInterval, Interval rightInterval) {
				LOG.warn("loss of precision due to unsupported division expression");
				return super.fallback(leftInterval, rightInterval);
			}
		});
	}

	public static Interval rem(Interval leftInterval, Interval rightInterval) {
		return binOp(leftInterval, rightInterval, new BinOp() {

			@Override
			public int op(int leftValue, int rightValue) {
				return leftValue % rightValue;
			}
			
			@Override
			public Interval fallback(Interval leftInterval, Interval rightInterval) {
				LOG.warn("loss of precision due to unsupported modulo expression");
				return super.fallback(leftInterval, rightInterval);
			}
		});
	}

	public static Interval neg(Interval interval) {
		if (interval.isBottom()) {
			return BOTTOM;
		}

		if (interval.getLower() == Integer.MIN_VALUE
				&& interval.getUpper() > Integer.MIN_VALUE) {
			return TOP;
		} else {
			return Interval.of(-interval.getUpper(), -interval.getLower());
		}
	}

	public static Interval shl(Interval leftInterval, Interval rightInterval) {
		return binOp(leftInterval, rightInterval, new BinOp() {

			@Override
			public int op(int firstValue, int rightValue) {
				return firstValue << rightValue;
			}
		});
	}

	public static Interval shr(Interval leftInterval, Interval rightInterval) {
		return binOp(leftInterval, rightInterval, new BinOp() {

			@Override
			public int op(int firstValue, int rightValue) {
				return firstValue >> rightValue;
			}
		});
	}

	public static Interval ushr(Interval leftInterval, Interval rightInterval) {
		return binOp(leftInterval, rightInterval, new BinOp() {

			@Override
			public int op(int firstValue, int rightValue) {
				return firstValue >>> rightValue;
			}
		});
	}

	public static Interval and(Interval leftInterval, Interval rightInterval) {
		return binOp(leftInterval, rightInterval, new BinOp() {

			@Override
			public int op(int firstValue, int rightValue) {
				return firstValue & rightValue;
			}
			
			@Override
			public Interval fallback(Interval leftInterval, Interval rightInterval) {
				if (leftInterval.isNonNegative()
						&& rightInterval.isNonNegative()) {
					int minUpper = Math.min(leftInterval.getUpper(), rightInterval.getUpper());
					
					// Idea: some parts of the resulting numbers can only be determined by the 
					// 	greater of the two interval bounds.
					// 	if you have a = 000000AAAAAAAAAA and b = 0000000000BBBBBB, then the greatest 
					//	resulting number has the pattern 0000000000111111
					//	                                 000000AAAAAAAAAA
					//	                                 0000000000BBBBBB

					int havoc = (1 << (32 - Integer.numberOfLeadingZeros(minUpper)))-1;
					
					// As with XOR, we can almost always generate a bit pattern that will cancel
					// out the lower bits. To compensate, we conservatively assume a lower bound of
					// zero.
					return Interval.of(0, havoc);
				} else {
					return super.fallback(leftInterval, rightInterval);
				}
			}
		});
	}

	public static Interval or(Interval leftInterval, Interval rightInterval) {
		return binOp(leftInterval, rightInterval, new BinOp() {

			@Override
			public int op(int firstValue, int rightValue) {
				return firstValue | rightValue;
			}
			
			@Override
			public Interval fallback(Interval leftInterval, Interval rightInterval) {
				if (leftInterval.isNonNegative()
						&& rightInterval.isNonNegative()) {
					int maxUpper = Math.max(leftInterval.getUpper(), rightInterval.getUpper());
					int minUpper = Math.min(leftInterval.getUpper(), rightInterval.getUpper());
					
					// Idea: some parts of the resulting numbers can only be determined by the 
					// 	greater of the two interval bounds.
					// 	if you have a = 000000AAAAAAAAAA and b = 0000000000BBBBBB, then the greatest 
					//	resulting number has the pattern 000000AAAA111111

					int havoc = (1 << (32 - Integer.numberOfLeadingZeros(minUpper)))-1;
					maxUpper |= havoc;
					
					return Interval.of(Math.min(leftInterval.getLower(), rightInterval.getLower()), maxUpper);
				} else {
					return super.fallback(leftInterval, rightInterval);
				}
			}
		});
	}

	public static Interval xor(Interval leftInterval, Interval rightInterval) {
		return binOp(leftInterval, rightInterval, new BinOp() {

			@Override
			public int op(int firstValue, int rightValue) {
				return firstValue ^ rightValue;
			}
			
			@Override 
			public Interval fallback(Interval leftInterval, Interval rightInterval) {
				if(leftInterval.isNonNegative() && rightInterval.isNonNegative()) {
					int maxUpper = Math.max(leftInterval.getUpper(), rightInterval.getUpper());
					int minUpper = Math.min(leftInterval.getUpper(), rightInterval.getUpper());
					
					// Idea: some parts of the resulting numbers can only be determined by the 
					// 	greater of the two interval bounds.
					// 	if you have a = 000000AAAAAAAAAA and b = 0000000000BBBBBB, then the greatest 
					//	resulting number has the pattern 000000AAAA111111

					int havoc = (1 << (32-Integer.numberOfLeadingZeros(minUpper)))-1;
					maxUpper |= havoc;
					
					// Important: Unlike with the ordinary bitwise or, the exclusive or
					// 	could potentially cancel out all lower bits.
					// We thus conservatively assume a lower bound of 0. 
					return Interval.of(0, maxUpper);
				} else {
					return super.fallback(leftInterval, rightInterval);
				}
			}
		});
	}
	
	public static Interval meet(Interval interval, Interval otherInterval) {
		if (interval.isBottom() && otherInterval.isBottom()) {
			return BOTTOM;
		} else if (interval.isBottom()) {
			return otherInterval;
		} else if (otherInterval.isBottom()) {
			return interval;
		} else {
			int lower = Math.min(interval.getLower(), otherInterval.getLower());
			int upper = Math.max(interval.getUpper(), otherInterval.getUpper());
			Interval result = Interval.of(lower, upper);
			return result;
		}
	}

	public static Interval join(Interval interval, Interval otherInterval) {
		if (interval.isBottom() || otherInterval.isBottom()) {
			return BOTTOM;
		}
		int lower = Math.max(interval.getLower(), otherInterval.getLower());
		int upper = Math.min(interval.getUpper(), otherInterval.getUpper());
		Interval result = Interval.of(lower, upper);
		return result;
	}

	/**
	 * Returns the constrained left-hand side interval of a less-than comparison
	 * of two intervals under the assumption that the condition holds, i.e., the
	 * branch is taken.
	 * 
	 * Calling this method is only useful if the left-hand side of the condition
	 * is actually a variable such that the corresponding interval can be
	 * constrained inside of the branch.
	 * 
	 * @param leftInterval
	 *            the interval associated with the left-hand side
	 * @param rightInterval
	 *            the interval associated with the right-hand side
	 * @return the constrained left-hand side interval
	 */
	public static Interval lt(Interval leftInterval, Interval rightInterval) {
		if (leftInterval.isBottom() || rightInterval.isBottom()) {
			return BOTTOM;
		}
		if (rightInterval.getUpper() == Integer.MIN_VALUE) {
			return BOTTOM;
		}
		int lower = leftInterval.getLower();
		int upper = Math.min(leftInterval.getUpper(),
				rightInterval.getUpper() - 1);
		Interval result = Interval.of(lower, upper);
		return result;
	}

	/**
	 * Returns the constrained left-hand side interval of a less-or-equal
	 * comparison of two intervals under the assumption that the condition
	 * holds, i.e., the branch is taken.
	 * 
	 * Calling this method is only useful if the left-hand side of the condition
	 * is actually a variable such that the corresponding interval can be
	 * constrained inside of the branch.
	 * 
	 * @param leftInterval
	 *            the interval associated with the left-hand side
	 * @param rightInterval
	 *            the interval associated with the right-hand side
	 * @return the constrained left-hand side interval
	 */
	public static Interval le(Interval leftInterval, Interval rightInterval) {
		if (leftInterval.isBottom() || rightInterval.isBottom()) {
			return BOTTOM;
		}
		int lower = leftInterval.getLower();
		int upper = Math.min(leftInterval.getUpper(), rightInterval.getUpper());
		Interval result = Interval.of(lower, upper);
		return result;
	}

	/**
	 * Returns the constrained left-hand side interval of an equality comparison
	 * of two intervals under the assumption that the condition holds, i.e., the
	 * branch is taken.
	 * 
	 * Calling this method is only useful if the left-hand side of the condition
	 * is actually a variable such that the corresponding interval can be
	 * constrained inside of the branch.
	 * 
	 * @param leftInterval
	 *            the interval associated with the left-hand side
	 * @param rightInterval
	 *            the interval associated with the right-hand side
	 * @return the constrained left-hand side interval
	 */
	public static Interval eq(Interval leftInterval, Interval rightInterval) {
		Interval result = join(leftInterval, rightInterval);
		return result;
	}

	/**
	 * Returns the constrained left-hand side interval of an non-equality
	 * comparison of two intervals under the assumption that the condition
	 * holds, i.e., the branch is taken.
	 * 
	 * Calling this method is only useful if the left-hand side of the condition
	 * is actually a variable such that the corresponding interval can be
	 * constrained inside of the branch.
	 * 
	 * @param leftInterval
	 *            the interval associated with the left-hand side
	 * @param rightInterval
	 *            the interval associated with the right-hand side
	 * @return the constrained left-hand side interval
	 */
	public static Interval ne(Interval leftInterval, Interval rightInterval) {
		if (leftInterval.isBottom() || rightInterval.isBottom()) {
			return BOTTOM;
		}

		if (leftInterval.getLower() == leftInterval.getUpper()
				&& rightInterval.contains(leftInterval.getLower())) {
			return BOTTOM;
		}

		// Constrain the left interval only if the right interval contains
		// exactly one value
		if (rightInterval.getLower() == rightInterval.getUpper()) {
			// Constrain the left interval only if the right value marks the
			// start or end of the left interval
			if (leftInterval.getLower() == rightInterval.getLower()) {
				return Interval.of(leftInterval.getLower() + 1,
						leftInterval.getUpper());
			} else if (leftInterval.getUpper() == rightInterval.getUpper()) {
				return Interval.of(leftInterval.getLower(),
						leftInterval.getUpper() - 1);
			}
		}
		return leftInterval;
	}

	/**
	 * Returns the constrained left-hand side interval of a greater-or-equal
	 * comparison of two intervals under the assumption that the condition
	 * holds, i.e., the branch is taken.
	 * 
	 * Calling this method is only useful if the left-hand side of the condition
	 * is actually a variable such that the corresponding interval can be
	 * constrained inside of the branch.
	 * 
	 * @param leftInterval
	 *            the interval associated with the left-hand side
	 * @param rightInterval
	 *            the interval associated with the right-hand side
	 * @return the constrained left-hand side interval
	 */
	public static Interval ge(Interval leftInterval, Interval rightInterval) {
		if (leftInterval.isBottom() || rightInterval.isBottom()) {
			return BOTTOM;
		}
		int lower = Math.max(leftInterval.getLower(), rightInterval.getLower());
		int upper = leftInterval.getUpper();
		Interval result = Interval.of(lower, upper);
		return result;
	}

	/**
	 * Returns the constrained left-hand side interval of a greater-than
	 * comparison of two intervals under the assumption that the condition
	 * holds, i.e., the branch is taken.
	 * 
	 * Calling this method is only useful if the left-hand side of the condition
	 * is actually a variable such that the corresponding interval can be
	 * constrained inside of the branch.
	 * 
	 * @param leftInterval
	 *            the interval associated with the left-hand side
	 * @param rightInterval
	 *            the interval associated with the right-hand side
	 * @return the constrained left-hand side interval
	 */
	public static Interval gt(Interval leftInterval, Interval rightInterval) {
		if (leftInterval.isBottom() || rightInterval.isBottom()) {
			return BOTTOM;
		}
		if (rightInterval.getUpper() == Integer.MAX_VALUE) {
			return BOTTOM;
		}
		int lower = Math.max(leftInterval.getLower(),
				rightInterval.getLower() + 1);
		int upper = leftInterval.getUpper();
		Interval result = Interval.of(lower, upper);
		return result;
	}

	public static Interval cond(Interval leftInterval, Interval rightInterval,
			ConditionExprEnum conditionExpr) {
		switch (conditionExpr) {
		case LT:
			return lt(leftInterval, rightInterval);
		case LE:
			return le(leftInterval, rightInterval);
		case EQ:
			return eq(leftInterval, rightInterval);
		case NE:
			return ne(leftInterval, rightInterval);
		case GE:
			return ge(leftInterval, rightInterval);
		case GT:
			return gt(leftInterval, rightInterval);
		default:
			throw new IllegalStateException("no such condition expression");
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lower;
		result = prime * result + upper;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Interval other = (Interval) obj;
		if (lower != other.lower) {
			return false;
		}
		if (upper != other.upper) {
			return false;
		}
		return true;
	}

}
