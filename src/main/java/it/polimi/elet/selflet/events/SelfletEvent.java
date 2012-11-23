package it.polimi.elet.selflet.events;

import java.util.Date;

/**
 * Adds a timestamp for the event
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public abstract class SelfletEvent implements ISelfletEvent {

	private final Date timeStamp;

	public SelfletEvent() {
		this.timeStamp = new Date();
	}

	public Date getTimeStamp() {
		return timeStamp;
	}
}
