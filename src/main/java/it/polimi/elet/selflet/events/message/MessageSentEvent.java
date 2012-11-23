package it.polimi.elet.selflet.events.message;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;
import it.polimi.elet.selflet.message.SelfLetMsg;

/**
 * @author silvia
 */
public class MessageSentEvent extends SelfletEvent {

	private final SelfLetMsg message;

	public MessageSentEvent(SelfLetMsg message) {
		this.message = message;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.MESSAGE_SENT;
	}

	public SelfLetMsg getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "Message sent toward " + message.getTo();
	}
}
