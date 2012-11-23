package it.polimi.elet.selflet.events.drools;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;

/**
 * @author silvia
 */
public class RuleAddedEvent extends SelfletEvent {

	private final String ruleName;

	public RuleAddedEvent(String ruleName) {
		this.ruleName = ruleName;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.RULE_ADDED;
	}

	public String getRuleName() {
		return ruleName;
	}
}