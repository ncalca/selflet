package it.polimi.elet.selflet.events;

import java.util.Date;

/**
 * The interface all the internal SelfLet events must implement.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 */
public interface ISelfletEvent {

	/**
	 * Gets the main event type of this Event.
	 * 
	 * @return a String representing the main event type
	 */
	EventTypeEnum getEventType();

	/**
	 * Gets the event timestamp (date of creation).
	 * 
	 * @return a Date object representing the creation timestamp for the event.
	 * */
	Date getTimeStamp();

}
