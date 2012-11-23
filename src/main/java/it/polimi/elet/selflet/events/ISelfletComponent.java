package it.polimi.elet.selflet.events;

import com.google.common.collect.ImmutableSet;

/**
 * An interface for each internal selflet component.
 * 
 * A component is able to receive events (as specified internally in the
 * componenet) and to produce events.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface ISelfletComponent extends IEventListener, IEventProducer {

	/**
	 * Returns the set of events to which this component is interested
	 * */
	ImmutableSet<EventTypeEnum> getReceivedEvents();

}
