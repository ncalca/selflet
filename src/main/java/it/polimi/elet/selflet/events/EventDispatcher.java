package it.polimi.elet.selflet.events;

import it.polimi.elet.selflet.exceptions.NotImplementedExeception;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.inject.Singleton;

/**
 * An implementation of the event dispatcher
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class EventDispatcher implements IEventDispatcher {

	private static final Logger LOG = Logger.getLogger(EventDispatcher.class);
	private static final int QUEUE_SIZE = 1000;

	private final BlockingQueue<ISelfletEvent> queue;
	private final Multimap<EventTypeEnum, IEventListener> listenerMapping;
	private boolean stopThread;

	public EventDispatcher() {
		listenerMapping = HashMultimap.create();
		queue = new ArrayBlockingQueue<ISelfletEvent>(QUEUE_SIZE);
		stopThread = false;
	}

	/**
	 * Runs the dispatching cycle.
	 * */
	public void run() {
		stopThread = false;
		while (!stopThread) {
			ISelfletEvent event = getEventFromQueue();
			dispatchEventToListeners(event);
		}
	}

	private ISelfletEvent getEventFromQueue() {
		try {
			return queue.take();
		} catch (InterruptedException e) {
			throw new IllegalStateException();
		}
	}

	private void dispatchEventToListeners(final ISelfletEvent event) {

		Set<IEventListener> candidateListeners = getAllCandidateListeners(event.getEventType());

		for (IEventListener listener : candidateListeners) {
			listener.eventReceived(event);
		}
	}

	private Set<IEventListener> getAllCandidateListeners(EventTypeEnum eventTypeEnum) {
		Collection<IEventListener> thisEventListeners = listenerMapping.get(eventTypeEnum);
		Collection<IEventListener> allEventsListeners = listenerMapping.get(EventTypeEnum.ALL_EVENTS);
		return Sets.union(Sets.newHashSet(thisEventListeners), Sets.newHashSet(allEventsListeners));
	}

	public void stop() {
		stopThread = true;
	}

	public void dispatchEvent(final ISelfletEvent event) {
		addToQueue(event);
	}

	private void addToQueue(ISelfletEvent event) {
		LOG.debug("Event received " + event);
		try {
			queue.put(event);
		} catch (InterruptedException e) {
			LOG.error("Error while putting event in the queue", e);
		}
	}

	@Override
	public void registerSelfLetComponent(ISelfletComponent selfletComponent) {
		registerReceivingEvents(selfletComponent);
		selfletComponent.setEventDispatcher(this);
	}

	private void registerReceivingEvents(ISelfletComponent selfletComponent) {
		ImmutableSet<EventTypeEnum> eventsReceivedByComponent = selfletComponent.getReceivedEvents();
		for (EventTypeEnum eventType : eventsReceivedByComponent) {
			listenerMapping.put(eventType, selfletComponent);
		}
	}

	@Override
	public void setEventDispatcher(IEventDispatcher dispatcher) {
		throw new NotImplementedExeception("setEventDispatcher");
	}

	@Override
	public void removeEventDispatcher(IEventDispatcher dispatcher) {
		throw new NotImplementedExeception("removeEventDispatcher");
	}

	@Override
	public void clearEventQueue() {
		queue.clear();
	}

	@Override
	public String toString() {
		return "Event dispatcher: " + listenerMapping;
	}
}
