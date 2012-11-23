package it.polimi.elet.selflet.events.behavior;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;

/**
 * @author silvia
 */
public class BehaviourActivatedEvent extends SelfletEvent {

	private final String behaviourName;

	public BehaviourActivatedEvent(String behaviourName) {
		this.behaviourName = behaviourName;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.BEHAVIOR_ACTIVATED;
	}

	public String getBehaviourName() {
		return behaviourName;
	}
}