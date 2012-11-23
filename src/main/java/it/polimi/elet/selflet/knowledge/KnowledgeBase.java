package it.polimi.elet.selflet.knowledge;

import it.polimi.elet.selflet.events.SelfletComponent;
import it.polimi.elet.selflet.events.property.PropertyDeletedEvent;
import it.polimi.elet.selflet.events.property.PropertySetEvent;
import it.polimi.elet.selflet.events.property.PropertyUpdatedEvent;
import it.polimi.elet.selflet.exceptions.AlreadyPresentException;
import it.polimi.elet.selflet.exceptions.NotFoundException;

import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.Maps;
import com.google.inject.Singleton;

/**
 * This class contains the knowledge related to ace's type This knowledge is
 * organized in couples of kind <code><typeCathegory, value></code>
 * <code>typeCathegory</code> is a String, while its <code>value</code> is a
 * generic Object, to let the implementation and the adding of new features more
 * free It is managed by the TypeKnowledgeManager
 * 
 * @author silvia
 */
@Singleton
public class KnowledgeBase extends SelfletComponent implements IGeneralKnowledge {

	private static final Logger LOG = Logger.getLogger(KnowledgeBase.class);

	public static final String EMPTY_FIELD = "empty";
	public static final String ERROR = "ERROR";

	private Map<String, Object> knowledge;

	public KnowledgeBase() {
		knowledge = Maps.newConcurrentMap();
	}

	public void deleteProperty(String attributeName) {

		if (attributeName == null) {
			throw new IllegalArgumentException("Attribute name can't be null to delete its properties");
		}

		if (!isPropertyPresent(attributeName)) {
			return;
		}

		Object propertyValue = knowledge.get(attributeName);

		knowledge.remove(attributeName);

		LOG.debug("Deleted variable " + attributeName + " and its value from the spacework");

		fireAttributePropertyDeletedEvent(attributeName, propertyValue);

	}

	/**
	 * @return the whole map containing variables and related values
	 */
	public Map<String, Object> getProperties() {
		return knowledge;
	}

	public Object getProperty(String name) throws NotFoundException {
		if (name == null) {
			throw new IllegalArgumentException("Variable identifier can't be null");
		}

		if (knowledge.get(name) == null) {
			throw new NotFoundException("Parameter Property Not Found: " + name);
		}

		return knowledge.get(name);
	}

	public boolean isPropertyPresent(String name) {
		return knowledge.containsKey(name);
	}

	/**
	 * this method should be used do ADD a new variable to the ace's workspace
	 */
	public void setProperty(String name, Object property) throws AlreadyPresentException {

		if (name == null) {
			throw new IllegalArgumentException("Property name can't be null");
		}

		if (property == null) {
			throw new IllegalArgumentException("Property value for \"" + name + "\" can't be null");
		}

		if (isPropertyPresent(name)) {
			throw new AlreadyPresentException("The variable " + name + " is already defined; use the " + "updateProperty method to update it");
		}

		knowledge.put(name, property);

		LOG.debug("Set variable " + name + " to value '" + property + "'");

		fireAttributePropertySetEvent(name, property);

	}

	public void setOrUpdateProperty(String name, Object property) {
		try {
			setProperty(name, property);
		} catch (AlreadyPresentException e) {
			updateProperty(name, property);
		}
	}

	/**
	 * this method should be used do SET an existing variable to a new value
	 */
	public void updateProperty(String name, Object property) {

		if (name == null) {
			throw new IllegalArgumentException("The name of the feature to update can't be null");
		}

		if (!isPropertyPresent(name)) {
			try {
				setProperty(name, property);
			} catch (AlreadyPresentException e) {
				// Can't happen
			}
		}

		Object previousValue = knowledge.get(name);

		knowledge.put(name, property);

		LOG.debug("Updated KnowledgeBase variable " + name + " to value " + property);

		fireAttributePropertyUpdatedEvent(name, property, previousValue);
	}

	private void fireAttributePropertySetEvent(String propertyName, Object propertyValue) {
		dispatchEvent(PropertySetEvent.class, propertyName, propertyValue);
	}

	private void fireAttributePropertyUpdatedEvent(String propertyName, Object currentValue, Object previousValue) {
		dispatchEvent(PropertyUpdatedEvent.class, propertyName, currentValue, previousValue);
	}

	private void fireAttributePropertyDeletedEvent(String propertyName, Object propertyValue) {
		dispatchEvent(PropertyDeletedEvent.class, propertyName, propertyValue);
	}

}