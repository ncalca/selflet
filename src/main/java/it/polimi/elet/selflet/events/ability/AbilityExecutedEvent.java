package it.polimi.elet.selflet.events.ability;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;

/**
 * @author silvia
 */
public class AbilityExecutedEvent extends SelfletEvent {

	private String ability;
	private Object output;
	private boolean local;

	public AbilityExecutedEvent(String abilityName, Object output, Boolean local) {
		this.ability = abilityName;
		this.output = output;
		this.local = local;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.ABILITY_EXECUTED;
	}

	public String getAbility() {
		return this.ability;
	}

	public Object getOutput() {
		return output;
	}

	public boolean isLocal() {
		return local;
	}
}
