import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests {@link Interval}.
 */
public class IntervalTests {

	@Test
	public void testConstructor() {
		Interval interval = new Interval(2, 3);
		assertInterval(2, 3, interval);
		assertFalse(interval.isBottom());
		assertFalse(interval.isTop());

		Interval singletonInterval = new Interval(4);
		assertInterval(4, 4, singletonInterval);
		assertFalse(singletonInterval.isBottom());
		assertFalse(singletonInterval.isTop());

		assertTrue(Interval.TOP.isTop());
		assertFalse(Interval.TOP.isBottom());
	}

	@Test
	public void testLt() {
		Interval leftInterval;
		Interval rightInterval;

		leftInterval = new Interval(2, 4);
		rightInterval = new Interval(6, 8);
		assertInterval(2, 4, Interval.lt(leftInterval, rightInterval));

		leftInterval = new Interval(2, 4);
		rightInterval = new Interval(4, 6);
		assertInterval(2, 4, Interval.lt(leftInterval, rightInterval));

		leftInterval = new Interval(2, 4);
		rightInterval = new Interval(3, 5);
		assertInterval(2, 4, Interval.lt(leftInterval, rightInterval));

		leftInterval = new Interval(2, 4);
		rightInterval = new Interval(1, 3);
		assertInterval(2, 2, Interval.lt(leftInterval, rightInterval));

		leftInterval = new Interval(2, 4);
		rightInterval = new Interval(0, 2);
		assertTrue(Interval.lt(leftInterval, rightInterval).isBottom());
	}

	protected void assertInterval(int expectedLower, int expectedUpper,
			Interval interval) {
		assertEquals(expectedLower, interval.getLower());
		assertEquals(expectedUpper, interval.getUpper());
	}

}
