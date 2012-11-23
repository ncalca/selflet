package it.polimi.elet.selflet.message;

import static it.polimi.elet.selflet.message.SelfLetMessageTypeEnum.*;

import com.google.common.collect.ImmutableSet;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.message.SelfLetMessageTypeEnum;

/**
 * Registers message type to event types
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class MessageHandlerMessageBinder {

	private final IMessageHandler messageHandler;

	// the set of messages received by message handlers
	private final ImmutableSet<SelfLetMessageTypeEnum> messagesTypes = ImmutableSet.of

	(

	ADVERTISE_ACHIEVABLE_SERVICE,

	ASK_NEEDED_SERVICE,

	GET_SERVICE_PROVIDER_INDIRECTLY,

	EXECUTE_ACHIEVABLE_SERVICE,

	DOWNLOAD_ACHIEVABLE_SERVICE,

	ACKNOWLEDGE_RECEIVED_SERVICE,

	NODE_STATE,

	REDIRECT_REQUEST,

	REDIRECT_REQUEST_REPLY,

	SERVICE_TEACH,

	ISTANTIATE_NEW_SELFLET_REPLY,

	NEIGHBORS

	);

	public MessageHandlerMessageBinder(IMessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}

	/**
	 * Bind messages received by the message handler with the internal events of
	 * the SelfLet.
	 * 
	 * */
	public void register() {
		for (SelfLetMessageTypeEnum messageType : messagesTypes) {
			messageHandler.bindMessagesToEvents(messageType, EventTypeEnum.MESSAGE_HANDLER);
		}
	}

}
