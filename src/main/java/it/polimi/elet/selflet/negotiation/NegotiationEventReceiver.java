package it.polimi.elet.selflet.negotiation;

import static it.polimi.elet.selflet.events.EventTypeEnum.*;
import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.ISelfletEvent;
import it.polimi.elet.selflet.events.SelfletComponent;
import it.polimi.elet.selflet.events.message.MessageReceivedEvent;
import it.polimi.elet.selflet.message.SelfLetMessageTypeEnum;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.negotiation.messageHandlers.ISelfletMessageHandler;
import it.polimi.elet.selflet.negotiation.messageHandlers.ISelfletMessageHandlerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import static it.polimi.elet.selflet.message.SelfLetMessageTypeEnum.*;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * A thread that is in charge of receiving all negotiation events and starts the
 * correspondent operation
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class NegotiationEventReceiver extends SelfletComponent implements INegotiationEventReceiver, Runnable {

	private static final Logger LOG = Logger.getLogger(NegotiationEventReceiver.class);

	private static final int EVENT_QUEUE_SIZE = 200;

	private final ISelfletMessageHandlerFactory selfletMessageHandlerFactory;
	private final Map<SelfLetMessageTypeEnum, ISelfletMessageHandler> selfletMessageHandlers;

	// the queue maintaining the events produced by the dispatcher
	private final BlockingQueue<ISelfletEvent> eventQueue = new ArrayBlockingQueue<ISelfletEvent>(EVENT_QUEUE_SIZE);

	@Inject
	public NegotiationEventReceiver(ISelfletMessageHandlerFactory selfletMessageHandlerFactory) {
		this.selfletMessageHandlerFactory = selfletMessageHandlerFactory;
		this.selfletMessageHandlers = Maps.newHashMap();
	}

	private void initMessageHandlers() {
		this.selfletMessageHandlerFactory.setDispatcher(dispatcher);

		Set<SelfLetMessageTypeEnum> messageHandlerTypes = Sets.immutableEnumSet(ASK_NEEDED_SERVICE, GET_SERVICE_PROVIDER_INDIRECTLY,
				EXECUTE_ACHIEVABLE_SERVICE, DOWNLOAD_ACHIEVABLE_SERVICE, ADVERTISE_ACHIEVABLE_SERVICE, NODE_STATE, REDIRECT_REQUEST, NEIGHBORS, SERVICE_TEACH,
				REDIRECT_REQUEST_REPLY, REMOVE_SELFLET, CHANGE_SERVICE_IMPLEMENTATION);

		for (SelfLetMessageTypeEnum messageType : messageHandlerTypes) {
			addMessageHandler(selfletMessageHandlerFactory.create(messageType));
		}
		
		LOG.debug("Message handlers initialized");
	}

	private void addMessageHandler(ISelfletMessageHandler selfletMessageHandler) {
		selfletMessageHandlers.put(selfletMessageHandler.getTypeOfHandledMessage(), selfletMessageHandler);
	}

	@Override
	public void eventReceived(ISelfletEvent event) {
		putEventInQueue(event);
	}

	private void putEventInQueue(ISelfletEvent event) {
		try {
			eventQueue.put(event);
		} catch (InterruptedException e) {
			LOG.error("Error while putting received event in the queue ", e);
		}
	}

	// thread code
	public void run() {
		initMessageHandlers();

		while (true) {
			ISelfletEvent event = getEventFromQueue();
			LOG.debug("Received [" + event + "]");
			messageFromMessageHandler(event);
		}
	}

	private ISelfletEvent getEventFromQueue() {
		try {
			// blocking queue
			return eventQueue.take();
		} catch (InterruptedException e) {
			LOG.error("Error while retrieving event from queue", e);
		}

		throw new IllegalStateException();
	}

	private void messageFromMessageHandler(ISelfletEvent event) {

		SelfLetMsg selfletMsg = ((MessageReceivedEvent) event).getMessage();
		SelfLetMessageTypeEnum messageType = selfletMsg.getType();
		ISelfletMessageHandler selfletMessageHandler = selfletMessageHandlers.get(messageType);

		if (selfletMessageHandler == null) {
			LOG.error("Received message for which no message handler is set. Ignoring it.");
			return;
		}

		selfletMessageHandler.handleMessage(selfletMsg);
	}

	@Override
	public ImmutableSet<EventTypeEnum> getReceivedEvents() {
		return ImmutableSet.of(MESSAGE_RECEIVED);
	}
}
