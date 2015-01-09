package it.polimi.elet.selflet.service;

import polimi.reds.MessageID;
import it.polimi.elet.selflet.events.ISelfletComponent;

/**
 * Interface for running service manager
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IRunningServiceManager extends ISelfletComponent {

	/**
	 * Execute the given service locally in the thread pool
	 * */
	void startService(RunningService runningService);

	/**
	 * Adds this running service to the list of services that are waiting for a
	 * remote reply
	 * */
	void addServiceWaitingForRemoteReply(RunningService callingService, MessageID messageID);

	/**
	 * Awakes the service that is waiting for an answer from a remote execution
	 * with the given messageID
	 * */
	void resumePendingService(MessageID messageID);

	/**
	 * Returns an object containing all service execution stats
	 * */
	ServiceExecutionStats getServiceExecutionStats();

	/**
	 * Removes old entries from the running service manager
	 * */
	void cleanOldRequests();
	
	/**
	 * Removes completed services
	 */
	public void cleanCompletedRequests();

}
