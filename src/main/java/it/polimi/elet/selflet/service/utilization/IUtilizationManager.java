package it.polimi.elet.selflet.service.utilization;

import it.polimi.elet.selflet.service.Service;

/**
 * Interface to access the system to compute and monitor CPU utilization
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IUtilizationManager {

	/**
	 * Returns the current CPU utilization as a number in [0,1]
	 * */
	double getCurrentTotalCPUUtilization();

	/**
	 * Update the utilization history
	 */
	public void updateUtilizationHistory();
	
	/**
	 * Returns the predicted CPU utilization as a number in [0,1]
	 * */
	double getPredictedTotalCPUUtilization();

	/**
	 * Returns the CPU utilization as a number in [0,1] for the given service
	 * */
	double getCurrentServiceUtilization(Service service);

	/**
	 * Returns the utilization threshold lower bound
	 * */
	double getUtilizationLowerBound();

	/**
	 * Returns the utilization threshold upper bound
	 * */
	double getUtilizationUpperBound();

	/**
	 * Returns true iff the current utilization is outside the predefined bounds
	 * */
	boolean isCPUUtilizationOutOfRange(double utilization);

	/**
	 * True if the CPU is over the threshold
	 * */
	boolean isCPUUtilizationOverTheThreshold();

}
