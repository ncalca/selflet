package it.polimi.elet.selflet.events.service;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;

public class ServiceSentEvent extends SelfletEvent {

	private final String serviceName;
	private final String receiverSelflet;

	public ServiceSentEvent(String name, String from) {
		this.serviceName = name;
		this.receiverSelflet = from;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.SERVICE_SENT;
	}

	public String getReceiverSelflet() {
		return receiverSelflet;
	}

	public String getServiceName() {
		return serviceName;
	}

}
