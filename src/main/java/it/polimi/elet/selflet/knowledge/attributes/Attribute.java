package it.polimi.elet.selflet.knowledge.attributes;

import it.polimi.elet.selflet.exceptions.NotFoundException;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Represents an attribute characterizing the selflet and useful to classify it
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 */
public abstract class Attribute {

	protected Map<String, Object> parameterMap = Maps.newHashMap();

	/**
	 * Each attribute has a name (eg. physical location, logical location etc).
	 * This method returns the attribute name
	 */
	public abstract String getName();

	/**
	 * Returns a map containing all the attribute contained within the attribute
	 */
	public Map<String, Object> getAllParameters() {
		return Maps.newHashMap(parameterMap);
	}

	public Object getParameter(String name) {

		if (parameterMap.containsKey(name)) {
			return parameterMap.get(name);
		}

		throw new NotFoundException("Parameter " + name + " cannot be found (maybe not yet set) on Attribute " + getName());
	}

	public void setParameter(String name, Object value) {
		parameterMap.put(name, value);
	}

	@Override
	public String toString() {
		return " [" + getName() + ": " + parameterMap + " ]";
	}
}
