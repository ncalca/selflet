package it.polimi.elet.selflet.load;

import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.*;

public class LoadGeneratorTest {

	private static final double TOLERANCE = 2;

	@Test
	@Ignore
	public void testLoadGenerator() {
		Boolean doCpuTest = true;
		Boolean doHdTest = true;
		double testDurationInSec = 0.5;
		double testDurationInMillis = testDurationInSec * 1000;

		LoadGenerator loadGenerator = new LoadGenerator(doCpuTest, doHdTest, LoadType.Constant, testDurationInSec);
		long before = System.currentTimeMillis();
		loadGenerator.runTest();
		long after = System.currentTimeMillis();
		long durationInMillis = after - before;
		assertTrue(durationInMillis >= testDurationInMillis);
		assertTrue(durationInMillis <= (testDurationInMillis * TOLERANCE));
	}
}
