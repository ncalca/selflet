package it.polimi.elet.selflet.knowledge;

import it.polimi.elet.selflet.events.ISelfletComponent;
import it.polimi.elet.selflet.events.SelfletComponent;
import it.polimi.elet.selflet.events.property.PropertyDeletedEvent;
import it.polimi.elet.selflet.events.property.PropertySetEvent;
import it.polimi.elet.selflet.events.property.PropertyUpdatedEvent;

/**
 * Abstract class for a general internal knowledge. It provides a list of
 * dispatchers and the methods to fire events whenever the knowledge is changed
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public abstract class Knowledge extends SelfletComponent implements ISelfletComponent {

	protected void firePropertySetEvent(String propertyName, Object propertyValue) {
		dispatchEvent(PropertySetEvent.class, propertyName, propertyValue);
	}

	protected void firePropertyUpdatedEvent(String propertyName, Object currentValue, Object previousValue) {
		dispatchEvent(PropertyUpdatedEvent.class, propertyName, currentValue, previousValue);
	}

	protected void firePropertyDeletedEvent(String propertyName, Object propertyValue) {
		dispatchEvent(PropertyDeletedEvent.class, propertyName, propertyValue);
	}

}
