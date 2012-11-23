package it.polimi.elet.selflet.negotiation.messageHandlers;

import org.apache.log4j.Logger;

import it.polimi.elet.selflet.events.DispatchingUtility;
import it.polimi.elet.selflet.events.IEventDispatcher;
import it.polimi.elet.selflet.events.service.RemoteReqLocalExeExecuteEvent;
import it.polimi.elet.selflet.message.SelfLetMessageTypeEnum;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.negotiation.ServiceExecutionParameter;
import it.polimi.elet.selflet.service.utilization.IRedirectMonitor;

/**
 * A message handler for redirect requests received by external providers. It
 * basically fires an internal event demanding for service execution.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class RedirectRequestMessageHandler implements ISelfletMessageHandler {

	private static final Logger LOG = Logger.getLogger(RedirectRequestMessageHandler.class);

	private final IEventDispatcher dispatcher;
	private final IRedirectMonitor redirectMonitor;

	public RedirectRequestMessageHandler(IEventDispatcher dispatcher, IRedirectMonitor redirectMonitor) {
		this.dispatcher = dispatcher;
		this.redirectMonitor = redirectMonitor;
	}

	@Override
	public void handleMessage(SelfLetMsg message) {
		ServiceExecutionParameter serviceExecutionParameters = (ServiceExecutionParameter) message.getContent();
		LOG.info("Received redirect request from  " + message.getFrom() + " for service " + serviceExecutionParameters.getServiceName());
		redirectMonitor.addReceivedRedirect(message.getFrom(), serviceExecutionParameters.getServiceName());
		fireRemoteReqLocalExeExecuteEvent(serviceExecutionParameters.getServiceName(), message);
	}

	private void fireRemoteReqLocalExeExecuteEvent(String name, SelfLetMsg message) {
		DispatchingUtility.dispatchEvent(dispatcher, RemoteReqLocalExeExecuteEvent.class, name, message);
	}

	@Override
	public SelfLetMessageTypeEnum getTypeOfHandledMessage() {
		return SelfLetMessageTypeEnum.REDIRECT_REQUEST;
	}

}
