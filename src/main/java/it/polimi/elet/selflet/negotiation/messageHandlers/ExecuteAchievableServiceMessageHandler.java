package it.polimi.elet.selflet.negotiation.messageHandlers;

import java.util.Map.Entry;

import org.apache.log4j.Logger;

import it.polimi.elet.selflet.events.DispatchingUtility;
import it.polimi.elet.selflet.events.IEventDispatcher;
import it.polimi.elet.selflet.events.service.ServiceNeededEvent;
import it.polimi.elet.selflet.knowledge.IGeneralKnowledge;
import it.polimi.elet.selflet.knowledge.IKnowledgesContainer;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.knowledge.KnowledgeBase;
import it.polimi.elet.selflet.message.IMessageHandler;
import it.polimi.elet.selflet.message.ISelfLetMsgFactory;
import it.polimi.elet.selflet.message.SelfLetMessageTypeEnum;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.negotiation.ServiceExecutionParameter;
import it.polimi.elet.selflet.service.IRunningServiceFactory;
import it.polimi.elet.selflet.service.RunningService;
import it.polimi.elet.selflet.service.Service;

/**
 * An implementation for EXECUTE_ACHIEVABLE_SERVICE message handler
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ExecuteAchievableServiceMessageHandler implements ISelfletMessageHandler {

	private static final Logger LOG = Logger.getLogger(ExecuteAchievableServiceMessageHandler.class);

	private final IServiceKnowledge serviceKnowledge;
	private final ISelfLetMsgFactory selfletMsgFactory;
	private final IGeneralKnowledge generalKnowledge;
	private final IMessageHandler messageHandler;
	private final IEventDispatcher dispatcher;
	private final IRunningServiceFactory runningServiceFactory;

	public ExecuteAchievableServiceMessageHandler(IKnowledgesContainer knowledges, ISelfLetMsgFactory selfletMessageFactory, IMessageHandler messageHandler,
			IEventDispatcher dispatchers, IRunningServiceFactory runningServiceFactory) {
		this.serviceKnowledge = knowledges.getServiceKnowledge();
		this.generalKnowledge = knowledges.getGeneralKnowledge();
		this.selfletMsgFactory = selfletMessageFactory;
		this.messageHandler = messageHandler;
		this.dispatcher = dispatchers;
		this.runningServiceFactory = runningServiceFactory;
	}

	@Override
	public void handleMessage(SelfLetMsg message) {
		LOG.debug("EXECUTE ACHIEVABLE SERVICE from selflet " + message.getFrom());

		ServiceExecutionParameter serviceParam = (ServiceExecutionParameter) message.getContent();

		if (serviceParam == null) {
			invalidServiceParam(message);
			return;
		}

		Service service = serviceKnowledge.getProperty(serviceParam.getServiceName());

		if (!service.isLocallyAvailable()) {
			invalidService(message, serviceParam);
			return;
		}

		LOG.debug("Received request for service: " + service.getName());

		for (Entry<String, Object> entry : serviceParam.getServiceParameters().entrySet()) {
			generalKnowledge.setOrUpdateProperty(entry.getKey(), entry.getValue());
		}

		// TODO possible problem for multiple execution of the same service from
		// the same requester
		String knowledgeOutputDestination = "behaviorOutput" + service.getName() + "Remote" + message.getFrom();
		generalKnowledge.setOrUpdateProperty(knowledgeOutputDestination, KnowledgeBase.EMPTY_FIELD);

		RunningService remoteService = runningServiceFactory.createRemoteRunningService(message, service);
		fireServiceNeededEvent(service.getName(), knowledgeOutputDestination, remoteService);
	}

	private void invalidService(SelfLetMsg message, ServiceExecutionParameter serviceParam) {
		LOG.warn("Received request for a non-available service " + serviceParam.getServiceName());
		String errMsg = "The requested service " + serviceParam.getServiceName() + " is not available";
		SelfLetMsg reply = selfletMsgFactory.newNegotiationErrorMsg(message.getFrom(), errMsg, null);
		messageHandler.reply(reply, message.getId());
	}

	private void invalidServiceParam(SelfLetMsg message) {
		LOG.warn("Received request with a null service parameter");
		SelfLetMsg reply = selfletMsgFactory.newNegotiationErrorMsg(message.getFrom(), "The received parameter is null", null);
		messageHandler.reply(reply, message.getId());
	}

	@Override
	public SelfLetMessageTypeEnum getTypeOfHandledMessage() {
		return SelfLetMessageTypeEnum.EXECUTE_ACHIEVABLE_SERVICE;
	}

	private void fireServiceNeededEvent(String serviceName, String knowledgeOutputDestination, RunningService callingService) {
		DispatchingUtility.dispatchEvent(dispatcher, ServiceNeededEvent.class, serviceName, knowledgeOutputDestination, callingService);
	}

}
