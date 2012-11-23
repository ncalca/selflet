package it.polimi.elet.selflet.events.behavior;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;

/**
 * @author silvia
 */
public class BehaviourStateChangedEvent extends SelfletEvent {

	private final String behaviourName;
	private final String stateName;

	public BehaviourStateChangedEvent(String behaviourName, String stateName) {
		this.behaviourName = behaviourName;
		this.stateName = stateName;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.BEHAVIOR_STATE_CHANGED;
	}

	public String getBehaviourName() {
		return behaviourName;
	}

	public String getStateName() {
		return stateName;
	}
}