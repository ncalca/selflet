package it.polimi.elet.selflet.events.service;

import java.util.Map;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.service.RunningService;

public class LocalReqRemoteExeExecuteEvent extends SelfletEvent implements IServiceEvent {

	private final String serviceName;
	private final Map<String, Object> parameters;
	private final ISelfLetID receiverProvider;
	private final RunningService callingService;

	public LocalReqRemoteExeExecuteEvent(String serviceName, Map<String, Object> parameters, ISelfLetID receiverProvider, RunningService callingService) {
		if (serviceName == null || receiverProvider == null || callingService == null) {
			throw new IllegalArgumentException("One of the passed argument for LocalReqRemoteExeExecuteEvent is null");
		}
		this.serviceName = serviceName;
		this.parameters = parameters;
		this.receiverProvider = receiverProvider;
		this.callingService = callingService;
	}

	public String getServiceName() {
		return serviceName;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public boolean isLocalExecutedService() {
		return false;
	}

	public boolean isRequestEvent() {
		return true;
	}

	public ISelfLetID getReceiverProvider() {
		return receiverProvider;
	}

	public RunningService getCallingService() {
		return callingService;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.LOCAL_REQUEST_REMOTE_EXECUTION_EXECUTE;
	}

	@Override
	public String toString() {
		return "Local req remote execution for service " + serviceName + ", executed in " + receiverProvider;
	}

}
