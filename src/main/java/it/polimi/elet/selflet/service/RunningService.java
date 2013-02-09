package it.polimi.elet.selflet.service;

import java.util.concurrent.CountDownLatch;

import it.polimi.elet.selflet.action.Action;
import it.polimi.elet.selflet.action.IActionExecutor;
import it.polimi.elet.selflet.action.IActionExecutorFactory;
import it.polimi.elet.selflet.behavior.IBehavior;
import it.polimi.elet.selflet.behavior.IConditionEvaluator;
import it.polimi.elet.selflet.events.DispatchingUtility;
import it.polimi.elet.selflet.events.IEventDispatcher;
import it.polimi.elet.selflet.events.service.ServiceNeededEvent;
import it.polimi.elet.selflet.exceptions.ActionException;
import it.polimi.elet.selflet.exceptions.DeadStateException;
import it.polimi.elet.selflet.knowledge.IGeneralKnowledge;
import it.polimi.elet.selflet.knowledge.KnowledgeBase;
import static it.polimi.elet.selflet.utilities.NumberFormat.*;

import org.apache.log4j.Logger;

/**
 * A instance of a running service
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public abstract class RunningService extends Thread {

	private static final Logger LOG = Logger.getLogger(RunningService.class);

	private final long serviceCreationTime = System.currentTimeMillis();

	private final IGeneralKnowledge generalKnowledge;
	private final IActionExecutorFactory actionExecutorFactory;
	private final IBehavior defaultBehavior;
	private final Service service;
	private final NextStateExtractor nextStateExtractor;
	private final IEventDispatcher dispatcher;
	private long startTime;

	private Object lastOutput;
	private long responseTime;
	private it.polimi.elet.selflet.behavior.State currentState;

	private CountDownLatch countDownLatch;

	public RunningService(Service service, IGeneralKnowledge generalKnowledge,
			IActionExecutorFactory actionExecutorFactory, IEventDispatcher dispatcher,
			IConditionEvaluator conditionEvaluator) {
		super(service.getName());
		this.service = service;
		this.generalKnowledge = generalKnowledge;
		this.actionExecutorFactory = actionExecutorFactory;
		this.defaultBehavior = service.getDefaultBehavior();
		this.dispatcher = dispatcher;
		// move to a factory
		this.nextStateExtractor = new NextStateExtractor(defaultBehavior, conditionEvaluator);
	}

	public void run() {
		LOG.debug("Starting execution of service " + service);
		startTime = System.currentTimeMillis();

		// main service loop
		currentState = defaultBehavior.getInitialState();

		while (!currentState.isFinalState()) {
			logStateInfo();
			executeCurrentState();
			try {
				moveToNextState();
			} catch (DeadStateException e) {
				LOG.error("Dead state. Abandoning behavior " + defaultBehavior, e);
				break;
			}
		}

		finalizeService();
	}

	private void finalizeService() {
		LOG.debug("Service '" + service.getName() + "' completed. Response time: " + formatNumber(responseTime));
		logStateInfo();
		computeResponseTime();
		fireCompletionEvent();
	}

	private void computeResponseTime() {
		responseTime = System.currentTimeMillis() - serviceCreationTime;
	}

	private void executeCurrentState() {
		if (currentState.isInitialState()) {
			return;
		}

		if (isInvocationStateOfElementaryBehavior()) {
			executeInvocationStateOfElementaryBehavior();
		} else {
			executeInvocationStateOfComplexBehavior();
		}
	}

	private void executeInvocationStateOfComplexBehavior() {
		this.countDownLatch = new CountDownLatch(1);
		String stateName = currentState.getName();
		String outputPlace = "output";
		fireServiceNeededEvent(stateName, outputPlace, this);
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			LOG.error(e);
		}
	}

	private void executeInvocationStateOfElementaryBehavior() {
		String stateName = currentState.getName();
		Action currentAction = currentState.getAction();
		lastOutput = executeAction(currentAction, stateName);
		generalKnowledge.setOrUpdateProperty("output_" + stateName, lastOutput);
	}

	private boolean isInvocationStateOfElementaryBehavior() {
		return (defaultBehavior.isElementaryBehavior() && currentState.getAction() != null);
	}

	private Object executeAction(Action action, String stateName) {

		if (action == null) {
			LOG.error("Empty action for " + stateName);
			return KnowledgeBase.EMPTY_FIELD;
		}

		Object outputFromAction = null;

		try {
			IActionExecutor actionExecutor = actionExecutorFactory.createActionExecutor(this);
			outputFromAction = actionExecutor.executeAction(action, stateName);
		} catch (ActionException e) {
			LOG.error("Prolem while executing the action contained in state " + stateName, e);
		}

		generalKnowledge.setOrUpdateProperty("outputFromAction_" + stateName, outputFromAction);
		return outputFromAction;
	}

	private void moveToNextState() {
		currentState = nextStateExtractor.nextState(currentState);
	}

	private void logStateInfo() {
		LOG.info("Current state: <<" + currentState.getName() + ":" + currentState.getUniqueId() + " of behavior: "
				+ defaultBehavior + ">>");
	}

	/**
	 * This method is called upon completion of service
	 * */
	protected abstract void fireCompletionEvent();

	public Object getLastOutput() {
		return lastOutput;
	}

	public long getResponseTime() {
		return responseTime;
	}

	public String getServiceName() {
		return service.getName();
	}

	public IEventDispatcher getDispatcher() {
		return dispatcher;
	}

	public String toString() {
		return service.getName();
	}

	public void resumeService() {
		if (countDownLatch != null) {
			countDownLatch.countDown();
		}
	}

	public Service getService() {
		return service;
	}

	public long getServiceLifeTimeInMillis() {
		return System.currentTimeMillis() - startTime;
	}

	public abstract boolean isChildService();

	public abstract boolean isLocalServiceInvocation();

	private void fireServiceNeededEvent(String serviceName, String outputDestination, RunningService callingService) {
		DispatchingUtility.dispatchEvent(dispatcher, ServiceNeededEvent.class, serviceName, outputDestination,
				callingService);
	}

}
