import org.junit.Test;

/**
 * Tests methods where array sizes and indices are supplied from static fields.
 */
public class StaticFieldTests extends AbstractTest {

	public static final int CONSTANT_SIX;

	static {
		CONSTANT_SIX = 5 + 1;
	}

	public static void main(String[] args) {
		testUnsafeConstantStaticField();
	}

	@Unsafe
	public static void testUnsafeConstantStaticField() {
		int[] array = new int[6];
		array[CONSTANT_SIX] = 1;
	}

	@Test
	public void _testUnsafeConstantStaticField() {
		assertAnalysis("testUnsafeConstantStaticField");
	}

}
