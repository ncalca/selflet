package it.polimi.elet.selflet.service.utilization;

import it.polimi.elet.selflet.events.service.ILocalExecutedServiceEvent;
import it.polimi.elet.selflet.service.Service;

/**
 * Interface to deal with the operations to update the service time and the
 * response time of services
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IServiceExecutionTimesMonitor {

	/**
	 * Updates response time for service execution
	 * */
	void sendEvent(ILocalExecutedServiceEvent event);

	/**
	 * Returns the response time for the given service (it includes the time in
	 * the queue)
	 * */
	long getResponseTimeInMsec(String serviceName);

	/**
	 * Returns the estimated service cpu time (time that it takes to execute the
	 * service once it is started)
	 * */
	double getEstimatedServiceCPUTimeInSec(Service service);

}