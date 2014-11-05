package it.polimi.elet.selflet.service;

import it.polimi.elet.selflet.action.IActionExecutorFactory;
import it.polimi.elet.selflet.behavior.IConditionEvaluator;
import it.polimi.elet.selflet.events.DispatchingUtility;
import it.polimi.elet.selflet.events.IEventDispatcher;
import it.polimi.elet.selflet.events.service.RemoteReqLocalExeCompletedEvent;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.knowledge.IGeneralKnowledge;

import java.io.Serializable;

import org.apache.log4j.Logger;

import polimi.reds.MessageID;

/**
 * This class is used to execute a service asked from a remote selflet.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class RemoteRunningService extends RunningService {
	
	private static final Logger LOG = Logger.getLogger("resultsLogger");

	private final ISelfLetID requestorSelflet;
	private final MessageID originatingMessageID;

	public RemoteRunningService(Service service, ISelfLetID from, MessageID msgId, IGeneralKnowledge generalKnowledge,
			IActionExecutorFactory actionExecutorFactory, IEventDispatcher dispatcher, IConditionEvaluator conditionEvaluator) {
		super(service, generalKnowledge, actionExecutorFactory, dispatcher, conditionEvaluator);
		this.requestorSelflet = from;
		this.originatingMessageID = msgId;
	}

	@Override
	protected void fireCompletionEvent() {
		fireRemoteReqLocalExeCompletedEvent(getServiceName(), getLastOutput(), requestorSelflet, originatingMessageID, this, getResponseTime());
	}

	private void fireRemoteReqLocalExeCompletedEvent(String serviceName, Object lastOutput, ISelfLetID requestorSelflet, MessageID originatingMessageID,
			RunningService runningService, long responseTime) {

		LOG.info(System.currentTimeMillis() +  ",remote," + serviceName +"," + responseTime + ",1");
		DispatchingUtility.dispatchEvent(getDispatcher(), RemoteReqLocalExeCompletedEvent.class, serviceName, (Serializable) lastOutput, requestorSelflet,
				originatingMessageID, runningService, responseTime);
	}

	@Override
	public boolean isChildService() {
		return false;
	}

	@Override
	public boolean isLocalServiceInvocation() {
		return false;
	}

}
