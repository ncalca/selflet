package it.polimi.elet.selflet.negotiation.messageHandlers;

import polimi.reds.MessageID;
import it.polimi.elet.selflet.events.DispatchingUtility;
import it.polimi.elet.selflet.events.IEventDispatcher;
import it.polimi.elet.selflet.events.service.LocalReqRemoteExeCompletedEvent;
import it.polimi.elet.selflet.message.SelfLetMessageTypeEnum;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.negotiation.ReplyRequestData;

/**
 * Handler that receives responses to service redirects
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class RedirectRequestReplyMessageHandler implements ISelfletMessageHandler {

	private final IEventDispatcher eventDispatcher;

	public RedirectRequestReplyMessageHandler(IEventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}

	@Override
	public void handleMessage(SelfLetMsg message) {
		ReplyRequestData replyRequestData = (ReplyRequestData) message.getContent();
		fireLocalReqRemoteExeCompletedEvent(replyRequestData.getServiceName(), replyRequestData.getOutput(), replyRequestData.getMessageId());
	}

	private void fireLocalReqRemoteExeCompletedEvent(String serviceName, Object object, MessageID messageID) {
		DispatchingUtility.dispatchEvent(eventDispatcher, LocalReqRemoteExeCompletedEvent.class, serviceName, serviceName, messageID);
	}

	@Override
	public SelfLetMessageTypeEnum getTypeOfHandledMessage() {
		return SelfLetMessageTypeEnum.REDIRECT_REQUEST_REPLY;
	}

}
