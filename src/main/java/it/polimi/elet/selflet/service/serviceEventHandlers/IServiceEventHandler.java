package it.polimi.elet.selflet.service.serviceEventHandlers;

import it.polimi.elet.selflet.events.service.IServiceEvent;

/**
 * An interface for service events
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IServiceEventHandler {

	/**
	 * Handles the given service event
	 * */
	void handleEvent(IServiceEvent serviceEvent);
}
