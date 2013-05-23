package it.polimi.elet.selflet.action;

import it.polimi.elet.selflet.ability.AbilitySignature;
import it.polimi.elet.selflet.ability.IAbilityExecutionEnvironment;
import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletComponent;
import it.polimi.elet.selflet.events.ability.AbilityExecutedEvent;
import it.polimi.elet.selflet.events.service.ServiceNeededEvent;
import it.polimi.elet.selflet.exceptions.AbilityEnvironmentException;
import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.knowledge.IGeneralKnowledge;
import it.polimi.elet.selflet.knowledge.IKnowledgesContainer;
import it.polimi.elet.selflet.knowledge.ITypeKnowledge;
import it.polimi.elet.selflet.knowledge.KnowledgeBase;
import it.polimi.elet.selflet.load.ILoadProfileManager;
import it.polimi.elet.selflet.service.RunningService;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableSet;

/**
 * An implementation of the API passed to the action
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * @author Davide Devescovi
 */
public class ActionAPI extends SelfletComponent implements IActionAPI {

	private static final Logger LOG = Logger.getLogger(ActionAPI.class);

	private final IAbilityExecutionEnvironment abilityExecutionEnvironment;
	private final ITypeKnowledge typeKnowledge;
	private final IGeneralKnowledge generalKnowledge;
	private final RunningService runningService;
	private final ILoadProfileManager loadProfilerManager;

	public ActionAPI(IAbilityExecutionEnvironment abilityExecutionEnvironment, IKnowledgesContainer knowledges,
			RunningService runningService, ILoadProfileManager loadProfilerManager) {
		this.abilityExecutionEnvironment = abilityExecutionEnvironment;
		this.typeKnowledge = knowledges.getTypeKnowledge();
		this.generalKnowledge = knowledges.getGeneralKnowledge();
		this.runningService = runningService;
		this.loadProfilerManager = loadProfilerManager;
	}

	public Object needService(String serviceName) {
		final int timeOut = 0;
		return needService(serviceName, timeOut);
	}

	private Object needService(String serviceName, long timeout) {

		if (serviceName == null || timeout < 0) {
			LOG.error("Null service name or negative timeout");
			return KnowledgeBase.ERROR;
		}

		LOG.debug("Need service " + serviceName);

		String resultDestination = "output" + serviceName;

		// // set the result location
		// try {
		// this.generalKnowledge.setProperty(resultDestination,
		// KnowledgeBase.EMPTY_FIELD);
		// } catch (AlreadyPresentException e) {
		// LOG.error("Setting an existing variable");
		// this.generalKnowledge.updateProperty(resultDestination,
		// KnowledgeBase.EMPTY_FIELD);
		// }
		// fire the event
		fireServiceNeededEvent(serviceName, resultDestination);

		// try {
		// LOG.debug("Putting thread to sleep " + Thread.currentThread());

		// put the calling service in wait state. The thread will be
		// awakened in the ServiceManager when a service completed event is
		// received.
		// while (!runningService.isCompleted()) {
		// synchronized (runningService) {
		// runningService.wait(timeout);
		// }
		// }
		// } catch (InterruptedException e) {
		// LOG.error("Thread interrupted");
		// }

		Object returnValue = null;
		try {
			returnValue = generalKnowledge.getProperty(resultDestination);
		} catch (NotFoundException e) {
			LOG.error("Trying to read a non existent variable: " + resultDestination);
			returnValue = KnowledgeBase.ERROR;
			return returnValue;
		}

		// deletes variable resDest from the space since it's needed no more to
		// free space and enable new runs
		generalKnowledge.deleteProperty(resultDestination);
		return returnValue;
	}

	public Object executeAbility(String abilityName, List<Object[]> params) {

		// TODO modify these instruction not to make it work on Properties
		AbilitySignature signature = null;
		try {
			signature = (AbilitySignature) typeKnowledge.getProperty(abilityName + "Signature");
		} catch (it.polimi.elet.selflet.exceptions.NotFoundException e1) {
			LOG.error("Ability not found: " + abilityName);
			return null;
		}

		// the first parameter is the method name
		Object[] p = params.get(0);
		String methodName = (String) p[0];

		// shift all the remaining parameters
		Object[] abilityParams = new Object[p.length - 1];

		for (int i = 1; i < p.length; i++) {
			abilityParams[i - 1] = p[i];
		}

		try {
			Object output = localAbilityExecution(signature, methodName, abilityParams);
			fireAbilityExecutedEvent(abilityName, output, true);
			return output;
		} catch (AbilityEnvironmentException e) {
			LOG.error("Negotiation error", e);
		}

		throw new IllegalStateException("Cannot load ability");
	}

	@Override
	public ILoadProfileManager getLoadProfileManager() {
		return this.loadProfilerManager;
	}

	/* *************** */
	/* Private methods */
	/* *************** */

	private Object localAbilityExecution(AbilitySignature signature, String methodName, Object[] params) {

		if (abilityExecutionEnvironment.isPresent(signature)) {
			return abilityExecutionEnvironment.execute(signature, methodName, params);
		} else {
			throw new it.polimi.elet.selflet.exceptions.NotFoundException("Couldn't find ability");
		}
	}

	private void fireServiceNeededEvent(final String serviceName, final String resultDestination) {
		dispatchEvent(ServiceNeededEvent.class, serviceName, resultDestination, runningService);
	}

	private void fireAbilityExecutedEvent(String ability, Object output, boolean local) {
		dispatchEvent(AbilityExecutedEvent.class, ability, output, local);
	}

	@Override
	public ImmutableSet<EventTypeEnum> getReceivedEvents() {
		return ImmutableSet.of();
	}

}
