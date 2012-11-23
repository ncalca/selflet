package it.polimi.elet.selflet.service.utilization;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

public class MonitoredConsumptionTest {

	private static final double DELTA = 0.01;
	private MonitoredConsumption monitoredConsumption;

	@Before
	public void setUp() {
		monitoredConsumption = new MonitoredConsumption();
	}

	@Test
	public void testMonitoredConsumption() {
		monitoredConsumption.addToHistory(1, 1);
		assertEquals(1, monitoredConsumption.getCPUTime(), DELTA);
		assertEquals(1, monitoredConsumption.getWallTime(), DELTA);
	}

}
