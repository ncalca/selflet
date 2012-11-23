package it.polimi.elet.selflet.service;

import java.util.Set;

import it.polimi.elet.selflet.events.ISelfletComponent;
import it.polimi.elet.selflet.optimization.actions.redirect.RedirectPolicy;

/**
 * Interface for service execution used by the autonomic attuator
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IServiceExecutor extends ISelfletComponent {

	/**
	 * Executes the given service
	 * 
	 * @param service
	 *            the service
	 * @param outputDest
	 *            destination for output produced by this service execution
	 * @param callingService
	 *            the service that invoked the execution of this other service
	 * */
	void executeService(Service service, String outputDest, RunningService callingService);

	/**
	 * Install the given redirect policies in service executor
	 * */
	void setRedirectPolicies(Set<RedirectPolicy> redirectPolicy);

}
