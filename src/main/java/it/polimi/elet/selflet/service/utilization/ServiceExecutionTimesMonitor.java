package it.polimi.elet.selflet.service.utilization;

import it.polimi.elet.selflet.events.service.ILocalExecutedServiceEvent;
import it.polimi.elet.selflet.service.RunningService;
import it.polimi.elet.selflet.service.Service;

import org.apache.log4j.Logger;

/**
 * Stores service execution times and response times for services
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ServiceExecutionTimesMonitor implements IServiceExecutionTimesMonitor {

	private static final Logger LOG = Logger.getLogger(ServiceExecutionTimesMonitor.class);

	private ResponseTimeMonitor responseTimeMonitor;

	public ServiceExecutionTimesMonitor() {
		responseTimeMonitor = new ResponseTimeMonitor();
	}

	@Override
	public void sendEvent(ILocalExecutedServiceEvent event) {

		RunningService runningService = event.getRunningService();

		if (runningService == null) {
			LOG.warn("Service executed and now have a null running service");
			return;
		}

		updateResponseTime(event);
	}

	private void updateResponseTime(ILocalExecutedServiceEvent event) {
		String serviceName = event.getServiceName();
		long responseTime = event.getResponseTime();
		responseTimeMonitor.updateResponseTime(serviceName, responseTime);
	}

	@Override
	public long getResponseTimeInMsec(String service) {
		return responseTimeMonitor.getResponseTime(service);
	}

	@Override
	public double getEstimatedServiceCPUTimeInSec(Service service) {
		return ((double) getResponseTimeInMsec(service.getName())) / 1000;
	}

}
