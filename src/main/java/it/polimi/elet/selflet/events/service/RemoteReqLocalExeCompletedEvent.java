package it.polimi.elet.selflet.events.service;

import java.io.Serializable;

import polimi.reds.MessageID;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.service.RunningService;

/**
 * Completion notification for remote request local execution
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class RemoteReqLocalExeCompletedEvent extends SelfletEvent implements ILocalExecutedServiceEvent {

	private final String serviceName;
	private final ISelfLetID requestorId;
	private final MessageID msgId;
	private final RunningService runningService;
	private final Serializable output;
	private final Long responseTime;

	public RemoteReqLocalExeCompletedEvent(String serviceName, Serializable lastOutput, ISelfLetID requestor, MessageID msgId,
			RunningService runningService, Long responseTime) {
		this.serviceName = serviceName;
		this.output = lastOutput;
		this.requestorId = requestor;
		this.msgId = msgId;
		this.runningService = runningService;
		this.responseTime = responseTime;
	}

	public String getServiceName() {
		return serviceName;
	}

	public Serializable getOutput() {
		return output;
	}

	public ISelfLetID getRequestorId() {
		return requestorId;
	}

	public MessageID getMsgId() {
		return msgId;
	}

	public RunningService getRunningService() {
		return this.runningService;
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
		return EventTypeEnum.REMOTE_REQUEST_LOCAL_EXECUTION_COMPLETED;
	}

	public String toString() {
		return "Service " + serviceName + " requested remotely and executed locally completed";
	}

}
