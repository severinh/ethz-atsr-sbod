public class Interval {

	// TODO: Do you need to handle infinity or empty interval?
	private int lower;
	private int upper;

	public Interval(int startValue) {
		lower = startValue;
		upper = startValue;
	}

	public Interval(int l, int u) {
		lower = l;
		upper = u;
	}

	public int getLower() {
		return lower;
	}

	public int getUpper() {
		return upper;
	}

	@Override
	public String toString() {
		return String.format("[%d,%d]", lower, upper);
	}

	public void copyFrom(Interval other) {
		lower = other.lower;
		upper = other.upper;
	}

	public static Interval plus(Interval interval, Interval otherInterval) {
		// TODO: Handle overflow.
		return new Interval(interval.getLower() + otherInterval.getLower(),
				interval.getUpper() + otherInterval.getUpper());
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
