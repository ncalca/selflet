package it.polimi.elet.selflet.events.prediction;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.ISelfletEvent;
import it.polimi.elet.selflet.events.SelfletEvent;

public class FrequentlyExecutedEvent extends SelfletEvent {

	/** The frequent event instance */
	private final ISelfletEvent event;

	private Double support;
	private final String module_id;

	public FrequentlyExecutedEvent(ISelfletEvent event, Double support, String module_id) {
		this.event = event;
		this.support = support;
		this.module_id = module_id;
	}

	public EventTypeEnum getFrequentEventMainType() {
		return event.getEventType();
	}

	public double getSupport() {
		return support;
	}

	public String getModule_id() {
		return module_id;
	}

	public ISelfletEvent getEvent() {
		return event;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.FREQUENT_EVENT_SUBTYPE;
	}

	public String toString() {
		return "Frequently executed event: " + event;
	}

}
