package it.polimi.elet.selflet.negotiation;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletComponent;
import it.polimi.elet.selflet.events.service.LocalOfferModeChangedEvent;
import it.polimi.elet.selflet.events.service.LocalReqRemoteExeExecuteEvent;
import it.polimi.elet.selflet.events.service.ServiceProviderRemovedEvent;
import it.polimi.elet.selflet.exceptions.InvalidValueException;
import it.polimi.elet.selflet.exceptions.NegotiationErrorException;
import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.id.BroadcastSelfLetID;
import it.polimi.elet.selflet.id.DispatcherID;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.id.NeighborsID;
import it.polimi.elet.selflet.knowledge.IKnowledgesContainer;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.message.IMessageHandler;
import it.polimi.elet.selflet.message.ISelfLetMsgFactory;
import it.polimi.elet.selflet.message.MessageHandlerMessageBinder;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.negotiation.messageHandlers.ISelfletMessageHandlerFactory;
import it.polimi.elet.selflet.negotiation.servicepack.ServicePackInstaller;
import it.polimi.elet.selflet.service.RunningService;
import it.polimi.elet.selflet.service.Service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import polimi.reds.MessageID;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Default implementation of the INegoatiationManager interface.
 * 
 * @author Davide Devescovi
 * @author Silvia Bindelli
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 */

@Singleton
public class NegotiationManager extends SelfletComponent implements INegotiationManager {

	private static final Logger LOG = Logger.getLogger(NegotiationManager.class);

	private static final int DEFAULT_NUMBER_OF_ATTEMPTS = 3;

	private final IServiceKnowledge serviceKnowledge;
	private final ISelfLetMsgFactory selfLetMsgFactory;
	private final IMessageHandler messageHandler;
	private final IServiceProviderSearcher serviceProviderSearcher;
	private final IServicePackInstallerFactory servicePackInstallerFactory;

	@Inject
	public NegotiationManager(IMessageHandler messageHandler, IKnowledgesContainer knowledges, ISelfLetMsgFactory selfLetMsgFactory,
			IServicePackInstallerFactory servicePackInstallerFactory, ISelfletMessageHandlerFactory selfletMessageHandlerFactory) {
		this.serviceKnowledge = knowledges.getServiceKnowledge();
		this.messageHandler = messageHandler;

		// replace with injection
		selfletMessageHandlerFactory.setDispatcher(dispatcher);
		this.selfLetMsgFactory = selfLetMsgFactory;
		this.servicePackInstallerFactory = servicePackInstallerFactory;
		this.serviceProviderSearcher = new ServiceProviderSearcher(dispatcher, serviceKnowledge, messageHandler, selfLetMsgFactory);

		bindMessagesToEvents();
	}

	public ServiceProvider getNeededServiceProvider(String serviceName, ServiceAskModeEnum mode) {
		return getNeededServiceProvider(serviceName, mode, DEFAULT_NUMBER_OF_ATTEMPTS);
	}

	public ServiceProvider getNeededServiceProvider(String serviceName, ServiceAskModeEnum mode, int broadcastAttempts) {
		return serviceProviderSearcher.getServiceProvider(serviceName, mode, broadcastAttempts);
	}

	@Override
	public void redirectRequestToProvider(ServiceExecutionParameter serviceParam, ISelfLetID receiverProvider, RunningService callingService) {
		if (serviceParam == null || receiverProvider == null || callingService == null) {
			throw new IllegalArgumentException("Cannot invoked redirect. Null parameters");
		}
		fireLocalReqRemoteExeExecuteEvent(serviceParam.getServiceName(), serviceParam.getServiceParameters(), receiverProvider, callingService);
	}

	@Override
	public void istantiateNewSelflet() {
		SelfLetMsg message = selfLetMsgFactory.newIstantiateNewSelfletMsg();
		messageHandler.send(message);
		// MessageID messageID =
		// SelfLetMsg reply = messageHandler.retrieveReply(messageID,
		// LONG_TIMEOUT);
		// return (ISelfLetID) reply.getContent();
	}

