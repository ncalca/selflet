package it.polimi.elet.selflet.service.utilization;

import it.polimi.elet.selflet.events.service.IServiceEvent;
import it.polimi.elet.selflet.service.utilization.ServiceRateMonitor.ServiceRate;

import java.util.List;

/**
 * Main interface to monitor service request and completion rate
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IServiceRateMonitor {

	/**
	 * Append the service event in the appropriate list
	 */
	void sendEvent(IServiceEvent event);

	/**
	 * Each time this method is invoked, request rates are computed (better than
	 * doing it every time an event is received)
	 * 
	 * @return a list of service rates
	 */
	List<ServiceRate> getRequestRates();

	/**
	 * It basically returns the throughput for all services
	 * */
	List<ServiceRate> getThroughput();

}