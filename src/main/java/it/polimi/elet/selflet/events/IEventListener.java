package it.polimi.elet.selflet.events;

/**
 * This interface represents an internal selflet event listener.
 * 
 * @author Davide Devescovi
 */
public interface IEventListener {

	/**
	 * This method is called by the dispatcher when an event, relevant to the
	 * listener, is produced.
	 * 
	 * @param event
	 *            the produced event
	 */
	void eventReceived(ISelfletEvent event);
}