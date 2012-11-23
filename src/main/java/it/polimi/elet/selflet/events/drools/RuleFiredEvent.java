package it.polimi.elet.selflet.events.drools;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;

/**
 * @author silvia
 */
public class RuleFiredEvent extends SelfletEvent {

	private final String ruleName;

	public RuleFiredEvent(String ruleName) {
		this.ruleName = ruleName;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.RULE_FIRED;
	}

	public String getRuleName() {
		return ruleName;
	}
}