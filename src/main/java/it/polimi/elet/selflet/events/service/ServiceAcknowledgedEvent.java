package it.polimi.elet.selflet.events.service;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;

public class ServiceAcknowledgedEvent extends SelfletEvent {

	private final String service;
	private final Boolean frequent;

	public ServiceAcknowledgedEvent(String service, Boolean frequent) {
		this.service = service;
		this.frequent = frequent;
	}

	public boolean isFrequent() {
		return frequent;
	}

	public String getService() {
		return service;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.ACKNOWLEDGE_RECEIVED_SERVICE;
	}

}
