package it.polimi.elet.selflet.events;

/**
 * An empty implementation for event dispatcher used for test purposes
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class EmptyEventDispatcher implements IEventDispatcher {

	@Override
	public void setEventDispatcher(IEventDispatcher dispatcher) {
	}

	@Override
	public void removeEventDispatcher(IEventDispatcher dispatcher) {
	}

	@Override
	public void dispatchEvent(ISelfletEvent event) {
	}

	@Override
	public void registerSelfLetComponent(ISelfletComponent selfletComponent) {
	}

	@Override
	public void run() {
	}

	@Override
	public void stop() {
	}

	@Override
	public void clearEventQueue() {
	}

}
