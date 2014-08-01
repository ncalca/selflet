package it.polimi.elet.selflet.service;

import it.polimi.elet.selflet.behavior.State;
import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.message.IMessageHandler;
import it.polimi.elet.selflet.message.ISelfLetMsgFactory;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.negotiation.IServicePackFactory;
import it.polimi.elet.selflet.negotiation.ServicePack;
import it.polimi.elet.selflet.negotiation.servicepack.ServicePackFactory;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * An implementation of IServiceTeacher
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class ServiceTeacher implements IServiceTeacher {

	private static final Logger LOG = Logger.getLogger(ServiceTeacher.class);

	private final IMessageHandler messageHandler;
	private final ISelfLetMsgFactory selfletMessageFactory;
	private final IServicePackFactory servicePackFactory;
	private final IServiceKnowledge serviceKnowledge;

	@Inject
	public ServiceTeacher(IMessageHandler messageHandler,
			ISelfLetMsgFactory selfletMessageFactory,
			IServiceKnowledge serviceKnowledge) {
		this.messageHandler = messageHandler;
		this.selfletMessageFactory = selfletMessageFactory;
		this.serviceKnowledge = serviceKnowledge;
		this.servicePackFactory = new ServicePackFactory();
	}

	public void teach(Service service, ISelfLetID provider) {
		LOG.info("Teaching service " + service + " to provider " + provider);
		try {
			teachSubServicesIfNecessary(service, provider);
			packAndSendToProvider(service, provider);
		} catch (NotFoundException e) {
			LOG.error(e);
			LOG.error("Teaching aborted");
		}
	}

	private void teachSubServicesIfNecessary(Service service,
			ISelfLetID provider) {

		for (State serviceState : service.getDefaultBehavior().getStates()) {
			if (serviceState.isFinalState() || serviceState.isInitialState()) {
				continue;
			}

			String subServiceName = serviceState.getName();
			try {
				Service subservice = serviceKnowledge.getProperty(subServiceName);
				teachSubServicesIfNecessary(subservice, provider);
				packAndSendToProvider(subservice, provider);
			} catch (NotFoundException e) {
				throw new NotFoundException("error in getting sub services: "
						+ e);
			}

		}
	}

	private void packAndSendToProvider(Service service, ISelfLetID provider) {
		if(service.getMaxResponseTimeInMsec() <= 0){
			throw new NotFoundException("mrp is 0. Problem with service");
		}
		ServicePack servicePack = servicePackFactory
				.createServicePackForService(service);
		LOG.info("sending service " + service.getName() + "[");
		LOG.info("demand: " + service.getServiceDemand());
		LOG.info("mrp: " + service.getMaxResponseTimeInMsec() + "]");
		SelfLetMsg serviceTeachMessage = selfletMessageFactory
				.newServiceTeachMsg(servicePack, provider);
		messageHandler.send(serviceTeachMessage);
	}

}
