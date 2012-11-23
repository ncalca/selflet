package it.polimi.elet.selflet.events.service;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;
import it.polimi.elet.selflet.id.ISelfLetID;

/**
 * @author silvia
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 */
public class ServiceProviderRemovedEvent extends SelfletEvent {

	private final String service;
	private final ISelfLetID providerId;

	public ServiceProviderRemovedEvent(String serviceName, ISelfLetID providerId) {
		this.service = serviceName;
		this.providerId = providerId;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.SERVICE_PROVIDER_REMOVED;
	}

	public String getService() {
		return service;
	}

	public ISelfLetID getProvider() {
		return providerId;
	}

}