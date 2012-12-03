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
	public void testPlus() {
		Interval interval;
		Interval otherInterval;

		interval = Interval.of(2, 4);
		otherInterval = Interval.of(6, 8);
		assertInterval(8, 12, Interval.plus(interval, otherInterval));
		assertInterval(8, 12, Interval.plus(otherInterval, interval));

		assertTrue(Interval.plus(interval, Interval.BOTTOM).isBottom());
		assertTrue(Interval.plus(Interval.BOTTOM, interval).isBottom());

		interval = Interval.of(Integer.MAX_VALUE);
		otherInterval = Interval.of(1, 2);
		assertInterval(Integer.MIN_VALUE, Integer.MIN_VALUE + 1,
				Interval.plus(interval, otherInterval));
		assertInterval(Integer.MIN_VALUE, Integer.MIN_VALUE + 1,
				Interval.plus(otherInterval, interval));

		interval = Interval.of(Integer.MAX_VALUE);
		otherInterval = Interval.of(-1, 1);
		assertTrue(Interval.plus(interval, otherInterval).isTop());
		assertTrue(Interval.plus(otherInterval, interval).isTop());

		interval = Interval.of(Integer.MAX_VALUE - 1, Integer.MAX_VALUE);
		otherInterval = Interval.of(1);
		assertTrue(Interval.plus(interval, otherInterval).isTop());
		assertTrue(Interval.plus(otherInterval, interval).isTop());

		interval = Interval.of(Integer.MIN_VALUE);
		otherInterval = Interval.of(-2, -1);
		assertInterval(Integer.MAX_VALUE - 1, Integer.MAX_VALUE,
				Interval.plus(interval, otherInterval));
		assertInterval(Integer.MAX_VALUE - 1, Integer.MAX_VALUE,
				Interval.plus(otherInterval, interval));

		interval = Interval.of(Integer.MIN_VALUE);
		otherInterval = Interval.of(-1, 1);
		assertTrue(Interval.plus(interval, otherInterval).isTop());
		assertTrue(Interval.plus(otherInterval, interval).isTop());

		interval = Interval.of(Integer.MIN_VALUE, Integer.MIN_VALUE + 1);
		otherInterval = Interval.of(-1);
		assertTrue(Interval.plus(interval, otherInterval).isTop());
		assertTrue(Interval.plus(otherInterval, interval).isTop());

		interval = Interval.of(Integer.MIN_VALUE);
		otherInterval = Interval.of(Integer.MAX_VALUE);
		assertInterval(-1, Interval.plus(interval, otherInterval));
	}

	@Test
	public void testSub() {
		Interval interval;
		Interval otherInterval;

		interval = Interval.of(2, 4);
		otherInterval = Interval.of(6, 8);
		assertInterval(-6, -2, Interval.sub(interval, otherInterval));
		assertInterval(2, 6, Interval.sub(otherInterval, interval));

		assertTrue(Interval.sub(interval, Interval.BOTTOM).isBottom());
		assertTrue(Interval.sub(Interval.BOTTOM, interval).isBottom());

		interval = Interval.of(Integer.MIN_VALUE);
		otherInterval = Interval.of(1, 2);
		assertInterval(Integer.MAX_VALUE - 1, Integer.MAX_VALUE,
				Interval.sub(interval, otherInterval));
		assertInterval(Integer.MIN_VALUE + 1, Integer.MIN_VALUE + 2,
				Interval.sub(otherInterval, interval));

		interval = Interval.of(Integer.MIN_VALUE);
		otherInterval = Interval.of(-1, 1);
		assertTrue(Interval.sub(interval, otherInterval).isTop());
		assertTrue(Interval.sub(otherInterval, interval).isTop());

		interval = Interval.of(Integer.MIN_VALUE, Integer.MIN_VALUE + 1);
		otherInterval = Interval.of(1);
		assertTrue(Interval.sub(interval, otherInterval).isTop());
		assertInterval(Integer.MIN_VALUE, Integer.MIN_VALUE + 1,
				Interval.sub(otherInterval, interval));

		interval = Interval.of(Integer.MAX_VALUE);
		otherInterval = Interval.of(-2, -1);
		assertInterval(Integer.MIN_VALUE, Integer.MIN_VALUE + 1,
				Interval.sub(interval, otherInterval));
		assertTrue(Interval.sub(otherInterval, interval).isTop());

		interval = Interval.of(Integer.MAX_VALUE);
		otherInterval = Interval.of(-1, 1);
		assertTrue(Interval.sub(interval, otherInterval).isTop());
		assertInterval(Integer.MIN_VALUE, Integer.MIN_VALUE + 2,
				Interval.sub(otherInterval, interval));

		interval = Interval.of(Integer.MAX_VALUE + 1, Integer.MAX_VALUE);
		otherInterval = Interval.of(-1);
		assertTrue(Interval.sub(interval, otherInterval).isTop());
		assertTrue(Interval.sub(otherInterval, interval).isTop());

		interval = Interval.of(Integer.MAX_VALUE);
		otherInterval = Interval.of(Integer.MIN_VALUE);
		assertInterval(-1, Interval.sub(interval, otherInterval));
	}

	@Test
	public void testNeg() {
		Interval interval;

		interval = Interval.of(2, 4);
		assertInterval(-4, -2, Interval.neg(interval));

		interval = Interval.of(-2, 4);
		assertInterval(-4, 2, Interval.neg(interval));

		interval = Interval.of(-4, -2);
		assertInterval(2, 4, Interval.neg(interval));

		interval = Interval.of(Integer.MAX_VALUE);
		assertInterval(Integer.MIN_VALUE + 1, Interval.neg(interval));

		interval = Interval.of(Integer.MIN_VALUE + 1);
		assertInterval(Integer.MAX_VALUE, Interval.neg(interval));

		interval = Interval.of(Integer.MIN_VALUE);
		assertInterval(Integer.MIN_VALUE, Interval.neg(interval));

		interval = Interval.of(Integer.MIN_VALUE, Integer.MIN_VALUE + 1);
		assertTrue(Interval.neg(interval).isTop());
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

		leftInterval = Interval.of(2, 4);
		rightInterval = Interval.of(Integer.MIN_VALUE);
		assertTrue(Interval.lt(leftInterval, rightInterval).isBottom());
	}

	protected void assertInterval(int expectedLower, int expectedUpper,
			Interval interval) {
		assertEquals(expectedLower, interval.getLower());
		assertEquals(expectedUpper, interval.getUpper());
	}

	protected void assertInterval(int expectedValue, Interval interval) {
		assertEquals(expectedValue, interval.getLower());
		assertEquals(expectedValue, interval.getUpper());
	}

}
