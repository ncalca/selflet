package it.polimi.elet.selflet.message;

import it.polimi.elet.selflet.events.DispatchingUtility;
import it.polimi.elet.selflet.events.IEventDispatcher;
import it.polimi.elet.selflet.events.message.MessageReceivedEvent;

import org.apache.log4j.Logger;

import polimi.reds.DispatchingService;

/**
 * An inner thread class, which monitors the received messages and produces
 * events if a message of a bound type is received.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * @author Davide Devescovi
 */
public class MessageDispatcherThread extends Thread {

	private static final Logger LOG = Logger
			.getLogger(MessageDispatcherThread.class);

	private final DispatchingService dispatchingService;
	private final IEventDispatcher eventDispatcher;

	private boolean stop;

	/**
	 * Constructs a DispatcherThread with the given parameters.
	 * 
	 * @param dispatchingService
	 *            the REDS dispatching service
	 * @param bindings
	 *            the bindings between message types and event types
	 */
	public MessageDispatcherThread(DispatchingService dispatchingService,
			IEventDispatcher eventDispatcher) {
		this.dispatchingService = dispatchingService;
		this.eventDispatcher = eventDispatcher;
		this.stop = false;
		this.setDaemon(true);
	}

	/**
	 * The method monitoring messages and producing events.
	 */
	public void run() {

		while (!stop) {
			RedsMessage redsMessage = (RedsMessage) dispatchingService
					.getNextMessage();
			SelfLetMsg selfletMsg = redsMessage.getMessage();
			selfletMsg.setId(redsMessage.getID());

			if (selfletMsg.getType() != SelfLetMessageTypeEnum.NODE_STATE)
				LOG.info("received message: " + selfletMsg);
			fireMessageReceivedEvent(selfletMsg);
		}
	}

	/**
	 * Stops the thread by setting the stop parameter.
	 */
	public void stopThread() {
		this.stop = true;
	}

	private void fireMessageReceivedEvent(SelfLetMsg selfletMessage) {
		// LOG.debug("firing message to dispatching utility");
		DispatchingUtility.dispatchEvent(eventDispatcher,
				MessageReceivedEvent.class, selfletMessage);
	}
}
