package it.polimi.elet.selflet.events;

/**
 * This interface represents an internal Selflet event producer.
 * 
 * @author Davide Devescovi
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 */
public interface IEventProducer {

	/**
	 * Adds an event dispatcher to this producer.
	 * 
	 * @param dispatcher
	 *            the IEventDispatcher to be added
	 */
	void setEventDispatcher(IEventDispatcher dispatcher);

	/**
	 * Removes an event produce from this producer.
	 * 
	 * @param dispatcher
	 *            the IEventDispatcher to be removed
	 */
	void removeEventDispatcher(IEventDispatcher dispatcher);

}