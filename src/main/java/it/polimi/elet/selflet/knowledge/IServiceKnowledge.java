package it.polimi.elet.selflet.knowledge;

import java.util.Set;

import it.polimi.elet.selflet.service.Service;

/**
 * Main interface for service knowledge
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 */
public interface IServiceKnowledge extends IKnowledge<Service> {

	/**
	 * Checks if the service is locally available
	 * 
	 * @return true if the service is local
	 * */
	boolean isLocalService(String serviceName);

	/**
	 * Returns the set of all services
	 * */
	Set<Service> getServices();

}
