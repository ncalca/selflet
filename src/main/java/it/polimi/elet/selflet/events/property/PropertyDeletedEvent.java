package it.polimi.elet.selflet.events.property;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;

/**
 * @author silvia
 */
public class PropertyDeletedEvent extends SelfletEvent {

	private final String propertyName;
	private final Object propertyValue;

	public PropertyDeletedEvent(String propertyName, Object propertyValue) {
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.PROPERTY_DELETED;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public Object getPropertyValue() {
		return propertyValue;
	}

	@Override
	public String toString() {
		return "Delete " + propertyName;
	}
}
