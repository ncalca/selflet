package it.polimi.elet.selflet.negotiation.messageHandlers;

import java.util.Set;

import org.apache.log4j.Logger;

import it.polimi.elet.selflet.action.IActionManager;
import it.polimi.elet.selflet.behavior.IBehavior;
import it.polimi.elet.selflet.behavior.State;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.message.SelfLetMessageTypeEnum;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.negotiation.ServicePack;
import it.polimi.elet.selflet.service.Service;

/**
 * Handles service teach messages. Extracts a service pack from the message and
 * installs it in the local knowledge
 * */
public class ServiceTeachMessageHandler implements ISelfletMessageHandler {

	private static final Logger LOG = Logger.getLogger(ServiceTeachMessageHandler.class);

	private final IServiceKnowledge serviceKnowledge;
	private final IActionManager actionManager;

	public ServiceTeachMessageHandler(IServiceKnowledge serviceKnowledge, IActionManager actionManager) {
		this.serviceKnowledge = serviceKnowledge;
		this.actionManager = actionManager;
	}

	@Override
	public void handleMessage(SelfLetMsg message) {
		LOG.debug("Received service teach message:" + message);
		ServicePack servicePack = (ServicePack) message.getContent();
		installServicePack(servicePack);
	}

	private void installServicePack(ServicePack servicePack) {
		String serviceName = servicePack.getName();

		if (serviceKnowledge.isPropertyPresent(serviceName)) {
			return;
		}

		Service receivedService = new Service(servicePack.getName());
		LOG.info("installing service " + receivedService);
		LOG.info("demand: " + servicePack.getServiceDemand());
		LOG.info("mrt: " + servicePack.getMaxResponseTimeInMsec());

		receivedService.addImplementingBehavior(servicePack.getImplementingBehaviors());
		receivedService.setMaxResponseTimeInMsec(servicePack.getMaxResponseTimeInMsec());
		receivedService.setServiceDemand(servicePack.getServiceDemand());

		addActionsToActionManager(servicePack.getImplementingBehaviors());
		receivedService.setDefaultBehavior(servicePack.getDefaultBehavior());
		serviceKnowledge.setProperty(receivedService.getName(), receivedService);
		LOG.debug("Service " + serviceName + " installed");
	}

	private void addActionsToActionManager(Set<IBehavior> implementingBehaviors) {
		for (IBehavior behavior : implementingBehaviors) {
			for (State state : behavior.getStates()) {
				actionManager.addAction(state.getAction());
			}
		}
	}

	@Override
	public SelfLetMessageTypeEnum getTypeOfHandledMessage() {
		return SelfLetMessageTypeEnum.SERVICE_TEACH;
	}

}
