package it.polimi.elet.selflet.negotiation.servicepack;

import it.polimi.elet.selflet.events.IEventDispatcher;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.negotiation.ServicePack;

/**
 * Installs a new service
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ServicePackInstaller {

	// private static final Logger LOG =
	// Logger.getLogger(ServicePackInstaller.class);

	// private final IEventDispatcher eventDispatcher;
	// private final ISelfLetID selfletId;

	public ServicePackInstaller(ISelfLetID selfletID, IEventDispatcher eventDispatcher) {
		// this.selfletId = selfletID;
		// this.eventDispatcher = eventDispatcher;
	}

	/**
	 * Installs the content received from the service pack into the local
	 * selflet
	 * 
	 * @param servicePack
	 *            the servicePack to install
	 * */
	public void install(ServicePack servicePack) {

		// // extract data from the BehaviorPack object
		// BehaviorStructure behaviorStructure =
		// servicePack.getBehaviorStructure();
		//
		// IBehavior newBehavior = null;
		//
		// if
		// (BehaviorUtilities.isElementaryBehaviorStructure(behaviorStructure))
		// {
		//
		// ElementaryServicePack elementaryServicePack = (ElementaryServicePack)
		// servicePack;
		//
		// newBehavior = new ElementaryBehavior(behaviorStructure,
		// elementaryServicePack.getElementaryBehaviorCost(),
		// elementaryServicePack.getElementaryBehaviorCPUTime());
		//
		// } else {
		// newBehavior = new Behavior(servicePack.getBehaviorStructure());
		// }

		// String serviceName = servicePack.getServiceName();

		// Service newService = new Service(serviceName, newBehavior,
		// knowledges);
		// newService.setInputParameters(servicePack.getInputParameters());
		// newService.setRevenue(servicePack.getRevenue());
		//
		// knowledges.getServiceKnowledge().setProperty(serviceName,
		// newService);

		// servicePack.get
		// // abilities = behaviorPack.getAbilities();
		//
		// Iterator<AbstractAbility> it = abilities.iterator();
		// AbstractAbility ability = null;
		//
		// // 1) install abilities
		// while (it.hasNext()) {
		//
		// ability = it.next();
		//
		// try {
		// abilityExecutionEnvironment.addAbility(ability);
		// } catch (AlreadyPresentException e) {
		// // If the ability is already there, do nothing
		// } catch (AbilityEnvironmentException e) {
		// LOG.error("Can't add received ability to Ability environment",
		// e);
		// }
		//
		// // if it is already there update its Signature
		// if (typeKnowledge.getProperties().containsKey(
		// ability.getSignature().getUniqueName() + "Signature"))
		//
		// typeKnowledge.updateProperty(ability.getSignature()
		// .getUniqueName() + "Signature", ability.getSignature());
		// else
		// // else add the signature
		//
		// {
		// typeKnowledge.setProperty(ability.getSignature()
		// .getUniqueName() + "Signature", ability.getSignature());
		// }
		// // fire the event
		// fireAbilityInstalledEvent(ability.getSignature());
		//
		// }
		//
		// // 2) create the new behavior
		// IBehavior behavior = null;
		// try {
		// behavior = new Behavior(behaviorRep);
		// } catch (BehaviourLoaderException e1) {
		// LOG.debug(selfletId + " Can't build and load given behavior", e1);
		// }

		// try {
		// behaviorKnowledge.setProperty(behavior.getName(), behavior);
		// } catch (AlreadyPresentException e) {
		// // If the behavior is already there, do nothing
		// }

		// Adding it as an achievable goal will be done by an event when
		// the behavior installs without problems

		// 3) Add the goal
		// Service service = null;
		// service = (Service) serviceKnowledge.getProperty(serviceName);

		// modify the goal parameters
		// service.addImplementingBehaviorSignature(behavior.getSignature());
		//
		// // in any cases, the new default behavior for given goal is the
		// // newly downloaded one
		// service.setDefaultBehaviorSignature(behavior.getSignature());
		//
		// // now that the exchange is completed send an acknowledge to the
		// // sender Selflet.
		//
		// service.enable();

		// boolean local = goalKnowledge.isLocalGoal(goal);

		// prepare ack msg & send it
		// LOG.debug(selfletId + " Sending acknowledge for service "
		// + serviceName);
		//
		// SelfLetMsg ackServiceReceived = selfLetMsgFactory
		// .newAckServiceReceivedMsg(providerId, serviceName);
		//
		// id = messageHandler.send(ackServiceReceived);

	}

}
