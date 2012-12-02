public class Interval {

	public static final Interval TOP = new Interval(Integer.MIN_VALUE,
			Integer.MAX_VALUE);
	public static final Interval BOTTOM = new Interval(1, 0);

	private final int lower;
	private final int upper;

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

	public int getLower() {
		return lower;
	}

	public int getUpper() {
		return upper;
	}

	public boolean isBottom() {
		return getLower() > getUpper();
	}

	public boolean isTop() {
		return equals(TOP);
	}

	@Override
	public String toString() {
		return String.format("[%d,%d]", getLower(), getUpper());
	}

	public static Interval plus(Interval interval, Interval otherInterval) {
		if (interval.isBottom() || otherInterval.isBottom()) {
			return BOTTOM;
		}
		// TODO: Handle overflow.
		return new Interval(interval.getLower() + otherInterval.getLower(),
				interval.getUpper() + otherInterval.getUpper());
	}

	public static Interval sub(Interval interval, Interval otherInterval) {
		if (interval.isBottom() || otherInterval.isBottom()) {
			return BOTTOM;
		}
		// TODO: Handle overflow.
		return new Interval(interval.getLower() - otherInterval.getLower(),
				interval.getUpper() - otherInterval.getUpper());
	}

	public static Interval mul(Interval interval, Interval otherInterval) {
		if (interval.isBottom() || otherInterval.isBottom()) {
			return BOTTOM;
		}
		// TODO: Handle overflow.
		// TODO: Not correct yet...
		return new Interval(interval.getLower() * otherInterval.getLower(),
				interval.getUpper() * otherInterval.getUpper());
	}

	public static Interval neg(Interval interval) {
		if (interval.isBottom()) {
			return BOTTOM;
		}
		// TODO: Handle overflow
		return new Interval(-interval.getUpper(), -interval.getLower());
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
			Interval result = new Interval(lower, upper);
			return result;
		}
	}

	public static Interval join(Interval interval, Interval otherInterval) {
		if (interval.isBottom() || otherInterval.isBottom()) {
			return BOTTOM;
		}
		int lower = Math.max(interval.getLower(), otherInterval.getLower());
		int upper = Math.min(interval.getUpper(), otherInterval.getUpper());
		Interval result = new Interval(lower, upper);
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
		int lower = leftInterval.getLower();
		int upper = Math.min(leftInterval.getUpper(),
				rightInterval.getUpper() - 1);
		Interval result = new Interval(lower, upper);
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
		Interval result = new Interval(lower, upper);
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
		// Constrain the left interval only if the right interval contains
		// exactly one value
		if (rightInterval.getUpper() - rightInterval.getLower() == 1) {
			// Constrain the left interval only if the right value marks the
			// start or end of the left interval
			if (leftInterval.getLower() == rightInterval.getLower()) {
				return new Interval(leftInterval.getLower() + 1,
						leftInterval.getUpper());
			} else if (leftInterval.getUpper() == rightInterval.getUpper()) {
				return new Interval(leftInterval.getLower(),
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
		Interval result = new Interval(lower, upper);
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
		int lower = Math.max(leftInterval.getLower(),
				rightInterval.getLower() + 1);
		int upper = leftInterval.getUpper();
		Interval result = new Interval(lower, upper);
		return result;
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Interval other = (Interval) obj;
		if (lower != other.lower)
			return false;
		if (upper != other.upper)
			return false;
		return true;
	}

}
