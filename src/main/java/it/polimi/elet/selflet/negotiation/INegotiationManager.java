package it.polimi.elet.selflet.negotiation;

import it.polimi.elet.selflet.events.ISelfletComponent;
import it.polimi.elet.selflet.exceptions.IncompatibleTypeException;
import it.polimi.elet.selflet.exceptions.InvalidValueException;
import it.polimi.elet.selflet.exceptions.NegotiationErrorException;
import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.service.RunningService;

/**
 * Interface enclosing main negotiation operations
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface INegotiationManager extends ISelfletComponent {

	/**
	 * Get the service provider for the given service in the asked mode. Perform
	 * a call to <code>getNeededServiceProvider(serviceName, mode, 1)</code>.
	 * 
	 * @param serviceName
	 *            the name of the asked service
	 * @param mode
	 *            the mode in which the service is asked
	 * 
	 * @return the service provider offering the service in the specified mode
	 * 
	 * @throws NegotiationErrorException
	 *             if no service provider is found for the given service
	 * 
	 * */
	ServiceProvider getNeededServiceProvider(String serviceName, ServiceAskModeEnum mode);

	/**
	 * Asks the other SelfLets for a specified service, with the given preferred
	 * <code>ServiceAskMode</code>. We try for <code>broadcastAttempts</code>
	 * times to get the service provider.
	 * 
	 * @param serviceName
	 *            the serviceName's name
	 * @param mode
	 *            the preferred interaction mode
	 * @param broadcastAttempts
	 *            the number of broadcast requests to attempt in case no
	 *            providers are known for this service
	 * 
	 * @return a service provider for the given service if one is found
	 * 
	 * @throws InvalidValueException
	 *             when broadcastAttempts is less than 0
	 * @throws NegotiationErrorException
	 *             if an error occurs on a remote Selflet
	 */
	ServiceProvider getNeededServiceProvider(String serviceName, ServiceAskModeEnum mode, int broadcastAttempts);

	/**
	 * Asks the given provider to remotely execute a service (needed for the
	 * caller, achievable for the callee).
	 * 
	 * 
	 * @param serviceParam
	 *            the parameter to invoke the service, which is dependent on the
	 *            SelfLet implementation of an Ability
	 * 
	 * @param providerId
	 *            the provider's id
	 * @param callingService
	 * 
	 * 
	 * @return the Object returned by the service's execution on the remote
	 *         SelfLet
	 * 
	 */
	void redirectRequestToProvider(ServiceExecutionParameter serviceParam, ISelfLetID providerId, RunningService callingService);

	/**
	 * Issues the command to request dispatcher to istantiate a new selflet
	 * */
	void istantiateNewSelflet();

	/**
	 * Download a service from a given provider.
	 * 
	 * It specifically downloads its 1) default behavior, 2) the actions, the 3)
	 * conditions. If the service is implemented by a simple behavior it also
	 * download the ability implementing included in the service
	 * 
	 * @param serviceName
	 *            the service's name
	 * @param providerId
	 *            the provider's id
	 * @param timeout
	 *            the maximum time to wait for a response, in milliseconds
	 * 
	 * @throws IncompatibleTypeException
	 * 
	 * @throws AbilityEnvironmentException
	 *             if an error occurs while installing the ability
	 * 
	 * @throws NegotiationErrorException
	 *             if an error occurs on a remote SelfLet
	 * 
	 * @throws InvalidValueException
	 *             if the timeout value is less than 0
	 */
	void downloadService(String serviceName, ISelfLetID providerId, int timeout);

	/**
	 * Sets an achievable service's offer mode.
	 * 
	 * @param serviceName
	 *            the service's name
	 * @param mode
	 *            the offer mode
	 * @param active
	 *            whether the offer mode is to be set true or false
	 * 
	 * @throws NotFoundException
	 *             if the service was not found, or if the offer mode is
	 *             serviceOfferMode.Both, serviceOfferMode.CanDo or
	 *             serviceOfferMode.CanTeach and if the service has no
	 *             corresponding ability installed in the environment
	 * @throws InvalidValueException
	 *             if trying to set the mode None to false
	 */
	void setAchievableServiceOfferMode(String serviceName, ServiceOfferModeEnum mode, boolean active);

	void advertiseAchievableService(String serviceName, ServiceOfferModeEnum mode);

	/**
	 * Notifies the request dispatcher that this selflet is going to vacation!
	 * */
	void notifySelfletRemoval();

}