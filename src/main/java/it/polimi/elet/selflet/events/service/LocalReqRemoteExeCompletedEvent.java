package it.polimi.elet.selflet.events.service;

import polimi.reds.MessageID;
import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;

public class LocalReqRemoteExeCompletedEvent extends SelfletEvent implements IServiceEvent {

	private final String serviceName;
	private final Object output;
	private final MessageID messageID;

	public LocalReqRemoteExeCompletedEvent(String serviceName, Object output, MessageID messageID) {
		this.serviceName = serviceName;
		this.output = output;
		this.messageID = messageID;
	}

	public String getServiceName() {
		return serviceName;
	}

	public Object getOutput() {
		return output;
	}

	public boolean isRequestEvent() {
		return true;
	}

	public boolean isLocalExecutedService() {
		return false;
	}

	public MessageID getMessageID() {
		return messageID;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.LOCAL_REQUEST_REMOTE_EXECUTION_COMPLETED;
	}

	public String toString() {
		return "Service " + serviceName + " requested locally and executed remotely completed";
	}

}
