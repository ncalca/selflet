package it.polimi.elet.selflet.negotiation.messageHandlers;

import it.polimi.elet.selflet.events.DispatchingUtility;
import it.polimi.elet.selflet.events.IEventDispatcher;
import it.polimi.elet.selflet.events.service.ServiceProviderAddedEvent;
import it.polimi.elet.selflet.events.service.ServiceProviderRemovedEvent;
import it.polimi.elet.selflet.exceptions.AlreadyPresentException;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.message.SelfLetMessageTypeEnum;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.negotiation.AchievableServiceParam;
import it.polimi.elet.selflet.negotiation.ServiceOfferModeEnum;
import it.polimi.elet.selflet.negotiation.ServiceProvider;
import it.polimi.elet.selflet.service.Service;

import org.apache.log4j.Logger;

/**
 * An implementation for ADVERTISE_ACHIEVABLE_SERVICE message handler
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class AdvertiseAchievableServiceMessageHandler implements ISelfletMessageHandler {

	private static final Logger LOG = Logger.getLogger(AdvertiseAchievableServiceMessageHandler.class);

	private final IServiceKnowledge serviceKnowledge;
	private final IEventDispatcher dispatcher;

	public AdvertiseAchievableServiceMessageHandler(IServiceKnowledge serviceKnowledge, IEventDispatcher dispatcher) {
		this.serviceKnowledge = serviceKnowledge;
		this.dispatcher = dispatcher;
	}

	@Override
	public void handleMessage(SelfLetMsg message) {
		LOG.debug("Negotiation Manager: received event " + "ADVERTISE ACHIEVABLE SERVICE");

		AchievableServiceParam param = (AchievableServiceParam) message.getContent();

		if (param == null) {
			return;
		}

		String serviceName = param.getAchievableService();
		ServiceOfferModeEnum mode = param.getMode();
		ISelfLetID providerId = param.getProviderId();

		// If the service is null, someone is trying to tell us that an
		// SelfLet is probably offline and must be removed from the list
		// of providers
		if (serviceName == null) {

			for (Service service : serviceKnowledge.getProperties().values()) {

				service.removeProvider(providerId);
				fireServiceProviderRemovedEvent(service.getName(), providerId);

			}

			return;
		}

		Service service = serviceKnowledge.getProperty(serviceName);

		if (service == null) {
			// We don't know the service, but the message is telling us
			// to remove a provider, so we can return now
			if (mode == ServiceOfferModeEnum.None) {
				return;
			}

			// Otherwise create the service
			service = new Service(serviceName);

			try {
				serviceKnowledge.setProperty(serviceName, service);
			} catch (AlreadyPresentException e) {
				LOG.error("Given service is already known to the selflet", e);
			}
		}

		if (mode == ServiceOfferModeEnum.None) {

			service.removeProvider(providerId);
			fireServiceProviderRemovedEvent(service.getName(), providerId);

			LOG.debug("Provider " + providerId + " of service " + serviceName + " removed");

		} else {

			ServiceProvider newProvider = new ServiceProvider(providerId, mode);

			service.addProvider(newProvider);
			fireServiceProviderAddedEvent(serviceName, newProvider);
		}

	}

	private void fireServiceProviderAddedEvent(String serviceName, ServiceProvider newProvider) {
		DispatchingUtility.dispatchEvent(dispatcher, ServiceProviderAddedEvent.class, serviceName, newProvider);
	}

	private void fireServiceProviderRemovedEvent(String service, ISelfLetID providerId) {
		DispatchingUtility.dispatchEvent(dispatcher, ServiceProviderRemovedEvent.class, service, providerId);
	}

	@Override
	public SelfLetMessageTypeEnum getTypeOfHandledMessage() {
		return SelfLetMessageTypeEnum.ADVERTISE_ACHIEVABLE_SERVICE;
	}

}
