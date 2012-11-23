package it.polimi.elet.selflet.events.service;

import java.util.List;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;
import it.polimi.elet.selflet.negotiation.ServiceOfferModeEnum;

/**
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * @author silvia
 */
public class LocalOfferModeChangedEvent extends SelfletEvent {

	private final String serviceName;
	private final List<ServiceOfferModeEnum> modes;

	public LocalOfferModeChangedEvent(String service, List<ServiceOfferModeEnum> modes) {
		this.serviceName = service;
		this.modes = modes;
	}

	public String getServiceName() {
		return serviceName;
	}

	public List<ServiceOfferModeEnum> getModes() {
		return modes;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.LOCAL_OFFER_MODE_CHANGED;
	}

	@Override
	public String toString() {

		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("[");
		for (int i = 0; i < modes.size(); i++) {
			stringBuffer.append(" ");
			stringBuffer.append(modes.get(i));
		}

		return "Changing offer mode for service " + serviceName + " with " + stringBuffer + " ]";
	}
}