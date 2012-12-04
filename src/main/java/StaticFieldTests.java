import org.junit.Test;

/**
 * Tests methods where array sizes and indices are supplied from static fields.
 */
public class StaticFieldTests extends AbstractTest {

	public static int VALUE_TWO = 2;
	public static final int CONSTANT_SIX;

	static {
		CONSTANT_SIX = 5 + 1;
	}

	public void main(String[] args) {
		testSafeConstantStaticField();
	}

	public static void testSafeConstantStaticField() {
		int[] array = new int[7];
		array[CONSTANT_SIX] = 1;
	}

	@Test
	public void _testSafeConstantStaticField() {
		assertSafe("testSafeConstantStaticField");
	}

	public static void testUnsafeConstantStaticField() {
		int[] array = new int[6];
		array[CONSTANT_SIX] = 1;
	}

	@Test
	public void _testUnsafeConstantStaticField() {
		assertMaybeUnsafe("testUnsafeConstantStaticField");
	}

}