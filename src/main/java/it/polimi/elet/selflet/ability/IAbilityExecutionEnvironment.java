package it.polimi.elet.selflet.ability;

import it.polimi.elet.selflet.events.ISelfletComponent;

import java.io.Serializable;
import java.util.List;

/**
 * The interface of an Ability Execution Environment; the AEE stores and
 * executes the various Abilities of the SelfLet. The AEE is also an event
 * producer (<code>IEventProducer</code>).
 * 
 * @author Davide Devescovi
 */
public interface IAbilityExecutionEnvironment extends ISelfletComponent {

	String FRAMEWORK_DIR_PROP = "org.osgi.framework.dir";
	String SETUP_METHOD_NAME = "setup";
	String TEARDOWN_METHOD_NAME = "tearDown";

	/**
	 * Starts the environment.
	 * 
	 * @throws AbilityEnvironmentException
	 *             if an error occurs when starting the environment
	 * @throws IncompatibleTypeException
	 *             if a preinstalled Ability is not compatible with the type of
	 *             this environment
	 */
	void startEnvironment();

	/**
	 * Stops the environment.
	 */
	void stopEnvironment();

	/**
	 * Adds an Ability to the environment, making it available for execution.
	 * 
	 * @param ability
	 *            the <code>AbstractAbility</code> object to be added
	 * 
	 * @throws AbilityEnvironmentException
	 *             if an error occurs while installing the Ability
	 * @throws AlreadyPresentException
	 *             when trying to add an already installed Abiliy
	 * @throws IncompatibleTypeException
	 *             if the type of the Ability is not compatible with the type of
	 *             this environment
	 */
	void addAbility(AbstractAbility ability);

	/**
	 * TODO
	 * */
	void installAbility(String inputFileName, String abilityName, String serviceName, String methodName);

	/**
	 * Removes the specified Ability from the environment.
	 * 
	 * @param abilitySignature
	 *            the unique ability signature
	 * 
	 * @throws AbilityEnvironmentException
	 *             if an error occurs during the uninstallation of the Ability
	 * @throws IncompatibleTypeException
	 *             if the type of the Ability is not compatible with the type of
	 *             this environment
	 */
	void removeAbility(AbilitySignature abilitySignature);

	/**
	 * Executes the specified Ability with the given parameters.
	 * 
	 * @param abilitySignature
	 *            the unique Ability signature
	 * @param methodName
	 *            the name of the method to be invoked
	 * @param params
	 *            the optional list of parameters required by the method
	 * 
	 * @return the generic Serializable object returned by the method invocation
	 *         (or null if the invoked method has a void return type)
	 * 
	 * @throws AbilityEnvironmentException
	 *             if an error occurs when trying to execute the Ability
	 * @throws NotFoundException
	 *             if the requested Ability is not installed
	 */
	Serializable execute(AbilitySignature abilitySignature, String methodName, Object... params);

	/**
	 * Checks whether the specified Ability is present in the environment.
	 * 
	 * @param abilitySignature
	 *            the unique Ability signature
	 * 
	 * @return true if the Ability is present, false otherwise
	 */
	boolean isPresent(AbilitySignature abilitySignature);

	/**
	 * Retrieves the specified Ability from the environment as an
	 * AbstractAbility object.
	 * 
	 * @param abilitySignature
	 *            the unique Ability signature
	 * 
	 * @return the desidred Ability
	 * 
	 * @throws NotFoundException
	 *             if the required Ability is not found, or if there is an error
	 *             while retrieving it
	 */
	AbstractAbility getAbility(AbilitySignature abilitySignature);

	/**
	 * Retrieves all the installed Abilities from the environment as a List of
	 * AbilitySignature objects.
	 * 
	 * @return the List of installed Abilities
	 */
	List<AbilitySignature> listAbilities();

}