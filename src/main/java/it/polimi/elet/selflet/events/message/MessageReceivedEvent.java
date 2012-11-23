package it.polimi.elet.selflet.events.message;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;
import it.polimi.elet.selflet.message.SelfLetMsg;

/**
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * @author silvia
 */
public class MessageReceivedEvent extends SelfletEvent {

	private final SelfLetMsg message;

	public MessageReceivedEvent(SelfLetMsg message) {
		this.message = message;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.MESSAGE_RECEIVED;
	}

	public SelfLetMsg getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "Message received:" + message;
	}
}