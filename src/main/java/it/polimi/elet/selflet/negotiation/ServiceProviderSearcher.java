package it.polimi.elet.selflet.negotiation;

import it.polimi.elet.selflet.events.DispatchingUtility;
import it.polimi.elet.selflet.events.IEventDispatcher;
import it.polimi.elet.selflet.events.service.ServiceProviderAddedEvent;
import it.polimi.elet.selflet.events.service.ServiceProviderRemovedEvent;
import it.polimi.elet.selflet.exceptions.InvalidValueException;
import it.polimi.elet.selflet.exceptions.NegotiationErrorException;
import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.message.IMessageHandler;
import it.polimi.elet.selflet.message.ISelfLetMsgFactory;
import it.polimi.elet.selflet.message.SelfLetMessageTypeEnum;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.service.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * An implementation of the service provider searcher
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ServiceProviderSearcher implements IServiceProviderSearcher {

	private static final Logger LOG = Logger.getLogger(ServiceProviderSearcher.class);

	private static final Integer TIME_OUT = 2000;
	private static final Integer DEFAULT_NUMBER_OF_ATTEMPS = 3;

	private final IServiceKnowledge serviceKnowledge;
	// private final Advertiser advertiser;
	private final IMessageHandler messageHandler;
	private final ISelfLetMsgFactory selfLetMsgFactory;
	private final IEventDispatcher dispatcher;

	public ServiceProviderSearcher(IEventDispatcher dispatchers, IServiceKnowledge serviceKnowledge, IMessageHandler messageHandler,
			ISelfLetMsgFactory selfLetMsgFactory) {
		this.serviceKnowledge = serviceKnowledge;
		// this.advertiser = advertiser;
		this.messageHandler = messageHandler;
		this.dispatcher = dispatchers;
		this.selfLetMsgFactory = selfLetMsgFactory;
	}

	@Override
	public ServiceProvider getServiceProvider(String serviceName, ServiceAskModeEnum askedMode) {
		return getServiceProvider(serviceName, askedMode, DEFAULT_NUMBER_OF_ATTEMPS);
	}

	public ServiceProvider getServiceProvider(String serviceName, ServiceAskModeEnum askedMode, int broadcastAttempts) {

		if (broadcastAttempts < 0) {
			throw new InvalidValueException("The number of broadcast attempts to try can't be less than 0");
		}

		int attempts = 0;

		LOG.debug("Looking for a provider for service " + serviceName);

		do {

			LOG.debug("Trying " + (attempts + 1) + "/" + broadcastAttempts + "...");
			Service neededService = serviceKnowledge.getProperty(serviceName);

			try {
				return getServiceProviderUsingLocalKnowledge(neededService, askedMode);
			} catch (NegotiationErrorException e) {
				LOG.debug("Trying to get service provider for " + serviceName + " using broadcast");
				try {
					return askBroadcastForNeededService(serviceName, askedMode, TIME_OUT);
				} catch (NegotiationErrorException e1) {
					attempts++;
				}
			}
		} while (attempts < broadcastAttempts);

		throw new NegotiationErrorException("No provider has been found for service " + serviceName + ". Tried for " + broadcastAttempts + " times");
	}

	public ServiceProvider getServiceProviderUsingLocalKnowledge(Service service, ServiceAskModeEnum askedMode) {

		ServiceProvider preferredProvider = service.getPreferredProvider(askedMode);

		// check what i know about this service
		switch (preferredProvider.getOfferMode()) {

		case Both:
			return preferredProvider;

		case CanDo:
			return preferredProvider;

		case CanTeach:
			return preferredProvider;

		case KnowsWhoCanBoth:
			return getNeededServiceProviderIndirectly(service.getName(), preferredProvider.getProviderID(), askedMode);

		case KnowsWhoCanDo:
			return getNeededServiceProviderIndirectly(service.getName(), preferredProvider.getProviderID(), askedMode);

		case KnowsWhoCanTeach:
			return getNeededServiceProviderIndirectly(service.getName(), preferredProvider.getProviderID(), askedMode);

		default:
			throw new NegotiationErrorException("Error while searching a provider for service " + service);
		}
	}

	public ServiceProvider getNeededServiceProviderIndirectly(String serviceName, ISelfLetID providerId, ServiceAskModeEnum mode) {

		LOG.debug("Getting service provider indirectly ");

		SelfLetMsg request = selfLetMsgFactory.newGetIndirectlyServiceProviderMsg(providerId, new NeededServiceParam(serviceName, mode));

		// send request
		Object msgId = messageHandler.send(request);

		SelfLetMsg reply = null;

		try {

			reply = messageHandler.retrieveReply(msgId, TIME_OUT);

		} catch (NotFoundException e) {

			// If no replies are received, return null after removing the
			// provider, as it's probably offline and not reliable anymore

			removeServiceProviderFromAllServices(providerId);

			// After that, tell everyone the provider is not reliable, by using
			// null in the goal field

			// advertiser.advertiseAchievableService(null,
			// ServiceOfferModeEnum.None, providerId);

			throw new NegotiationErrorException("Remote error: timeout", e);
		}

		// Should always be so, but check anyway
		if (reply == null) {
			throw new NullPointerException("Unexpected error");
		}

		if (reply.getType().equals(SelfLetMessageTypeEnum.GET_SERVICE_PROVIDER_INDIRECTLY_REPLY)) {

			ServiceProvider indirect = (ServiceProvider) reply.getContent();

			return serviceProviderIndirectlyReply(serviceName, indirect, mode, providerId);

		}

		if (reply.getType().equals(SelfLetMessageTypeEnum.NEGOTIATION_ERROR)) {
			// An error occurred on the other selflet, but it replied anyway:
			// better remove it from this goal, but not from every goal;
			// also, as before, no need to tell the other nodes, as we did
			// get a reply, so the node is alive.
			removeProviderFromService(serviceName, providerId);
			NegotiationErrorParam param = (NegotiationErrorParam) reply.getContent();
			throw new NegotiationErrorException("Remote error: " + param.getMessage(), param.getCause());
		}

		// This means we got a reply which has nothing to do with our
		// request.
		// Better remove the provider from all the goals and notify the
		// other nodes, as the unexpected behavior might mean it is not
		// working correctly
		removeServiceProviderFromAllServices(providerId);
		// advertiser.advertiseAchievableService(null,
		// ServiceOfferModeEnum.None, providerId);
		throw new NegotiationErrorException("Remote error: received unexpected reply");

	}

	private ServiceProvider serviceProviderIndirectlyReply(String serviceName, ServiceProvider indirectProvider, ServiceAskModeEnum mode, ISelfLetID providerId) {

		if (indirectProvider == null) {
			throw new NegotiationErrorException("The provider does no longer provides an indirect support for the service");
		}

		// Whatever the case, add the provider
		Service service = serviceKnowledge.getProperty(serviceName);

		service.addProvider(indirectProvider);
		fireServiceProviderAddedEvent(serviceName, indirectProvider);

		switch (indirectProvider.getOfferMode()) {

		case KnowsWhoCanBoth:
		case KnowsWhoCanDo:
		case KnowsWhoCanTeach:
			throw new NegotiationErrorException("A indirect service provider has been received instead of a direct one");
		case CanDo:
			if (mode == ServiceAskModeEnum.Teach) {
				throw new NegotiationErrorException("Not what we asked");
			}
			break;

		case CanTeach:
			if (mode == ServiceAskModeEnum.Do) {
				throw new NegotiationErrorException("Not what we asked");
			}
			break;
		}

		return indirectProvider; // All good

	}

	/**
	 * Remove a provider from all the existing services. Iterates over all
	 * services and remove the provider
	 * */
	private void removeServiceProviderFromAllServices(ISelfLetID providerId) {
		Iterator<String> services = serviceKnowledge.getProperties().keySet().iterator();
		while (services.hasNext()) {
			String serviceName = services.next();
			removeProviderFromService(serviceName, providerId);
		}
	}

	private void removeProviderFromService(String serviceName, ISelfLetID providerId) {
		Service service = serviceKnowledge.getProperty(serviceName);
		service.removeProvider(providerId);
		fireServiceProviderRemovedEvent(service.getName(), providerId);
	}

	public ServiceProvider askBroadcastForNeededService(String serviceName, ServiceAskModeEnum mode, int timeout) {

		NeededServiceParam param = new NeededServiceParam(serviceName, mode);

		SelfLetMsg requestMsg = selfLetMsgFactory.newAskNeededServiceMsg(param);

		Object id = messageHandler.send(requestMsg);

		List<SelfLetMsg> replies = null;

		LOG.debug("Broadcast request, searched reply with ID: " + id);

		try {
			replies = messageHandler.retrieveReplies(id, timeout);
		} catch (NotFoundException e) {
			LOG.debug("No replies found while asking in broadcast for service " + serviceName);
			throw new NegotiationErrorException("No replies found!", e);
		}

		if (replies == null || replies.isEmpty()) {
			throw new NegotiationErrorException("No replies found!");
		}

		List<ServiceProvider> providers = new ArrayList<ServiceProvider>();

		// For each reply try to add the providers to the goal
		for (SelfLetMsg reply : replies) {

			// The type doesn't match... let's ignore it and skip it
			if (!reply.getType().equals(SelfLetMessageTypeEnum.ASK_NEEDED_SERVICE_REPLY)) {
				LOG.debug("Unexpected message type in the reply");
				continue;
			}

			ServiceOfferModeEnum offerMode = (ServiceOfferModeEnum) reply.getContent();

			Service service = null;

			try {

				service = serviceKnowledge.getProperty(serviceName);

			} catch (NotFoundException e1) {

				// if missing goal then create it
				service = new Service(serviceName);
				serviceKnowledge.setProperty(serviceName, service);
			}

			ServiceProvider newProvider = new ServiceProvider(reply.getFrom(), offerMode);
			providers.add(newProvider);
			service.addProvider(newProvider);
			fireServiceProviderAddedEvent(serviceName, newProvider);
		}

		// if we have many replies choose randomly the default provider
		Collections.shuffle(providers);
		return providers.get(0);
	}

	public void fireServiceProviderAddedEvent(String serviceName, ServiceProvider provider) {
		DispatchingUtility.dispatchEvent(dispatcher, ServiceProviderAddedEvent.class, serviceName, provider);
	}

	public void fireServiceProviderRemovedEvent(String serviceName, ISelfLetID providerId) {
		DispatchingUtility.dispatchEvent(dispatcher, ServiceProviderRemovedEvent.class, serviceName, providerId);

	}

}
