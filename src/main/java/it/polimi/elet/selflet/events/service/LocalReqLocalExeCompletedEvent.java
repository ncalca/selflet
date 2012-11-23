package it.polimi.elet.selflet.events.service;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;
import it.polimi.elet.selflet.service.RunningService;

/**
 * @author Nicola
 */
public class LocalReqLocalExeCompletedEvent extends SelfletEvent implements ILocalExecutedServiceEvent {

	private final String serviceName;
	private final RunningService callingService;
	private final RunningService runningService;
	private final Object output;
	private final long responseTime;

	/**
	 * @param serviceName
	 *            the service executed
	 * @param callingService
	 *            runningService thread instance that requested this service
	 * @param runningService
	 *            the runningService thread that executed this service
	 * @param output
	 *            result of the service
	 * @param responseTime
	 */
	public LocalReqLocalExeCompletedEvent(String serviceName, RunningService callingService, RunningService runningService, Object output, Long responseTime) {
		this.serviceName = serviceName;
		this.callingService = callingService;
		this.runningService = runningService;
		this.output = output;
		this.responseTime = responseTime;
	}

	public String getServiceName() {
		return serviceName;
	}

	public RunningService getCallingService() {
		return callingService;
	}

	public RunningService getRunningService() {
		return runningService;
	}

	public Object getOutput() {
		return output;
	}

	public boolean isRequestEvent() {
		return true;
	}

	public boolean isLocalExecutedService() {
		return true;
	}

	public long getResponseTime() {
		return responseTime;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.LOCAL_REQUEST_LOCAL_EXECUTION_COMPLETED;
	}

	@Override
	public String toString() {
		return "Service " + serviceName + " requested locally and executed locally completed";
	}

}
