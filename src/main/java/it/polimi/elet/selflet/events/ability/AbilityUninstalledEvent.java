package it.polimi.elet.selflet.events.ability;

import it.polimi.elet.selflet.ability.AbilitySignature;
import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;

/**
 * @author silvia
 */
public class AbilityUninstalledEvent extends SelfletEvent {

	private final AbilitySignature signature;

	public AbilityUninstalledEvent(AbilitySignature signature) {
		this.signature = signature;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.ABILITY_UNINSTALLED;
	}

	public AbilitySignature getSignature() {
		return signature;
	}
}
