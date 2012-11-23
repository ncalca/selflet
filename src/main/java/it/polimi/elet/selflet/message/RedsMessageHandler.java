package it.polimi.elet.selflet.message;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletComponent;
import it.polimi.elet.selflet.events.message.MessageSentEvent;
import it.polimi.elet.selflet.exceptions.ConnectionException;
import it.polimi.elet.selflet.exceptions.InvalidValueException;
import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.utilities.ThreadUtilities;

import java.net.ConnectException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import polimi.reds.DispatchingService;
import polimi.reds.Message;
import polimi.reds.MessageID;
import polimi.reds.TCPDispatchingService;
import polimi.reds.TimeoutException;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * A REDS implementation of the IMessageHandler interface.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * @author Davide Devescovi
 */
@Singleton
public class RedsMessageHandler extends SelfletComponent implements IMessageHandler {

	private static final Logger LOG = Logger.getLogger(RedsMessageHandler.class);

	private static final int WAIT_STEP = 2000;

	private final IRedsMessageFactory redsMessageFactory;
	private final ISelfLetID selfLetId;
	private final Map<SelfLetMessageTypeEnum, EventTypeEnum> bindings;

	private String address;
	private Integer port;
	private DispatchingService dispatchingService;
	private MessageDispatcherThread dispatcherThread;

	// private CleanerThread cleanerThread;

	@Inject
	public RedsMessageHandler(ISelfLetID selfletID, IRedsMessageFactory redsMessageFactory) {
		this.selfLetId = selfletID;
		this.redsMessageFactory = redsMessageFactory;
		this.bindings = new ConcurrentHashMap<SelfLetMessageTypeEnum, EventTypeEnum>(24, 0.75f, 8);
	}

	/* ****************************** */
	/* IMessageHandler implementation */
	/* ****************************** */

	public void connect(String address, Integer port) throws ConnectionException {

		if (dispatcherThread != null && dispatcherThread.isAlive()) {
			dispatcherThread.stopThread();
			dispatcherThread = null;
		}

		this.address = address;
		this.port = port;

		LOG.info("Trying to connect to " + address + ":" + port + "...");

		dispatchingService = new TCPDispatchingService(address, port);

		try {
			dispatchingService.open();
		} catch (ConnectException e) {
			LOG.error("Error while connecting to REDS", e);
			throw new ConnectionException("Exception raised during connection", e);
		}

		if (dispatchingService.isOpened()) {
			LOG.info("Connection established");
		} else {
			LOG.error("Error while connecting to REDS");
			throw new ConnectionException("Exception raised during connection");
		}

		// Subscribe to all the messages that have my id as one of the
		// recipients
		dispatchingService.subscribe(new RedsMessageFilter(selfLetId));

		// Subscribe to broadcast messages
		dispatchingService.subscribe(new RedsBroadcastFilter());

		// Start the dispatcher thread, which converts messages into events
		dispatcherThread = new MessageDispatcherThread(dispatchingService, dispatcher);
		ThreadUtilities.submitGenericJob(dispatcherThread);
		// dispatcherThread.start();

		// Start the cleaner thread, which removes unwanted messages from REDS
		// queue
		// cleanerThread = new CleanerThread(dispatchingService,
		// bindings.keySet());
		// cleanerThread.start();
	}

	public void disconnect() {

		if (dispatchingService == null) {
			return;
		}

		dispatchingService.close();

		if (dispatcherThread != null && dispatcherThread.isAlive()) {
			dispatcherThread.stopThread();
			dispatcherThread = null;
		}
		LOG.debug("Disconnected");
	}

	public MessageID send(SelfLetMsg message) {

		RedsMessage redsMessage = redsMessageFactory.createNewRedsMessage(message);
		dispatchingService.publish(redsMessage);

		LOG.debug("Published message " + message.getType() + " to " + message.getTo());

		fireMessageSentEvent(message);
		return redsMessage.getID();
	}

	public Object reply(SelfLetMsg reply, Object originalID) {

		if (bindings.containsKey(reply.getType())) {
			throw new UnsupportedOperationException("Can't use the \"reply\" method " + "with a message of type " + reply.getType()
					+ " because that type is bound to an event");
		}

		RedsMessage redsMessage = redsMessageFactory.createNewRedsMessage(reply);
		dispatchingService.reply(redsMessage, (MessageID) originalID);
		LOG.debug("Replied to message " + originalID.toString() + " with message " + reply);
		fireMessageSentEvent(reply);
		return redsMessage.getID();
	}

