package it.polimi.elet.selflet.service.serviceEventHandlers;

import it.polimi.elet.selflet.events.service.IServiceEvent;
import it.polimi.elet.selflet.events.service.RemoteReqLocalExeCompletedEvent;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.message.IMessageHandler;
import it.polimi.elet.selflet.message.ISelfLetMsgFactory;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.negotiation.ReplyRequestData;

import org.apache.log4j.Logger;

/**
 * Handles event produced whenever a service remotely request is completed.
 * Basically we have to answer to the requestor
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class RemoteReqLocalExeCompletedEventHandler implements IServiceEventHandler {

	private static final Logger LOG = Logger.getLogger(RemoteReqLocalExeCompletedEventHandler.class);

	private final IMessageHandler messageHandler;
	private final ISelfLetMsgFactory selfletMsgFactory;

	public RemoteReqLocalExeCompletedEventHandler(IMessageHandler messageHandler, ISelfLetMsgFactory selfletMsgFactory) {
		this.messageHandler = messageHandler;
		this.selfletMsgFactory = selfletMsgFactory;
	}

	@Override
	public void handleEvent(IServiceEvent serviceEvent) {
		RemoteReqLocalExeCompletedEvent event = (RemoteReqLocalExeCompletedEvent) serviceEvent;
		LOG.debug("Remote service completed event " + event.getServiceName() + " with result [" + event.getOutput() + "] and response time: "
				+ event.getResponseTime());
		
		ISelfLetID receiverID = event.getRequestorId();
		ReplyRequestData replyRequestData = new ReplyRequestData(event.getServiceName(), event.getOutput(), event.getMsgId());
		SelfLetMsg selfletMsg = selfletMsgFactory.newRedirectRequestReplyMsg(receiverID, replyRequestData);
		messageHandler.send(selfletMsg);
	}
}
