import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests {@link Interval}.
 */
public class IntervalTests {

	@Test
	public void testConstructor() {
		Interval interval = Interval.of(2, 3);
		assertInterval(2, 3, interval);
		assertFalse(interval.isBottom());
		assertFalse(interval.isTop());

		Interval singletonInterval = Interval.of(4);
		assertInterval(4, 4, singletonInterval);
		assertFalse(singletonInterval.isBottom());
		assertFalse(singletonInterval.isTop());

		assertTrue(Interval.TOP.isTop());
		assertFalse(Interval.TOP.isBottom());

		assertFalse(Interval.BOTTOM.isTop());
		assertTrue(Interval.BOTTOM.isBottom());

		Interval someBottom = Interval.of(4, 2);
		assertTrue(someBottom.isBottom());
		assertTrue(someBottom.equals(Interval.BOTTOM));
		assertTrue(someBottom.hashCode() == Interval.BOTTOM.hashCode());
	}

	@Test
	public void testLt() {
		Interval leftInterval;
		Interval rightInterval;

		leftInterval = Interval.of(2, 4);
		rightInterval = Interval.of(6, 8);
		assertInterval(2, 4, Interval.lt(leftInterval, rightInterval));

		leftInterval = Interval.of(2, 4);
		rightInterval = Interval.of(4, 6);
		assertInterval(2, 4, Interval.lt(leftInterval, rightInterval));

		leftInterval = Interval.of(2, 4);
		rightInterval = Interval.of(3, 5);
		assertInterval(2, 4, Interval.lt(leftInterval, rightInterval));

		leftInterval = Interval.of(2, 4);
		rightInterval = Interval.of(1, 3);
		assertInterval(2, 2, Interval.lt(leftInterval, rightInterval));

		leftInterval = Interval.of(2, 4);
		rightInterval = Interval.of(0, 2);
		assertTrue(Interval.lt(leftInterval, rightInterval).isBottom());
	}

	protected void assertInterval(int expectedLower, int expectedUpper,
			Interval interval) {
		assertEquals(expectedLower, interval.getLower());
		assertEquals(expectedUpper, interval.getUpper());
	}

}
