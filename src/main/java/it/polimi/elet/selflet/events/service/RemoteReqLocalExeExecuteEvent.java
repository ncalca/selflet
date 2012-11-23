package it.polimi.elet.selflet.events.service;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;
import it.polimi.elet.selflet.message.SelfLetMsg;

/**
 * A remote service request for a local execution
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class RemoteReqLocalExeExecuteEvent extends SelfletEvent implements IServiceEvent {

	private final String serviceName;
	private final SelfLetMsg requestMsg;

	public RemoteReqLocalExeExecuteEvent(String serviceName, SelfLetMsg requestMsg) {
		this.serviceName = serviceName;
		this.requestMsg = requestMsg;
	}

	public String getServiceName() {
		return serviceName;
	}

	public SelfLetMsg getMsg() {
		return requestMsg;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.REMOTE_REQUEST_LOCAL_EXECUTION_EXECUTE;
	}

	public boolean isRequestEvent() {
		return false;
	}

	public boolean isLocalExecutedService() {
		return false;
	}

	@Override
	public String toString() {
		return "Execute service " + serviceName + " called by remote node " + requestMsg.getFrom();
	}
}