	public SelfLetMsg retrieveMessage(SelfLetMessageTypeEnum messageType, int timeout) {

		if (timeout < 0) {
			throw new InvalidValueException("The timeout can't be less than 0");
		}

		// each time a new message type filter is created, they can be cached
		RedsMessageTypeFilter messageTypeFilter = new RedsMessageTypeFilter(ImmutableSet.of(messageType));

		int counter = 0;
		while (counter <= timeout) {

			// Look for messages of the specified type
			if (dispatchingService.hasMoreMessages(messageTypeFilter)) {
				RedsMessage rawMsg = (RedsMessage) dispatchingService.getNextMessage(messageTypeFilter);
				SelfLetMsg msg = rawMsg.getMessage();
				msg.setId(rawMsg.getID());
				return msg;
			}

			// Wait
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
			counter += 10;
		}

		throw new NotFoundException("No messages with type " + messageType + " were found before timeout");
	}

	public SelfLetMsg retrieveReply(Object originalID, int timeout) {

		if (timeout < 0) {
			throw new InvalidValueException("The timeout can't be less than 0");
		}

		int waitCounter = 0;

		while (waitCounter <= timeout) {
			// If the original message has replies...
			while (dispatchingService.hasMoreReplies((MessageID) originalID)) {

				Message replyMsg = null;
				// ...look for the next reply
				try {

					replyMsg = dispatchingService.getNextReply((MessageID) originalID);
				} catch (TimeoutException e) {
					LOG.error(e);
				}

				if (replyMsg == null) {
					throw new NotFoundException("No replies to message " + originalID.toString() + " were found before timeout");
				}
				LOG.debug("Have reply: " + replyMsg.getID());

				RedsMessage rawMsg = (RedsMessage) replyMsg;
				SelfLetMsg msg = rawMsg.getMessage();
				msg.setId(rawMsg.getID());

				return msg;
			}

			// Wait
			try {
				Thread.sleep(WAIT_STEP);
			} catch (InterruptedException e) {
			}
			waitCounter += WAIT_STEP;

			LOG.debug("No reply: waited for " + waitCounter + "ms, waiting again...");
		}

		throw new NotFoundException("No replies to message " + originalID.toString() + " were found");
	}

	public List<SelfLetMsg> retrieveReplies(Object originalID, int timeout) {

		if (timeout < 0) {
			throw new InvalidValueException("The timeout can't be less than 0");
		}

		List<SelfLetMsg> messagesRetrieved = Lists.newArrayList();

		int counter = 0;

		LOG.debug("Looking for reply " + originalID);
		while (counter <= timeout) {

			// As long as the original message has replies...
			while (dispatchingService.hasMoreReplies((MessageID) originalID)) {

				Message message = null;
				// ...look for the next reply
				try {
					message = dispatchingService.getNextReply((MessageID) originalID);
				} catch (TimeoutException e) {
					LOG.debug("Timed out", e);
				}

				if (message == null) {
					LOG.info("Retrieved null msg");
					break;
				}

				LOG.debug("Have reply: " + message.getID());

				RedsMessage rawMsg = (RedsMessage) message;
				SelfLetMsg msg = rawMsg.getMessage();
				msg.setId(rawMsg.getID());
				messagesRetrieved.add(msg);
			}

			// Wait
			try {
				Thread.sleep(WAIT_STEP);
			} catch (InterruptedException e) {
			}

			LOG.debug("Counter: " + counter);
			counter += WAIT_STEP;
		}

		// If no replies were received, throw
		if (messagesRetrieved.isEmpty()) {
			throw new NotFoundException("No replies to message " + originalID.toString() + " were found");
		}

		return messagesRetrieved;
	}

	/**
	 * Adds messageType and eventSubtype to the binding hashtable.
	 * */
	public void bindMessagesToEvents(SelfLetMessageTypeEnum messageType, EventTypeEnum eventType) {
		bindings.put(messageType, eventType);
		LOG.debug("Bound " + messageType + " to " + eventType);
	}

	public void unbindMessagesToEvent(String messageType) {

		EventTypeEnum eventType = null;

		synchronized (bindings) {
			SelfLetMessageTypeEnum messageTypeEnum = SelfLetMessageTypeEnum.valueOf(messageType);
			eventType = bindings.get(messageTypeEnum);
			bindings.remove(messageTypeEnum);
			LOG.debug("Message Handler: unbound " + messageType);
			bindings.notifyAll();
		}

		if (eventType == null) {
			return;
		}

	}

	public String getAddress() {
		return address;
	}

	public Integer getPort() {
		return port;
	}

	private void fireMessageSentEvent(SelfLetMsg msg) {
		dispatchEvent(MessageSentEvent.class, msg);
	}

}