	public void downloadService(String serviceName, ISelfLetID providerId, int timeout) {

		// TODO move to separated class
		// Creates request message
		SelfLetMsg serviceRequest = selfLetMsgFactory.newGetServiceMsg(providerId, serviceName);

		LOG.debug("Trying to download " + serviceName + " from " + providerId);

		// send message
		MessageID messageId = messageHandler.send(serviceRequest);

		SelfLetMsg reply = null;
		try {
			// wait for reply
			reply = messageHandler.retrieveReply(messageId, timeout);
			LOG.debug("Retrieved reply: " + reply);
		} catch (NotFoundException e) {
			// Notify everyone
			// advertiser.advertiseAchievableService(null,
			// ServiceOfferModeEnum.None);

			// No replies in the given timeout
			throw new NegotiationErrorException("Remote error: timeout");
		} catch (InvalidValueException e) {
			LOG.error("Timeout value is not valid", e);
		}

		switch (reply.getType()) {

		case DOWNLOAD_ACHIEVABLE_SERVICE_REPLY:
			LOG.debug("Service downloaded! " + serviceName);
			installDownloadedService((ServicePack) reply.getContent());
			break;

		case NEGOTIATION_ERROR:
			NegotiationErrorParam param = (NegotiationErrorParam) reply.getContent();
			throw new NegotiationErrorException(param.getMessage(), param.getCause());

		default:
			// This means we got a reply which has nothing to do with our
			// request.
			// Better remove the provider from all the goals and notify the
			// other nodes, as the unexpected behavior might mean it is not
			// working correctly

			for (Service service : serviceKnowledge.getProperties().values()) {
				service.removeProvider(providerId);
				fireServiceProviderRemovedEvent(service.getName(), providerId);
			}

			// advertiser.advertiseAchievableService(null,
			// ServiceOfferModeEnum.None);

			throw new NegotiationErrorException("Remote error: received unexpected reply");

		}
	}

	private void installDownloadedService(ServicePack servicePack) {
		ServicePackInstaller servicePackInstaller = servicePackInstallerFactory.createInstance();
		servicePackInstaller.install(servicePack);
	}

	public void setAchievableServiceOfferMode(String serviceName, ServiceOfferModeEnum mode, boolean active) {

		Service service = serviceKnowledge.getProperty(serviceName);

		if (service == null) {
			throw new NotFoundException("Couldn't find service " + serviceName);
		}

		service.setLocalOfferMode(mode, active);
		fireLocalOfferModeChangedEvent(serviceName);
	}

	public void advertiseAchievableService(String serviceName, ServiceOfferModeEnum mode) {
		// advertiser.advertiseAchievableService(serviceName, mode);
	}

	private List<ServiceOfferModeEnum> getAchievableServiceOfferModes(String serviceName) {
		Service service = serviceKnowledge.getProperty(serviceName);
		return service.getOfferModes();
	}

	private void bindMessagesToEvents() {
		MessageHandlerMessageBinder messageBinder = new MessageHandlerMessageBinder(messageHandler);
		messageBinder.register();
	}

	@Override
	public void notifySelfletRemoval() {
		SelfLetMsg neighborsSelfletMsg = selfLetMsgFactory.newRemovalSelfletMessage(new BroadcastSelfLetID());
		messageHandler.send(neighborsSelfletMsg);
//		SelfLetMsg dispatcherSelfletMsg = selfLetMsgFactory.newRemovalSelfletMessage(new DispatcherID());
//		messageHandler.send(dispatcherSelfletMsg);
	}

	private void fireLocalOfferModeChangedEvent(String service) {
		dispatchEvent(LocalOfferModeChangedEvent.class, service, getAchievableServiceOfferModes(service));
	}

	private void fireServiceProviderRemovedEvent(String serviceName, ISelfLetID providerId) {
		dispatchEvent(ServiceProviderRemovedEvent.class, serviceName, providerId);
	}

	private void fireLocalReqRemoteExeExecuteEvent(String serviceName, Map<String, Object> parameters, ISelfLetID receiverProvider,
			RunningService callingService) {
		dispatchEvent(LocalReqRemoteExeExecuteEvent.class, serviceName, parameters, receiverProvider, callingService);
	}

	@Override
	public ImmutableSet<EventTypeEnum> getReceivedEvents() {
		return ImmutableSet.of();
	}

}