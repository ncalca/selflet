package it.polimi.elet.selflet.service.utilization;

import java.util.Date;
import java.util.List;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.service.IServiceEvent;
import it.polimi.elet.selflet.service.utilization.ServiceRateMonitor.ServiceRate;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static junit.framework.Assert.*;

import static it.polimi.elet.selflet.events.EventTypeEnum.REMOTE_REQUEST_LOCAL_EXECUTION_EXECUTE;
import static it.polimi.elet.selflet.events.EventTypeEnum.REMOTE_REQUEST_LOCAL_EXECUTION_COMPLETED;

public class ServiceRateMonitorTest {

	private final static String TEST_SERVICE_1 = "test_service_1";
	private final static String TEST_SERVICE_2 = "test_service_2";

	private IServiceRateMonitor serviceRateMonitor;

	@Before
	public void setUp() {
		serviceRateMonitor = new ServiceRateMonitor();
	}

	@Test
	public void testSingleRequestEvent() {
		IServiceEvent event = createServiceEvent(REMOTE_REQUEST_LOCAL_EXECUTION_EXECUTE, TEST_SERVICE_1);

		serviceRateMonitor.sendEvent(event);

		assertEquals(1, serviceRateMonitor.getRequestRates().size());

		ServiceRate request = serviceRateMonitor.getRequestRates().get(0);
		assertEquals(TEST_SERVICE_1, request.getServiceName());

		assertTrue(request.getRequestRate() > 0);
	}

	@Test
	public void testTwoRequestEvents() {
		IServiceEvent event1 = createServiceEvent(REMOTE_REQUEST_LOCAL_EXECUTION_EXECUTE, TEST_SERVICE_1);
		IServiceEvent event2 = createServiceEvent(REMOTE_REQUEST_LOCAL_EXECUTION_EXECUTE, TEST_SERVICE_2);

		serviceRateMonitor.sendEvent(event1);
		serviceRateMonitor.sendEvent(event2);

		List<ServiceRate> rates = serviceRateMonitor.getRequestRates();
		assertEquals(2, rates.size());

		assertTrue(containsService(rates, TEST_SERVICE_1));
		assertTrue(containsService(rates, TEST_SERVICE_2));

		assertTrue(rates.get(0).getRequestRate() > 0);
		assertTrue(rates.get(1).getRequestRate() > 0);
	}

	@Test
	public void testOneRequestOneCompletion() {
		IServiceEvent event1 = createServiceEvent(REMOTE_REQUEST_LOCAL_EXECUTION_EXECUTE, TEST_SERVICE_1);
		IServiceEvent event2 = createServiceEvent(REMOTE_REQUEST_LOCAL_EXECUTION_COMPLETED, TEST_SERVICE_1);

		serviceRateMonitor.sendEvent(event1);
		serviceRateMonitor.sendEvent(event2);

		List<ServiceRate> requestRates = serviceRateMonitor.getRequestRates();
		List<ServiceRate> completionRates = serviceRateMonitor.getThroughput();

		assertEquals(1, requestRates.size());
		assertEquals(1, completionRates.size());

		assertTrue(containsService(requestRates, TEST_SERVICE_1));
		assertTrue(containsService(completionRates, TEST_SERVICE_1));

		assertTrue(requestRates.get(0).getRequestRate() > 0);
		assertTrue(completionRates.get(0).getRequestRate() > 0);

	}

	private boolean containsService(List<ServiceRate> rates, String serviceName) {
		for (ServiceRate serviceRate : rates) {
			if (serviceRate.getServiceName().equals(serviceName)) {
				return true;
			}
		}
		return false;
	}

	private IServiceEvent createServiceEvent(EventTypeEnum eventType, String test_service) {
		IServiceEvent event = mock(IServiceEvent.class);
		when(event.getEventType()).thenReturn(eventType);
		when(event.getServiceName()).thenReturn(test_service);
		when(event.getTimeStamp()).thenReturn(new Date());
		return event;
	}

}
