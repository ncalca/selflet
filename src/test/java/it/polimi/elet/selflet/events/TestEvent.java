package it.polimi.elet.selflet.events;

public class TestEvent extends SelfletEvent implements ISelfletEvent {

	@Override
	public EventTypeEnum getEventType() {
		return EventTypeEnum.TEST_EVENT;
	}

}
