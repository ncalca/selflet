package it.polimi.elet.selflet.service;

import it.polimi.elet.selflet.behavior.IBehavior;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.message.IMessageHandler;
import it.polimi.elet.selflet.message.ISelfLetMsgFactory;
import it.polimi.elet.selflet.message.SelfLetMsg;

import java.util.Set;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ServiceImplementationChanger implements
		IServiceImplementationChanger {
	
	private static final Logger LOG = Logger.getLogger(ServiceImplementationChanger.class);
	
	private final IMessageHandler messageHandler;
	private final ISelfLetMsgFactory selfletMessageFactory;
	private final IServiceKnowledge serviceKnowledge;

	@Inject
	public ServiceImplementationChanger(IMessageHandler messageHandler, ISelfLetMsgFactory selfletMessageFactory, IServiceKnowledge serviceKnowledge) {
		this.messageHandler = messageHandler;
		this.selfletMessageFactory = selfletMessageFactory;
		this.serviceKnowledge = serviceKnowledge;
	}

	@Override
	public void changeServiceImplementation(String serviceName, int behaviorQuality) {
		Service service = serviceKnowledge.getProperty(serviceName);
		IBehavior behaviorOfDesiredQuality = getBehaviorOfQuality(service,
				behaviorQuality);
		if (service.getDefaultBehavior().equals(behaviorOfDesiredQuality)) {
			return;
		}
		service.setDefaultBehavior(behaviorOfDesiredQuality);
		notifyImplementationChange(serviceName, behaviorOfDesiredQuality);
		serviceKnowledge.updateProperty(serviceName, service);
		LOG.info("changed implementation of service " + serviceName + "with behavior " + behaviorOfDesiredQuality.getName());
		LOG.info("notification fired to other selflets");
		

	}
	
	private IBehavior getBehaviorOfQuality(Service service, int quality) {
		IBehavior defaultBehavior = service.getDefaultBehavior();
		Set<IBehavior> behaviors = service.getImplementingBehaviors();
		for (IBehavior behavior : behaviors) {
			if (quality == getQualityOfBehavior(behavior)) {
				defaultBehavior = behavior;
			}
		}
		return defaultBehavior;
	}

	private int getQualityOfBehavior(IBehavior behavior) {
		String name = behavior.getName();
		return Integer.parseInt(name.substring(name.length() - 1));
	}
	
	private void notifyImplementationChange(String serviceName, IBehavior newBehavior){
		SelfLetMsg changeBehaviorMessage = selfletMessageFactory.changeServiceImplementation(serviceName, newBehavior);
		messageHandler.send(changeBehaviorMessage);
	}

}
