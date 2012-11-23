package it.polimi.elet.selflet.events.behavior;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;

/**
 * @author silvia
 */
public class BehaviourDeactivatedEvent extends SelfletEvent {

	private final String behaviourName;

	public BehaviourDeactivatedEvent(String behaviourName) {
		this.behaviourName = behaviourName;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.BEHAVIOR_DEACTIVATED;
	}

	public String getBehaviourName() {
		return behaviourName;
	}
}
