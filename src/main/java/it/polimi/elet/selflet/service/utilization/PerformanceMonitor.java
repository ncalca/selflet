package it.polimi.elet.selflet.service.utilization;

import static it.polimi.elet.selflet.events.EventTypeEnum.LOCAL_REQUEST_LOCAL_EXECUTION_COMPLETED;
import static it.polimi.elet.selflet.events.EventTypeEnum.LOCAL_REQUEST_LOCAL_EXECUTION_EXECUTE;
import static it.polimi.elet.selflet.events.EventTypeEnum.LOCAL_REQUEST_REMOTE_EXECUTION_COMPLETED;
import static it.polimi.elet.selflet.events.EventTypeEnum.LOCAL_REQUEST_REMOTE_EXECUTION_EXECUTE;
import static it.polimi.elet.selflet.events.EventTypeEnum.REMOTE_REQUEST_LOCAL_EXECUTION_COMPLETED;
import static it.polimi.elet.selflet.events.EventTypeEnum.REMOTE_REQUEST_LOCAL_EXECUTION_EXECUTE;
import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.ISelfletEvent;
import it.polimi.elet.selflet.events.SelfletComponent;
import it.polimi.elet.selflet.events.service.ILocalExecutedServiceEvent;
import it.polimi.elet.selflet.events.service.IServiceEvent;
import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.service.Service;
import it.polimi.elet.selflet.service.utilization.ServiceRateMonitor.ServiceRate;

import java.util.List;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * A class implementing the performance monitor.
 * 
 * It is implemented as a facade for other subsystems
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class PerformanceMonitor extends SelfletComponent implements IPerformanceMonitor {

	private final IServiceExecutionTimesMonitor executionMonitor;
	private final IServiceRateMonitor serviceRateMonitor;
	private final IUtilizationManager utilizationManager;
	private final IServiceKnowledge serviceKnowledge;

	@Inject
	public PerformanceMonitor(IServiceKnowledge serviceKnowledge) {
		this.serviceKnowledge = serviceKnowledge;
		this.executionMonitor = new ServiceExecutionTimesMonitor();
		this.serviceRateMonitor = new ServiceRateMonitor();
		this.utilizationManager = new UtilizationManager(this, serviceKnowledge);
	}

	public void eventReceived(ISelfletEvent event) {

		if (!(event instanceof IServiceEvent)) {
			return;
		}

		IServiceEvent serviceEvent = (IServiceEvent) event;
		serviceRateMonitor.sendEvent(serviceEvent);

		if (serviceEvent.isLocalExecutedService() && (event instanceof ILocalExecutedServiceEvent)) {
			ILocalExecutedServiceEvent localExecutedEvent = (ILocalExecutedServiceEvent) event;
			executionMonitor.sendEvent(localExecutedEvent);
		}
	}

	public double getServiceRequestRate(String serviceName) {
		List<ServiceRate> requestRates = getRequestRates();
		for (ServiceRate serviceRequestRate : requestRates) {
			if (serviceRequestRate.getServiceName().equals(serviceName)) {
				return serviceRequestRate.getRequestRate();
			}
		}
		return 0;
	}

	public double getServiceThroughput(String serviceName) {
		List<ServiceRate> completionRates = getThroughput();
		for (ServiceRate serviceRequestRate : completionRates) {
			if (serviceRequestRate.getServiceName().equals(serviceName)) {
				return serviceRequestRate.getRequestRate();
			}
		}
		return 0;
	}

	public double getCurrentTotalCPUUtilization() {
		return utilizationManager.getCurrentTotalCPUUtilization();
	}

	public double getPredictedTotalCPUUtilization() {
		return utilizationManager.getPredictedTotalCPUUtilization();
	}

	public double getServiceUtilization(String serviceName) {
		Service service;
		try {
			service = serviceKnowledge.getProperty(serviceName);
		} catch (NotFoundException e) {
			return 0;
		}
		return utilizationManager.getCurrentServiceUtilization(service);
	}

	public List<ServiceRate> getRequestRates() {
		return serviceRateMonitor.getRequestRates();
	}

	public List<ServiceRate> getThroughput() {
		return serviceRateMonitor.getThroughput();
	}

	public double getServicePredictedCPUTimeInSec(Service service) throws NotFoundException {
		return executionMonitor.getEstimatedServiceCPUTimeInSec(service);
	}

	public boolean isCPUUtilizationOutOfRange(double utilization) {
		return utilizationManager.isCPUUtilizationOutOfRange(utilization);
	}

	public boolean isCPUUtilizationOutOfRange() {
		double currentUtilization = getCurrentTotalCPUUtilization();
		return utilizationManager.isCPUUtilizationOutOfRange(currentUtilization);
	}

	public double getCPUUtilizationLowerBound() {
		return utilizationManager.getUtilizationLowerBound();
	}

	public double getCPUUtilizationUpperBound() {
		return utilizationManager.getUtilizationUpperBound();
	}

	public long getServiceResponseTimeInMsec(String serviceName) {
		return executionMonitor.getResponseTimeInMsec(serviceName);
	}

	@Override
	public boolean isCPUUtilizationOverTheThreshold() {
		return utilizationManager.isCPUUtilizationOverTheThreshold();
	}

	@Override
	public ImmutableSet<EventTypeEnum> getReceivedEvents() {
		return ImmutableSet.of(LOCAL_REQUEST_LOCAL_EXECUTION_COMPLETED, LOCAL_REQUEST_REMOTE_EXECUTION_COMPLETED, REMOTE_REQUEST_LOCAL_EXECUTION_COMPLETED,
				LOCAL_REQUEST_LOCAL_EXECUTION_EXECUTE, REMOTE_REQUEST_LOCAL_EXECUTION_EXECUTE, LOCAL_REQUEST_REMOTE_EXECUTION_EXECUTE);
	}


}
