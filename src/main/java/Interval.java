public class Interval {

	// TODO: Do you need to handle infinity or empty interval?
	private final int lower;
	private final int upper;

	public Interval(int value) {
		this.lower = value;
		this.upper = value;
	}

	public Interval(int lower, int upper) {
		// TODO: What if l > u?
		this.lower = lower;
		this.upper = upper;
	}

	public int getLower() {
		return lower;
	}

	public int getUpper() {
		return upper;
	}

	@Override
	public String toString() {
		return String.format("[%d,%d]", getLower(), getUpper());
	}

	public static Interval plus(Interval interval, Interval otherInterval) {
		// TODO: Handle overflow.
		return new Interval(interval.getLower() + otherInterval.getLower(),
				interval.getUpper() + otherInterval.getUpper());
	}

	public static Interval sub(Interval interval, Interval otherInterval) {
		// TODO: Handle overflow.
		return new Interval(interval.getLower() - otherInterval.getLower(),
				interval.getUpper() - otherInterval.getUpper());
	}

	public static Interval mul(Interval interval, Interval otherInterval) {
		// TODO: Handle overflow.
		// TODO: Not correct yet...
		return new Interval(interval.getLower() * otherInterval.getLower(),
				interval.getUpper() * otherInterval.getUpper());
	}

	public static Interval neg(Interval interval) {
		// TODO: Handle overflow
		return new Interval(-interval.getUpper(), -interval.getLower());
	}

	public static Interval meet(Interval interval, Interval otherInterval) {
		int lower = Math.min(interval.getLower(), otherInterval.getLower());
		int upper = Math.max(interval.getUpper(), otherInterval.getUpper());
		Interval result = new Interval(lower, upper);
		return result;
	}

	public static Interval join(Interval interval, Interval otherInterval) {
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
