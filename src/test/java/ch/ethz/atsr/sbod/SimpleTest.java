package ch.ethz.atsr.sbod;

import static org.junit.Assert.*;

import org.junit.Test;

public class SimpleTest {

	@Test
	public void test() {
		String javaFilePath = SimpleTest.class.getResource(
				"/SimpleBufferOverflow.java").getPath();
		assertFalse(javaFilePath.isEmpty());
	}

}
