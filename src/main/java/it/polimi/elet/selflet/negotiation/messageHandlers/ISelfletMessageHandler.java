package it.polimi.elet.selflet.negotiation.messageHandlers;

import it.polimi.elet.selflet.message.SelfLetMessageTypeEnum;
import it.polimi.elet.selflet.message.SelfLetMsg;

/**
 * The interface for single message handler.
 * 
 * Each message handler handles a specific message
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface ISelfletMessageHandler {

	/**
	 * Handles the message
	 * */
	void handleMessage(SelfLetMsg message);

	/**
	 * Returns the type of the message handled by this handler
	 * */
	SelfLetMessageTypeEnum getTypeOfHandledMessage();

}
