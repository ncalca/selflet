package it.polimi.elet.selflet.autonomic;

import it.polimi.elet.selflet.events.ISelfletComponent;
import it.polimi.elet.selflet.events.service.ServiceNeededEvent;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.negotiation.ServiceAskModeEnum;
import it.polimi.elet.selflet.negotiation.ServiceOfferModeEnum;
import it.polimi.elet.selflet.service.RunningService;

/**
 * This interface exposes services to the rules added to the selflet. This way
 * the rules can be expressed at a higher level ignoring selflet implementative
 * issues
 * 
 * @author silvia
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 */
public interface IAutonomicActuator extends ISelfletComponent {

	/**
	 * Executes a service trying every possible way: local, do, download
	 * 
	 * @param service
	 *            whose execution is required
	 * @param dest
	 *            where to put the result of the service's execution
	 * @param callingBehavior
	 *            the behavior from where the request for the execution of the
	 *            service arose
	 */
	void executeService(String service, String dest, RunningService callingService);

	/**
	 * Redirects the service request to the best provider that is offering it
	 * */
	void redirectRequest(ServiceNeededEvent serviceNeededEvent, RunningService callingService);

	/**
	 * Redirects the service request to a specific service provider
	 * */
	void redirectRequestToProvider(ServiceNeededEvent serviceNeededEvent, ISelfLetID provider, RunningService callingService);

	/**
	 * Redirects the service request to a specific service provider
	 * 
	 * @param callingService
	 * */
	void redirectRequestToProvider(String serviceName, ISelfLetID provider, RunningService callingService);

	/**
	 * Teaches the given service to the given service provider
	 * */
	void teachServiceToProvider(String serviceName, ISelfLetID provider);

	/**
	 * Communicates to the dispatcher the need for a new selflet and returns the
	 * id of the new selflet
	 * */
	void istantiateNewSelflet();

	/**
	 * allows to modify how a service is achieved, and sends notification to
	 * neighbors
	 * 
	 * @param service
	 * @param mode
	 * @param enable
	 */
	void modifyServiceOfferMode(String service, ServiceOfferModeEnum mode, boolean enable);

	/**
	 * allows to modify how a service is achieved, and sends notification to
	 * neighbors
	 * 
	 * @param service
	 * @param mode
	 */
	void modifyServiceAskMode(String service, ServiceAskModeEnum mode);

	/**
	 * this methods allows to add both a behavior for an existing service, a new
	 * behavior implementing a new service or a new known service without any
	 * implementing behavior
	 * 
	 * @param fileName
	 * @param service
	 */
	void addBehaviorAndService(String fileName, String service);

	/**
	 * removes given behavior from those known by the selflet
	 * 
	 * @param behavior
	 */
	void removeBehavior(String behavior);

	/**
	 * installs a new ability
	 * */
	void installAbility(String inputFileName, String abilityName, String serviceName, String methodName);

	/**
	 * restarts default behavior
	 */
	void restartDefault();

	/**
	 * disables a service, it will be no longer "offerable" nor available to the
	 * SelfLet
	 */
	void disableService(String service);

	/**
	 * re-enables a service previously disabled
	 * 
	 * @param service
	 */
	void enableService(String service);

	/**
	 * Kills this selflet
	 * */
	void removeSelflet();

	long getLastTimeIstantiatedSelflet();
	
	public void changeServiceImplementation(String serviceName,
			int qualityOfBehavior);

}
