public class SimpleBufferOverflow {

	public static void main(String[] args) {
		int[] array = { 0, 1 };
		int outOfBoundsElement = array[2];
		System.out.println(outOfBoundsElement);
	}

}
