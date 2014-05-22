package it.polimi.elet.selflet.optimization.generators;

import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.knowledge.Neighbor;
import it.polimi.elet.selflet.negotiation.nodeState.INeighborStateManager;
import it.polimi.elet.selflet.negotiation.nodeState.INodeState;
import it.polimi.elet.selflet.negotiation.nodeState.NodeStateGenericDataEnum;
import it.polimi.elet.selflet.optimization.actions.IOptimizationAction;
import it.polimi.elet.selflet.optimization.actions.redirect.RedirectAction;
import it.polimi.elet.selflet.optimization.actions.redirect.RedirectPolicy;
import it.polimi.elet.selflet.service.Service;
import it.polimi.elet.selflet.service.utilization.IPerformanceMonitor;
import it.polimi.elet.selflet.service.utilization.IRedirectMonitor;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * An implementation of action generator for redirect actions
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class RedirectServiceActionGenerator implements IActionGenerator {

	private final INeighborStateManager neighborStateManager;
	private final IPerformanceMonitor performanceMonitor;
	private final IServiceKnowledge serviceKnowledge;
	private final IRedirectMonitor redirectMonitor;

	public RedirectServiceActionGenerator(INeighborStateManager neighborStateManager, IPerformanceMonitor performanceMonitor,
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
		for (Service service : overloadedServices) {
			optimizationActions.addAll(generateRedirectsForService(service));
		}

		return optimizationActions;
	}

	private Set<IOptimizationAction> generateRedirectsForService(Service service) {
		Set<Neighbor> neighbors = neighborStateManager.getNeighbors();
		Set<IOptimizationAction> optimizationActions = Sets.newHashSet();

		// FIXME Why???????????
//		if (service.isRecentlyCreated()) {
//			return optimizationActions;
//		}

		for (Neighbor neighbor : neighbors) {
			if (neighborIsOfferingService(neighbor, service) && neighborNotRedirectingToMe(neighbor, service)
					&& !neighborIsExceedingThreshold(neighbor, service)) {
				IOptimizationAction redirectAction = createRedirectAction(service, neighbor);
				optimizationActions.add(redirectAction);
			}
		}

		return optimizationActions;
	}

	private boolean neighborIsExceedingThreshold(Neighbor neighbor, Service service) {
		INodeState nodeState = neighborStateManager.getNodeStateOfNeighbor(neighbor);
		if (nodeState == null) {
			return true;
		}

		long responseTime;
		double utilization;

		try {
			Serializable data = nodeState.getGenericDataWithKey(NodeStateGenericDataEnum.RESPONSE_TIME.toString());
			if (data == null) {
				return true;
			}
			responseTime = (Long) data;
			data = nodeState.getGenericDataWithKey(NodeStateGenericDataEnum.CPU_UTILIZATION.toString());
			if (data == null) {
				return true;
			}

			utilization = (Double) data;
		} catch (NotFoundException e) {
			return true;
		}

		return (responseTime > service.getMaxResponseTimeInMsec()) || utilization >= performanceMonitor.getCPUUtilizationUpperBound();
	}

	private boolean neighborNotRedirectingToMe(Neighbor neighbor, Service service) {
		return !(redirectMonitor.isNeighborPerformingRedirectToMe(neighbor.getId(), service.getName()));
	}

	private IOptimizationAction createRedirectAction(Service service, Neighbor neighbor) {
		// For now creating simple redirect actions with a single redirect
		// policy
		double redirectProbability = computeRedirectProbabilityTowardNeighbor(service, neighbor);
		RedirectPolicy redirectPolicy = new RedirectPolicy(service.getName(), neighbor.getId(), redirectProbability);
		double serviceUtilization = performanceMonitor.getServiceUtilization(service.getName());
		return new RedirectAction(Sets.newHashSet(redirectPolicy),serviceUtilization);
	}

	private double computeRedirectProbabilityTowardNeighbor(Service service, Neighbor neighbor) {
		// TODO
		// the correct value should be computed considering the load of the
		// service
		return 0.5;
	}

	private boolean neighborIsOfferingService(Neighbor neighbor, Service service) {
		return neighborStateManager.isNeighborOfferingService(neighbor, service);
	}

	private Set<Service> getOverloadedServices() {

		Set<Service> overloadedServices = Sets.newHashSet();

		for (Entry<String, Service> entry : serviceKnowledge.getProperties().entrySet()) {
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
		double actualResponseTime = performanceMonitor.getServiceResponseTimeInMsec(service.getName());
		return (actualResponseTime > service.getMaxResponseTimeInMsec());
	}
}
