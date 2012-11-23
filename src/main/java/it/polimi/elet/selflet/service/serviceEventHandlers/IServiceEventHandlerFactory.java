package it.polimi.elet.selflet.service.serviceEventHandlers;

import it.polimi.elet.selflet.events.EventTypeEnum;

/**
 * Factory to create service event handlers
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IServiceEventHandlerFactory {

	/**
	 * Returns the service message handler associated with the given event type
	 * */
	IServiceEventHandler create(EventTypeEnum eventType);
}
