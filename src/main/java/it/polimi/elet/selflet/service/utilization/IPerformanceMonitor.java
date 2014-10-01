package it.polimi.elet.selflet.service.utilization;

import it.polimi.elet.selflet.events.ISelfletComponent;
import it.polimi.elet.selflet.service.Service;

/**
 * The performance monitor is subscribed to all events launched by the service
 * manager. The performance monitor has two main jobs:
 * 
 * 1) To monitor request rates for all services (either locally and remotely).
 * It differentiates a service requests and an effective service completion.
 * 
 * 2) To monitor the service execution time for each locally offered service
 * 
 * Performance monitor maintain an historical series of data by using a sliding
 * window technique.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IPerformanceMonitor extends ISelfletComponent {

	/**
	 * Get the total node utilization
	 * 
	 * @return a double representing the total utilization
	 */
	double getCurrentTotalCPUUtilization();

	/**
	 * Update the total utilization history
	 */
	public void updateUtilizationHistory();
	
	/**
	 * Get the predicted total node utilization. The value is obtained by
	 * monitoring past values of utilization and applying a prediction
	 * technique.
	 * 
	 * @return a double representing the total utilization
	 */
	double getPredictedTotalCPUUtilization();

	/**
	 * Returns the utilization for the given service
	 * 
	 * @param serviceName
	 * @return a double representing the service utilization
	 */
	double getServiceUtilization(String serviceName);

	/**
	 * Returns the request rate for the given service. The request rate takes
	 * into account the requests received from other nodes.
	 * 
	 * @param serviceName
	 * @return a double representing the request rate (requests per second).
	 */
	double getServiceRequestRate(String serviceName);

	/**
	 * Returns the completion rate for the given service. The completion rate
	 * measure the effective rate at which requests are satisfied. It depends on
	 * the specific implementation of the service.
	 * 
	 * @param serviceName
	 * @return a double representing the completion rate (requests per second).
	 */
	double getServiceThroughput(String serviceName);

	/**
	 * Returns the response time for the given service. The response time is
	 * obtained by monitoring past request response time. The value is obtained
	 * by doing the difference of the time instants between the service request
	 * is received and completed.
	 * 
	 * @param serviceName
	 * @return a long representing the response time in milliseconds
	 */
	long getServiceResponseTimeInMsec(String serviceName);

	/**
	 * Get the predicted CPU time, in seconds, for a given service. There can be
	 * three cases:
	 * 
	 * 1) If the service is remote the service CPU time is a constant.
	 * 
	 * 2) If the service is already monitored then the last value is given back
	 * 
	 * 3) If there are no monitor information about the service and the service
	 * is currently implemented by an elementary behavior then we can take the
	 * declared service time.
	 * 
	 * @return a double number representing the number of seconds taken by the
	 *         service to perform the request
	 * */
	double getServicePredictedCPUTimeInSec(Service service);

	/**
	 * Returns the CPU utilization upper bound
	 * */
	double getCPUUtilizationUpperBound();

	/**
	 * Returns the CPU utilization lower bound
	 * */
	double getCPUUtilizationLowerBound();

	/**
	 * Returns true iff the given cpu utilization is out of the predefined range
	 * */
	boolean isCPUUtilizationOutOfRange(double utilization);

	/**
	 * Returns true iff the current cpu utilization is out of the predefined
	 * range
	 * */
	boolean isCPUUtilizationOutOfRange();

	/**
	 * True if the utilization is over the upper bound threshold
	 * */
	boolean isCPUUtilizationOverTheThreshold();

}
