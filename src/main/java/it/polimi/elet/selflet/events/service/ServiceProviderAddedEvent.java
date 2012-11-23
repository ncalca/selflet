package it.polimi.elet.selflet.events.service;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;
import it.polimi.elet.selflet.negotiation.ServiceProvider;

/**
 * @author silvia
 */
public class ServiceProviderAddedEvent extends SelfletEvent {

	private final String service;
	private final ServiceProvider provider;

	public ServiceProviderAddedEvent(String service, ServiceProvider provider) {
		this.service = service;
		this.provider = provider;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.SERVICE_PROVIDER_ADDED;
	}

	public String getService() {
		return service;
	}

	public ServiceProvider getProvider() {
		return provider;
	}
}