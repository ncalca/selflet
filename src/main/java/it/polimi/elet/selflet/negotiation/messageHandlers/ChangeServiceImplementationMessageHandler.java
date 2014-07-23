package it.polimi.elet.selflet.negotiation.messageHandlers;

import org.apache.log4j.Logger;

import it.polimi.elet.selflet.behavior.IBehavior;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.message.SelfLetMessageTypeEnum;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.negotiation.ServicePack;
import it.polimi.elet.selflet.service.Service;

public class ChangeServiceImplementationMessageHandler implements
		ISelfletMessageHandler {

	private static final Logger LOG = Logger
			.getLogger(ChangeServiceImplementationMessageHandler.class);

	private final IServiceKnowledge serviceKnowledge;

	public ChangeServiceImplementationMessageHandler(
			IServiceKnowledge serviceKnowledge) {
		this.serviceKnowledge = serviceKnowledge;
	}

	@Override
	public void handleMessage(SelfLetMsg message) {
		ServicePack servicePack = (ServicePack) message.getContent();
		changeServiceImplementation(servicePack);

	}

	private void changeServiceImplementation(ServicePack servicePack) {
		Service service = serviceKnowledge.getProperty(servicePack.getName());
		if (serviceKnowledge.isLocalService(service.getName())) {
			IBehavior newDefaultBehavior = servicePack.getDefaultBehavior();
			if (service.getDefaultBehavior().equals(newDefaultBehavior)) {
				return;
			}
			service.setDefaultBehavior(newDefaultBehavior);
			serviceKnowledge.updateProperty(service.getName(), service);
			LOG.info("service " + service.getName()
					+ " has a new default behavior: "
					+ newDefaultBehavior.getName());
		}
	}

	@Override
	public SelfLetMessageTypeEnum getTypeOfHandledMessage() {
		return SelfLetMessageTypeEnum.CHANGE_SERVICE_IMPLEMENTATION;
	}

}
