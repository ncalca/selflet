package it.polimi.elet.selflet.negotiation.messageHandlers;

import org.apache.log4j.Logger;

import it.polimi.elet.selflet.behavior.State;
import it.polimi.elet.selflet.events.DispatchingUtility;
import it.polimi.elet.selflet.events.IEventDispatcher;
import it.polimi.elet.selflet.events.service.ServiceSentEvent;
import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.message.IMessageHandler;
import it.polimi.elet.selflet.message.ISelfLetMsgFactory;
import it.polimi.elet.selflet.message.SelfLetMessageTypeEnum;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.negotiation.IServicePackFactory;
import it.polimi.elet.selflet.negotiation.ServiceOfferModeEnum;
import it.polimi.elet.selflet.negotiation.ServicePack;
import it.polimi.elet.selflet.negotiation.servicepack.ServicePackFactory;
import it.polimi.elet.selflet.service.Service;

/**
 * An implementation for DOWNLOAD_ACHIEVABLE_SERVICE message
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class DownloadAchievableServiceMessageHandler implements ISelfletMessageHandler {

	private static final Logger LOG = Logger.getLogger(DownloadAchievableServiceMessageHandler.class);

	private final ISelfLetMsgFactory selfletMessageFactory;
	private final IMessageHandler messageHandler;
	private final IServiceKnowledge serviceKnowledge;
	private final IEventDispatcher dispatcher;
	

	public DownloadAchievableServiceMessageHandler(IServiceKnowledge serviceKnowledge, ISelfLetMsgFactory selfletMessageFactory,
			IMessageHandler messageHandler, IEventDispatcher dispatcher) {
		this.serviceKnowledge = serviceKnowledge;
		this.selfletMessageFactory = selfletMessageFactory;
		this.messageHandler = messageHandler;
		this.dispatcher = dispatcher;
	}

	@Override
	public void handleMessage(SelfLetMsg message) {
		LOG.debug("Negotiation Manager: received event " + "DOWNLOAD ACHIEVABLE SERVICE");

		String serviceName = (String) message.getContent();

		if (serviceName == null) {
			SelfLetMsg reply = selfletMessageFactory.newNegotiationErrorMsg(message.getFrom(), "The received parameter is null", null);
			messageHandler.reply(reply, message.getId());
			return;
		}

		Service service = serviceKnowledge.getProperty(serviceName);

		if (service == null || service.getDefaultBehavior() == null /*|| !service.hasLocalOfferMode(ServiceOfferModeEnum.CanTeach)*/) {

			String errorMsg = "Service: " + service;
			if (service != null) {
				errorMsg += ", default behavior: " + service.getDefaultBehavior();
				errorMsg += ", offer modes: " + service.getOfferModes();
			}
			LOG.error(errorMsg);
			SelfLetMsg reply = selfletMessageFactory.newNegotiationErrorMsg(message.getFrom(), errorMsg, null);
			messageHandler.reply(reply, message.getId());
			return;
		}

		// here is where behavior is passed within a message to the
		// requesting selflet

//		IServicePackFactory servicePackFactory = new ServicePackFactory();

		// TODO check again these things...
//		ServicePack servicePack = servicePackFactory.createServicePackForService(service);
//		SelfLetMsg reply = selfletMessageFactory.newGetServiceMsgReply(message.getFrom(), servicePack);
//		messageHandler.reply(reply, message.getId());
//
//		fireServiceSentEvent(service.getName(), message.getFrom());
		
		//TODO this is not well written but now should do all the work...
		sendSubServicesIfNecessary(service, message.getFrom(), message);
		packAndSendToProvider(service, message.getFrom(), message);
	}

	private void fireServiceSentEvent(String name, ISelfLetID from) {
		DispatchingUtility.dispatchEvent(dispatcher, ServiceSentEvent.class, name, from);
	}

	@Override
	public SelfLetMessageTypeEnum getTypeOfHandledMessage() {
		return SelfLetMessageTypeEnum.DOWNLOAD_ACHIEVABLE_SERVICE;
	}
	
	private void sendSubServicesIfNecessary(Service service,
			ISelfLetID provider, SelfLetMsg message) {

		for (State serviceState : service.getDefaultBehavior().getStates()) {
			LOG.info("service state: " + serviceState);
			if (serviceState.isFinalState() || serviceState.isInitialState() || serviceState.getName().equals(service.getName())) {
				LOG.info("skipping state");
				continue;
			}

			String subServiceName = serviceState.getName();
			try {
				Service subservice = serviceKnowledge.getProperty(subServiceName);
				sendSubServicesIfNecessary(subservice, provider, message);
				packAndSendToProvider(subservice, provider, message);
			} catch (NotFoundException e) {
				throw new NotFoundException("error in getting sub services: "
						+ e);
			} catch (NullPointerException e){
				LOG.error("No subservice with that name");
			}

		}
	}
	
	private void packAndSendToProvider(Service service, ISelfLetID provider, SelfLetMsg message) {
		if(service.getMaxResponseTimeInMsec() <= 0){
			throw new NotFoundException("mrt is 0. Problem with service");
		}
		IServicePackFactory servicePackFactory = new ServicePackFactory();
		ServicePack servicePack = servicePackFactory
				.createServicePackForService(service);
		LOG.info("sending service " + service.getName() + "[");
		LOG.info("demand: " + service.getServiceDemand());
		LOG.info("mrp: " + service.getMaxResponseTimeInMsec() + "]");
		SelfLetMsg knowledge = selfletMessageFactory.newServiceTeachMsg(servicePack, provider);
		messageHandler.send(knowledge);

//		fireServiceSentEvent(service.getName(), message.getFrom());
	}

}
