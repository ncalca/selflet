package it.polimi.elet.selflet.events;

import com.google.common.collect.ImmutableSet;

/**
 * Defines an abstract class for a selflet internal subsystem (produce and
 * consumer)
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public abstract class SelfletComponent implements ISelfletComponent {

	protected IEventDispatcher dispatcher;

	public SelfletComponent() {
		this.dispatcher = new EmptyEventDispatcher();
	}

	public void setEventDispatcher(IEventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public void removeEventDispatcher(IEventDispatcher dispatcher) {
		this.dispatcher = new EmptyEventDispatcher();
	}

	public void dispatchEvent(Class<?> clazz, Object... constructorInput) {
		DispatchingUtility.dispatchEvent(dispatcher, clazz, constructorInput);
	}

	/**
	 * By default the component ignores events
	 * */
	public void eventReceived(ISelfletEvent event) {
	}

	/**
	 * By default the component is not interested in receiving events
	 * */
	@Override
	public ImmutableSet<EventTypeEnum> getReceivedEvents() {
		return ImmutableSet.of();
	}

}
