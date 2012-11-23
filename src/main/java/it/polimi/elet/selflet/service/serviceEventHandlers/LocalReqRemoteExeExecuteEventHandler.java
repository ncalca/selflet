package it.polimi.elet.selflet.service.serviceEventHandlers;

import java.util.Map;

import org.apache.log4j.Logger;

import polimi.reds.MessageID;

import it.polimi.elet.selflet.events.service.IServiceEvent;
import it.polimi.elet.selflet.events.service.LocalReqRemoteExeExecuteEvent;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.message.IMessageHandler;
import it.polimi.elet.selflet.message.ISelfLetMsgFactory;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.negotiation.ServiceExecutionParameter;
import it.polimi.elet.selflet.service.IRunningServiceManager;

/**
 * Handles event signaling the need to execute a service into another service
 * provider
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class LocalReqRemoteExeExecuteEventHandler implements IServiceEventHandler {

	private static final Logger LOG = Logger.getLogger(LocalReqRemoteExeExecuteEventHandler.class);
	private final ISelfLetMsgFactory selfletMsgFactory;
	private final IMessageHandler messageHandler;
	private final IRunningServiceManager runningServiceManager;

	public LocalReqRemoteExeExecuteEventHandler(ISelfLetMsgFactory selfletMsgFactory, IMessageHandler messageHandler,
			IRunningServiceManager runningServiceManager) {
		this.selfletMsgFactory = selfletMsgFactory;
		this.messageHandler = messageHandler;
		this.runningServiceManager = runningServiceManager;
	}

	@Override
	public void handleEvent(IServiceEvent serviceEvent) {
		LocalReqRemoteExeExecuteEvent event = (LocalReqRemoteExeExecuteEvent) serviceEvent;

		String serviceName = event.getServiceName();
		ISelfLetID receiverProvider = event.getReceiverProvider();
		Map<String, Object> serviceParam = event.getParameters();

		ServiceExecutionParameter serviceExecutionParameters = new ServiceExecutionParameter(serviceName, serviceParam);
		
		SelfLetMsg requestExecMsg = selfletMsgFactory.newRedirectRequestMsg(serviceExecutionParameters, receiverProvider);
		MessageID messageID = messageHandler.send(requestExecMsg);
		LOG.debug("Sending request for remote service " + serviceName + " to SelfLet " + receiverProvider + " with message: " + requestExecMsg);

		runningServiceManager.addServiceWaitingForRemoteReply(event.getCallingService(), messageID);
	}

}
