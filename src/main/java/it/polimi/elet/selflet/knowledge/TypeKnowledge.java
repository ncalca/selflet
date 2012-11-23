package it.polimi.elet.selflet.knowledge;

import it.polimi.elet.selflet.exceptions.AlreadyPresentException;
import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.exceptions.NotImplementedExeception;
import it.polimi.elet.selflet.knowledge.attributes.Attribute;

import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.Maps;
import com.google.inject.Singleton;

/**
 * This class contains the knowledge related to selflet's type.
 * 
 * This knowledge is organized in couples of kind
 * <code><typeCathegory, value></code> <code>typeCathegory</code> is a String,
 * while its <code>value</code> is a generic Object, to let the implementation
 * and the adding of new features more free It is managed by the
 * TypeKnowledgeManager
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * @author silvia
 */
@Singleton
public class TypeKnowledge extends Knowledge implements ITypeKnowledge {

	private static final Logger LOG = Logger.getLogger(TypeKnowledge.class);

	private final Map<String, Object> typeEntries;

	public TypeKnowledge() {
		typeEntries = Maps.newConcurrentMap();
	}

	public void deleteProperty(String typeName) {
		if (typeName == null) {
			throw new IllegalArgumentException("Type's feature can't be null to delete its properties");
		}

		if (!isPropertyPresent(typeName)) {
			return;
		}

		Object propertyValue = typeEntries.get(typeName);
		typeEntries.remove(typeName);
		LOG.debug("Deleted Type feature " + typeName + " and it's value");
		fireTypePropertyDeletedEvent(typeName, propertyValue);
	}

	/**
	 * @return the whole map containing type features and related values
	 */
	public Map<String, Object> getProperties() {
		return typeEntries;
	}

	public Object getProperty(String name) throws NullPointerException, NotFoundException {

		if (name == null) {
			throw new IllegalArgumentException("Type feature can't be null");
		}

		if (typeEntries.get(name) == null) {
			throw new NotFoundException("Type Property Not Found: " + name);
		}

		return typeEntries.get(name);
	}

	public boolean isPropertyPresent(String name) {
		return typeEntries.containsKey(name);
	}

	/**
	 * this method should be used do ADD a new feature to the ace's type
	 */
	public void setProperty(String name, Object property) throws AlreadyPresentException {

		if (name == null) {
			throw new IllegalArgumentException("Type feature can't be null");
		}

		if (isPropertyPresent(name)) {
			throw new AlreadyPresentException("The feature " + name + " of this ace's type is already defined; use the " + "updateProperty method to update it");
		}

		typeEntries.put(name, property);
		LOG.debug("Set Type feature " + name + " to value " + (property != null ? property.toString() : "null"));

		fireTypePropertySetEvent(name, property);
	}

	public void setOrUpdateProperty(String name, Object property) {
		try {
			setProperty(name, property);
		} catch (AlreadyPresentException e) {
			updateProperty(name, property);
		}
	}

	/**
	 * this method should be used do SET an existing feature of the ace's type
	 * to a new value
	 */
	public void updateProperty(String name, Object property) {
		if (name == null) {
			throw new IllegalArgumentException("The name of the feature to update can't be null");
		}

		if (!isPropertyPresent(name)) {
			setProperty(name, property);
		}

		Object previousValue = typeEntries.get(name);
		typeEntries.put(name, property);
		LOG.debug("Updated Type Feature " + name + " to value " + (property != null ? property.toString() : "null"));
		fireTypePropertyUpdatedEvent(name, property, previousValue);
	}

	public void setAttribute(Attribute attribute) {
		throw new NotImplementedExeception("setAttribute");
	}

	private void fireTypePropertySetEvent(String propertyName, Object propertyValue) {
		firePropertySetEvent(propertyName, propertyValue);
	}

	private void fireTypePropertyUpdatedEvent(String propertyName, Object currentValue, Object previousValue) {
		firePropertyUpdatedEvent(propertyName, currentValue, previousValue);
	}

	private void fireTypePropertyDeletedEvent(String propertyName, Object propertyValue) {
		firePropertyDeletedEvent(propertyName, propertyValue);
	}

}
