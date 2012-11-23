package it.polimi.elet.selflet.ability;

import java.io.Serializable;

/**
 * The interface for the descriptor of an Ability. It only has the
 * <code>getAbilityType</code> method, because the implementing class will have
 * to provide the specific methods needed for the corresponding Execution
 * Environment.
 * 
 * @author Davide Devescovi
 * 
 */
public interface IAbilityDescriptor extends Serializable {

	/**
	 * Returns a String representing the type of the Ability implementation
	 * being used.
	 * 
	 * @return a String representing the Ability implementation being used
	 */
	String getAbilityType();
}
