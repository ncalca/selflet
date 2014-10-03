package it.polimi.elet.selflet.optimization.generators;

import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.knowledge.Neighbor;
import it.polimi.elet.selflet.negotiation.nodeState.INeighborStateManager;
import it.polimi.elet.selflet.negotiation.nodeState.INodeState;
import it.polimi.elet.selflet.optimization.actions.IOptimizationAction;
import it.polimi.elet.selflet.optimization.actions.redirect.RedirectAction;
import it.polimi.elet.selflet.optimization.actions.redirect.RedirectPolicy;
import it.polimi.elet.selflet.service.Service;
import it.polimi.elet.selflet.service.utilization.IPerformanceMonitor;
import it.polimi.elet.selflet.service.utilization.IRedirectMonitor;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

/**
 * An implementation of action generator for redirect actions
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class RedirectServiceActionGenerator implements IActionGenerator {

	private static final Logger LOG = Logger
			.getLogger(RedirectServiceActionGenerator.class);

	private final INeighborStateManager neighborStateManager;
	private final IPerformanceMonitor performanceMonitor;
	private final IServiceKnowledge serviceKnowledge;
	private final IRedirectMonitor redirectMonitor;

	public RedirectServiceActionGenerator(
			INeighborStateManager neighborStateManager,
			IPerformanceMonitor performanceMonitor,
			IServiceKnowledge serviceKnowledge, IRedirectMonitor redirectMonitor) {
		this.neighborStateManager = neighborStateManager;
		this.performanceMonitor = performanceMonitor;
		this.serviceKnowledge = serviceKnowledge;
		this.redirectMonitor = redirectMonitor;
	}

	@Override
	public Collection<? extends IOptimizationAction> generateActions() {
		Set<IOptimizationAction> optimizationActions = Sets.newHashSet();

		Set<Service> overloadedServices = getOverloadedServices();
		double totalServicesUtilization = computeServicesTotalUtilization(overloadedServices);
		for (Service service : overloadedServices) {
			optimizationActions.addAll(generateRedirectsForService(service, totalServicesUtilization));
		}

		return optimizationActions;
	}

	private Set<IOptimizationAction> generateRedirectsForService(Service service, double totalServicesUtilization) {
		Set<Neighbor> neighbors = neighborStateManager.getNeighbors();
		Set<IOptimizationAction> optimizationActions = Sets.newHashSet();

		// FIXME Why???????????
		 if (service.isRecentlyCreated()) {
		 return optimizationActions;
		 }

		for (Neighbor neighbor : neighbors) {
			if (neighborIsOfferingService(neighbor, service)
					&& neighborNotRedirectingToMe(neighbor, service)
					&& !neighborIsExceedingThreshold(neighbor, service)) {
				LOG.info("creating redirect action for service "
						+ service.getName() + " to neighbor "
						+ neighbor.getId().getID());
				IOptimizationAction redirectAction = createRedirectAction(
						service, neighbor, totalServicesUtilization);
				optimizationActions.add(redirectAction);
			}
		}

		return optimizationActions;
	}

	private boolean neighborIsExceedingThreshold(Neighbor neighbor,
			Service service) {
		INodeState nodeState = neighborStateManager
				.getNodeStateOfNeighbor(neighbor);
		if (nodeState == null) {
			return true;
		}

		long responseTime;
		double utilization;

		try {
			utilization = nodeState.getUtilization();
			responseTime = nodeState
					.getResponseTimeOfService(service.getName());
		} catch (Exception e) {
			LOG.info("exception in neighborIsExceedingThreshold");
			return true;
		}

		return (responseTime > service.getMaxResponseTimeInMsec())
				|| utilization >= performanceMonitor
						.getCPUUtilizationUpperBound();
	}

	private boolean neighborNotRedirectingToMe(Neighbor neighbor,
			Service service) {
		return !(redirectMonitor.isNeighborPerformingRedirectToMe(
				neighbor.getId(), service.getName()));
	}

	private IOptimizationAction createRedirectAction(Service service,
			Neighbor neighbor, double totalServicesUtilization) {
		// For now creating simple redirect actions with a single redirect
		// policy
		double redirectProbability = computeRedirectProbabilityTowardNeighbor(
				service, neighbor, totalServicesUtilization);
		RedirectPolicy redirectPolicy = new RedirectPolicy(service.getName(),
				neighbor.getId(), redirectProbability);
		double weight = computeActionWeight(service, neighbor);
		return new RedirectAction(Sets.newHashSet(redirectPolicy),
				weight);
	}

	private double computeRedirectProbabilityTowardNeighbor(Service service,
			Neighbor neighbor, double totalServicesUtilization) {
		double serviceUtilization = performanceMonitor
				.getServiceUtilization(service.getName());
		INodeState nodeState = neighborStateManager
				.getNodeStateOfNeighbor(neighbor);
		double neighborUtilization = nodeState.getUtilization();
		
		double probability = Math.max((serviceUtilization
				/ totalServicesUtilization) * (1 - neighborUtilization), 0.5);
		return probability;
	}
	
	private double computeServicesTotalUtilization(Set<Service> services) {
		double total = 0;
		for (Service service : services) {
			total += performanceMonitor
					.getServiceUtilization(service.getName());
		}

		return total;
	}
	
	private double computeActionWeight(Service service,  Neighbor neighbor){
		double serviceResponseTime = performanceMonitor
				.getServiceResponseTimeInMsec(service.getName());
		double serviceMaxResponseTime = service.getMaxResponseTimeInMsec();
		INodeState nodeState = neighborStateManager
				.getNodeStateOfNeighbor(neighbor);
		double neighborUtilization = nodeState.getUtilization();
		double weight = Math.max(
				Math.min(serviceResponseTime - serviceMaxResponseTime,
						serviceMaxResponseTime) / (serviceMaxResponseTime), 0)
				* (1 - neighborUtilization);
		return weight;
	}

	private boolean neighborIsOfferingService(Neighbor neighbor, Service service) {
		return neighborStateManager
				.isNeighborOfferingService(neighbor, service);
	}

	private Set<Service> getOverloadedServices() {

		Set<Service> overloadedServices = Sets.newHashSet();

		for (Entry<String, Service> entry : serviceKnowledge.getProperties()
				.entrySet()) {
			Service service = entry.getValue();
			if (!service.isLocallyAvailable()) {
				continue;
			}

			if (isServiceExceedingResponseTime(service)) {
				overloadedServices.add(service);
			}
		}

		return overloadedServices;
	}

	private boolean isServiceExceedingResponseTime(Service service) {
		double actualResponseTime = performanceMonitor
				.getServiceResponseTimeInMsec(service.getName());
		return (actualResponseTime > service.getMaxResponseTimeInMsec());
	}
}
