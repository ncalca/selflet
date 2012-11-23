package it.polimi.elet.selflet.events.property;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;

/**
 * @author silvia
 */
public class PropertyUpdatedEvent extends SelfletEvent {

	private final String propertyName;
	private final Object currentValue;
	private final Object previousValue;

	public PropertyUpdatedEvent(String propertyName, Object currentValue, Object previousValue) {
		this.propertyName = propertyName;
		this.currentValue = currentValue;
		this.previousValue = previousValue;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.PROPERTY_UPDATED;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public Object getCurrentValue() {
		return currentValue;
	}

	public Object getPreviousValue() {
		return previousValue;
	}

	public String toString() {
		return "Update " + propertyName + " from " + previousValue + " to " + currentValue;
	}
}