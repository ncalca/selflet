package it.polimi.elet.selflet.events.service;

import it.polimi.elet.selflet.service.RunningService;

/**
 * Interface used by events that signal a service completion in the local
 * selflet.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 **/
public interface ILocalExecutedServiceEvent extends IServiceEvent {

	/**
	 * A reference to the running service
	 * */
	RunningService getRunningService();

	/**
	 * The execution time for this service
	 * */
	long getResponseTime();
}
