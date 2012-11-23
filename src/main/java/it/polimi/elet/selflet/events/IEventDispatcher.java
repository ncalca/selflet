package it.polimi.elet.selflet.events;

/**
 * An implementation for event dispatcher. The event dispatcher allows the
 * registration of selflet components and the dispatching of events to
 * componenes interested in those kind of events
 * 
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 */
public interface IEventDispatcher extends IEventProducer, Runnable {

	/**
	 * Sends an event to be dispatched
	 * */
	void dispatchEvent(ISelfletEvent event);

	/**
	 * Register an internal subsystem to the event dispatcher. Registration
	 * include events produced and events received
	 * 
	 * @param selfletComponent
	 *            the internal subsystem to be registered
	 * */
	void registerSelfLetComponent(ISelfletComponent selfletComponent);

	/**
	 * Starts event dispatching
	 */
	void run();

	/**
	 * Stops the event dispatcher
	 * */
	void stop();

	/**
	 * Removes all events in queue
	 * */
	void clearEventQueue();

}