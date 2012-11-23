package it.polimi.elet.selflet.optimization;

import it.polimi.elet.selflet.threadUtilities.IPeriodicTask;

/**
 * Subsystem for the optimization actions
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IOptimizationManager extends IPeriodicTask {

	/**
	 * Performs the optimization on the selflet (i.e. generates all possible
	 * optimization actions, choose the best one and actuates it)
	 * */
	void performOptimization();

}
