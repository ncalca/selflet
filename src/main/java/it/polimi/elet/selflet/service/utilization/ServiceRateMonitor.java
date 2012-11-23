package it.polimi.elet.selflet.service.utilization;

import static it.polimi.elet.selflet.events.EventTypeEnum.*;

import it.polimi.elet.selflet.configuration.SelfletConfiguration;
import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.service.IServiceEvent;
import it.polimi.elet.selflet.utilities.NumberFormat;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;

/**
 * Computes request and completion rates for services.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ServiceRateMonitor implements IServiceRateMonitor {

	private static final Logger LOG = Logger.getLogger(ServiceRateMonitor.class);

	private static final int WINDOW_LENGTH_SEC = Math.max(SelfletConfiguration.getSingleton().requestRateWindowMonitorInSec, 1);
	private static final int WINDOW_LENGTH_MSEC = WINDOW_LENGTH_SEC * 1000;

	private static final Set<EventTypeEnum> requestEvents = Sets.immutableEnumSet(REMOTE_REQUEST_LOCAL_EXECUTION_EXECUTE,
			LOCAL_REQUEST_LOCAL_EXECUTION_EXECUTE, LOCAL_REQUEST_REMOTE_EXECUTION_EXECUTE);

	private static final Set<EventTypeEnum> completionEvents = Sets.immutableEnumSet(REMOTE_REQUEST_LOCAL_EXECUTION_COMPLETED,
			LOCAL_REQUEST_LOCAL_EXECUTION_COMPLETED, LOCAL_REQUEST_REMOTE_EXECUTION_COMPLETED);

//	private final long startTime = System.currentTimeMillis();
	private final List<Request> requestHistory = Lists.newCopyOnWriteArrayList();
	private final List<Request> completionHistory = Lists.newCopyOnWriteArrayList();

	@Override
	public void sendEvent(IServiceEvent serviceEvent) {
		LOG.debug("Received event " + serviceEvent);

		Request request = new Request(serviceEvent.getServiceName());
		EventTypeEnum eventType = serviceEvent.getEventType();

		if (isRequestEvent(eventType)) {
			requestHistory.add(request);
		}

		if (isCompletionEvent(eventType)) {
			completionHistory.add(request);
		}
	}

	@Override
	public List<ServiceRate> getRequestRates() {
		cleanOldEntries(requestHistory);
		Multiset<String> eventOccurrences = computeOccurrences(requestHistory);
		return computeRates(eventOccurrences);
	}

	@Override
	public List<ServiceRate> getThroughput() {
		cleanOldEntries(completionHistory);
		Multiset<String> eventOccurrences = computeOccurrences(completionHistory);
		return computeRates(eventOccurrences);
	}

	private List<ServiceRate> computeRates(Multiset<String> eventOccurrences) {

		List<ServiceRate> requestRates = Lists.newArrayList();
		double windowLengthInSec = computeWindowLengthInSec();

		for (String serviceName : eventOccurrences) {
			double requestRate = ((double) eventOccurrences.count(serviceName)) / windowLengthInSec;
			ServiceRate serviceRate = new ServiceRate(serviceName, requestRate);
			requestRates.add(serviceRate);
		}

		return requestRates;
	}

	private double computeWindowLengthInSec() {
		return WINDOW_LENGTH_SEC;
		// long now = System.currentTimeMillis();
		// long runningTime = (now - startTime) / 1000;
		// return (runningTime > WINDOW_LENGTH_SEC) ? WINDOW_LENGTH_SEC :
		// Math.max(runningTime, 1);
	}

	private Multiset<String> computeOccurrences(List<Request> history) {
		Multiset<String> eventOccurrences = HashMultiset.create();
		for (Request request : history) {
			eventOccurrences.add(request.serviceName);
		}
		return eventOccurrences;
	}

	private void cleanOldEntries(List<Request> history) {
		long now = System.currentTimeMillis();
		for (Request request : history) {
			if (request.isOld(now)) {
				history.remove(request);
			}

		}
	}

	private boolean isCompletionEvent(EventTypeEnum eventType) {
		return completionEvents.contains(eventType);
	}

	private boolean isRequestEvent(EventTypeEnum eventType) {
		return requestEvents.contains(eventType);
	}

	/** Inner classes */

	/**
	 * Storage inner class representing the (request/completion) rate for a
	 * service
	 */
	public static class ServiceRate {

		private final String serviceName;
		private final double requestRate;

		public ServiceRate(String serviceName, double requestRate) {
			if (requestRate <= 0) {
				throw new IllegalArgumentException("Request rate cannot be negative");
			}
			this.serviceName = serviceName;
			this.requestRate = requestRate;
		}

		public String getServiceName() {
			return serviceName;
		}

		public double getRequestRate() {
			return requestRate;
		}

		public String toString() {
			return getServiceName() + ": " + NumberFormat.formatNumber(requestRate);
		}

	}

	// storage inner class representing a request
	public static class Request {
		final String serviceName;
		final long creationTimeInMillis;

		public Request(String serviceName) {
			this.serviceName = serviceName;
			this.creationTimeInMillis = System.currentTimeMillis();
		}

		public boolean isOld(long now) {
			return (now - creationTimeInMillis) > WINDOW_LENGTH_MSEC;
		}

		@Override
		public String toString() {
			return serviceName + " (" + new Date(creationTimeInMillis) + ")";
		}

	}

}
