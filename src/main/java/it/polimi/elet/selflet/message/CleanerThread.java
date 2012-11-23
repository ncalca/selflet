package it.polimi.elet.selflet.message;

import java.util.Set;

import org.apache.log4j.Logger;

import polimi.reds.DispatchingService;

/**
 * An thread class, which monitors the message queue and deletes unexpected
 * messages.
 * 
 * @author Davide Devescovi
 */
public class CleanerThread extends Thread {

	private static final Logger LOG = Logger.getLogger(CleanerThread.class);

	private final DispatchingService dispatchingService;
	private final Set<SelfLetMessageTypeEnum> bindings;

	private boolean stop;

	/**
	 * Constructs a CleanerThread with the given parameters.
	 * 
	 * @param dispatchingService
	 *            the DispatchingService
	 * @param bindings
	 *            the bindings between message types and event types
	 * @param acceptableTypes
	 *            the list of acceptable types
	 */
	public CleanerThread(DispatchingService dispatchingService, Set<SelfLetMessageTypeEnum> bindings) {

		this.dispatchingService = dispatchingService;
		this.bindings = bindings;

		this.stop = false;
		this.setDaemon(true);
	}

	/**
	 * The method which monitors the queue and deletes messages.
	 */
	public void run() {

		while (!stop) {

			RedsMessageCleanFilter filter = new RedsMessageCleanFilter(bindings);

			while (dispatchingService.hasMoreMessages(filter)) {

				// Just get and consume it
				RedsMessage rawMsg = (RedsMessage) dispatchingService.getNextMessage(filter);

				SelfLetMsg msg = rawMsg.getMessage();

				LOG.debug("Deleted unacceptable message of type [" + msg.getType() + "] from " + msg.getFrom());
			}

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Stops the thread.
	 */
	public void stopThread() {
		this.stop = true;
	}
}
