package it.polimi.elet.selflet.action;

import it.polimi.elet.selflet.events.ISelfletComponent;
import it.polimi.elet.selflet.exceptions.ActionException;

import java.util.List;

/**
 * This action contains the API that can be called within the action code
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * @author Davide Devescovi
 */
public interface IActionAPI extends ISelfletComponent {

	/**
	 * Executes an ability.
	 * 
	 * @param abilityName
	 *            the ability name
	 * @param params
	 *            a List of parameters
	 * 
	 * @return the result of the ability, or null if no results would be
	 *         returned
	 * 
	 * @throws ActionException
	 *             if an error occurs
	 */
	Object executeAbility(String abilityName, List<Object[]> params);

	/**
	 * Generate a service request event <code>ServiceNeededEvent</code> for a
	 * given service and put the current running service in wait state.
	 * 
	 * @param serviceName
	 *            the name of the requested service.
	 * @return the value produced by the service.
	 * */
	Object needService(String serviceName);

}