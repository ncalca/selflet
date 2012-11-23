package it.polimi.elet.selflet.service;

import it.polimi.elet.selflet.action.IActionExecutorFactory;
import it.polimi.elet.selflet.behavior.IConditionEvaluator;
import it.polimi.elet.selflet.events.IEventDispatcher;
import it.polimi.elet.selflet.knowledge.IGeneralKnowledge;
import it.polimi.elet.selflet.message.SelfLetMsg;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * An implementation for the running service factory
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>s
 * */
@Singleton
public class RunningServiceFactory implements IRunningServiceFactory {

	private final IGeneralKnowledge generalKnowledge;
	private final IActionExecutorFactory actionExecutorFactory;
	private final IEventDispatcher eventDispatcher;
	private final IConditionEvaluator conditionEvaluator;

	@Inject
	public RunningServiceFactory(IGeneralKnowledge generalKnowledge, IActionExecutorFactory actionExecutorFactory, IEventDispatcher eventDispatcher,
			IConditionEvaluator conditionEvaluator) {
		this.generalKnowledge = generalKnowledge;
		this.actionExecutorFactory = actionExecutorFactory;
		this.eventDispatcher = eventDispatcher;
		this.conditionEvaluator = conditionEvaluator;
	}

	@Override
	public RunningService createRemoteRunningService(SelfLetMsg msg, Service service) {
		return new RemoteRunningService(service, msg.getFrom(), msg.getId(), generalKnowledge, actionExecutorFactory, eventDispatcher, conditionEvaluator);
	}

	@Override
	public RunningService createLocalRunningService(Service service, RunningService callingService) {
		return new LocalRunningService(service, callingService, generalKnowledge, actionExecutorFactory, eventDispatcher, conditionEvaluator);
	}

}
