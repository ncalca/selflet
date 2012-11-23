package it.polimi.elet.selflet.events.drools;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;

/**
 * @author silvia
 */
public class RuleRemovedEvent extends SelfletEvent {

	private final String ruleName;

	public RuleRemovedEvent(String ruleName) {
		this.ruleName = ruleName;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.RULE_REMOVED;
	}

	public String getRuleName() {
		return ruleName;
	}
}