package it.polimi.elet.selflet.negotiation.messageHandlers;

import it.polimi.elet.selflet.events.IEventDispatcher;
import it.polimi.elet.selflet.message.SelfLetMessageTypeEnum;

/**
 * Interface for message handler factory. A message handler is in charge of
 * managing a specific kind of selflet message.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface ISelfletMessageHandlerFactory {

	/**
	 * Returns a new message handler for the given message type
	 * */
	ISelfletMessageHandler create(SelfLetMessageTypeEnum messageHandlerType);

	/**
	 * Sets the list of dispatchers
	 * */
	void setDispatcher(IEventDispatcher dispatchers);

}
