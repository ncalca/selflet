package it.polimi.elet.selflet.lifecycle;

import it.polimi.elet.selflet.threadUtilities.PeriodicThreadStarter;

/**
 * Component in charge of shutting down the entire selflet
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface ISelfletShutdown {

	/**
	 * Shuts down the current selflet
	 * */
	void shutDown();

	void addPeriodicThreadStarter(PeriodicThreadStarter periodicThreadsStarter);

}
