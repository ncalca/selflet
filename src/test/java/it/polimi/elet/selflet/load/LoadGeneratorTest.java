package it.polimi.elet.selflet.load;

import org.junit.Test;

import static junit.framework.Assert.*;

public class LoadGeneratorTest {

	private static final double TOLERANCE = 2;

	@Test
	public void testLoadGenerator() {
		Boolean doCpuTest = true;
		Boolean doHdTest = true;
		double testDurationInSec = 2;
		double testDurationInMillis = testDurationInSec * 1000;

		LoadGenerator loadGenerator = new LoadGenerator(doCpuTest, doHdTest, LoadType.Constant, testDurationInSec);
		long before = System.currentTimeMillis();
		loadGenerator.runTest();
		long after = System.currentTimeMillis();
		long duration = after - before;
		assertTrue(duration >= testDurationInMillis);
		assertTrue(duration <= (testDurationInMillis * TOLERANCE));
	}
}
