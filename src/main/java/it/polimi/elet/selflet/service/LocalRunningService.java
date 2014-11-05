package it.polimi.elet.selflet.service;

import org.apache.log4j.Logger;

import it.polimi.elet.selflet.action.IActionExecutorFactory;
import it.polimi.elet.selflet.behavior.IConditionEvaluator;
import it.polimi.elet.selflet.events.DispatchingUtility;
import it.polimi.elet.selflet.events.IEventDispatcher;
import it.polimi.elet.selflet.events.service.LocalReqLocalExeCompletedEvent;
import it.polimi.elet.selflet.knowledge.IGeneralKnowledge;

/**
 * This class is used to execute a service originated at this selflet. When the
 * service is completed the <code>LocalReqLocalExeCompletedEvent</code> is
 * fired.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class LocalRunningService extends RunningService {
	
	private static final Logger LOG = Logger.getLogger("resultsLogger");

	private final RunningService callingService;

	public LocalRunningService(Service service, RunningService callingService, IGeneralKnowledge generalKnowledge,
			IActionExecutorFactory actionExecutorFactory, IEventDispatcher dispatcher,
			IConditionEvaluator conditionEvaluator) {

		super(service, generalKnowledge, actionExecutorFactory, dispatcher, conditionEvaluator);
		this.callingService = callingService;
	}

	@Override
	public boolean isChildService() {

		if (callingService == null || callingService instanceof RemoteRunningService) {
			return false;
		}

		boolean calledByElementaryBehavior = callingService.getService().isImplementedByElementaryBehavior();
		return !calledByElementaryBehavior;
	}

	@Override
	protected void fireCompletionEvent() {
		fireLocalReqLocalExeCompletedEvent(getServiceName(), callingService, this, getLastOutput(), getResponseTime());
	}

	private void fireLocalReqLocalExeCompletedEvent(String name, RunningService callingService,
			RunningService runningService, Object output, Long responseTime) {
		
		LOG.info(System.currentTimeMillis() +  ",local," + name +"," + responseTime + ",1");
		DispatchingUtility.dispatchEvent(getDispatcher(), LocalReqLocalExeCompletedEvent.class, name, callingService,
				runningService, output, responseTime);
	}

	@Override
	public boolean isLocalServiceInvocation() {
		return true;
	}

}
