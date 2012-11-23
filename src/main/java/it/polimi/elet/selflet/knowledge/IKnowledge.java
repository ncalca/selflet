package it.polimi.elet.selflet.knowledge;

import it.polimi.elet.selflet.events.ISelfletComponent;
import it.polimi.elet.selflet.exceptions.AlreadyPresentException;
import it.polimi.elet.selflet.exceptions.IncompatibleTypeException;
import it.polimi.elet.selflet.exceptions.NotFoundException;

import java.util.Map;

/**
 * This interface represent the general interface for knowledges. It can be
 * parametrized according to the type of objects to be stored in
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * @author Davide
 */
interface IKnowledge<T> extends ISelfletComponent {

	/**
	 * Returns the value of the specified Property, in the form of an Object.
	 * 
	 * @param name
	 *            the Property's name
	 * 
	 * @return the value of the Property
	 * @throws NotFoundException
	 * 
	 * @throws NullPointerException
	 *             if the specified Property does not exist
	 */
	T getProperty(String name);

	/**
	 * Sets a new Property with the given name and value.
	 * 
	 * @param name
	 *            the name of the Property
	 * @param property
	 *            the value of the Property
	 * 
	 * @throws AlreadyPresentException
	 *             if the specified name is already registered with a
	 * @throws IncompatibleTypeException
	 */
	void setProperty(String name, T property);

	/**
	 * If the property name does not exists, the method creates it and set the
	 * value. If the property exists, the value is update with the new one.
	 * 
	 * @param name
	 *            the name of the Property
	 * @param property
	 *            the value of the Property
	 * */
	void setOrUpdateProperty(String name, T property);

	/**
	 * Updates a Property, or creates a new one if needed, with the given name
	 * and value.
	 * 
	 * @param name
	 *            the name of the Property
	 * @param property
	 *            the value of the Property
	 * @throws IncompatibleTypeException
	 */
	void updateProperty(String name, T property);

	/**
	 * Deletes the specified Property.
	 * 
	 * @param name
	 *            the name of the Property
	 */
	void deleteProperty(String name);

	/**
	 * Tells whether a specified Property is present in the Internal Knowledge.
	 * 
	 * @param name
	 *            the name of the Property
	 * 
	 * @return true if the Property is present, false otherwise
	 */
	boolean isPropertyPresent(String name);

	/**
	 * Returns all the Properties stored in the Internal Knowledge.
	 * 
	 * @return a Map of Properties
	 */
	Map<String, T> getProperties();
}