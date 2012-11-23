package it.polimi.elet.selflet.events.service;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;
import it.polimi.elet.selflet.service.RunningService;

/**
 * This class represents the command to execute a service
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class LocalReqLocalExeExecuteEvent extends SelfletEvent implements IServiceEvent {

	private final String serviceName;
	private final RunningService callingService;

	/**
	 * @param serviceName
	 *            service name to execute
	 * @param callingService
	 *            service that called this service. Initially, when the selflet
	 *            is started its value is null.
	 * */
	public LocalReqLocalExeExecuteEvent(String serviceName, RunningService callingService) {
		this.serviceName = serviceName;
		this.callingService = callingService;
	}

	public String getServiceName() {
		return serviceName;
	}

	public RunningService getCallingService() {
		return callingService;
	}

	public boolean isRequestEvent() {
		return false;
	}

	public boolean isLocalExecutedService() {
		return false;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.LOCAL_REQUEST_LOCAL_EXECUTION_EXECUTE;
	}

	public String toString() {
		return "Execute service " + serviceName + " called by " + callingService;
	}

}
