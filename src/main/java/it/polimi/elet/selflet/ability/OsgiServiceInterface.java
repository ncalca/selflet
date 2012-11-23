package it.polimi.elet.selflet.ability;

import it.polimi.elet.selflet.autonomic.IAutonomicManager;
import it.polimi.elet.selflet.events.IEventDispatcher;
import it.polimi.elet.selflet.knowledge.IGeneralKnowledge;
import it.polimi.elet.selflet.message.IMessageHandler;
import it.polimi.elet.selflet.negotiation.INegotiationManager;

/**
 * This interface must be implemented by the services which are registered by
 * the bundles in the OSGi Framework. It has a setup method, to initialize the
 * service and receive all the possible needed references to the selflet
 * components, and a tear down method to perform operations before shutdown.
 * 
 * @author Davide Devescovi
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 */
public interface OsgiServiceInterface {

	/**
	 * Sets up the service; it's called by the Ability Execution Environment as
	 * soon as the ability is installed.
	 * 
	 */
	void setup(IAbilityExecutionEnvironment abilityExecutionEnvironment, IAutonomicManager autonomicManager, IEventDispatcher eventDispatcher,
			IGeneralKnowledge generalKnowledge, IMessageHandler messageHandler, INegotiationManager negotiationManager);

	/**
	 * Tears down the service; it's called by the Ability Execution Environment
	 * just before uninstalling the ability.
	 */
	void tearDown();
}
