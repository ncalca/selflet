package it.polimi.elet.selflet.optimization.generators;

import static it.polimi.elet.selflet.negotiation.nodeState.NodeStateGenericDataEnum.CPU_UTILIZATION;
import it.polimi.elet.selflet.behavior.IBehavior;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.knowledge.Neighbor;
import it.polimi.elet.selflet.negotiation.nodeState.INeighborStateManager;
import it.polimi.elet.selflet.negotiation.nodeState.INodeState;
import it.polimi.elet.selflet.optimization.actions.IOptimizationAction;
import it.polimi.elet.selflet.optimization.actions.changeImplementation.ChangeServiceImplementationAction;
import it.polimi.elet.selflet.service.Service;
import it.polimi.elet.selflet.service.utilization.IPerformanceMonitor;

import java.util.Collection;
import java.util.Set;
import java.util.Map.Entry;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * A class generating change service implementation actions
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ChangeServiceImplementationGenerator implements IActionGenerator {

	private final INeighborStateManager neighborStateManager;
	private final IPerformanceMonitor performanceMonitor;
	private final IServiceKnowledge serviceKnowledge;

	@Inject
	public ChangeServiceImplementationGenerator(
			INeighborStateManager neighborStateManager,
			IPerformanceMonitor performanceMonitor,
			IServiceKnowledge serviceKnowledge) {
		this.neighborStateManager = neighborStateManager;
		this.performanceMonitor = performanceMonitor;
		this.serviceKnowledge = serviceKnowledge;
	}

	@Override
	public Collection<? extends IOptimizationAction> generateActions() {
		Set<IOptimizationAction> optimizationActions = Sets.newHashSet();
		Set<Service> services = serviceKnowledge.getServices();
		Set<Service> overloadedServices = getOverloadedServices();

		boolean selfletOverloaded = selfletIsOverloaded();
		boolean neighnorsOveloaded = neighborsAreLoaded();

		if (selfletOverloaded && neighnorsOveloaded) {
			for (Service overloadedService : getOverloadedServices()) {
				if (switchToLowQualityBehaviour(overloadedService)) {
					double weight = performanceMonitor
							.getServiceUtilization(overloadedService.getName());
					optimizationActions
							.add(new ChangeServiceImplementationAction(
									overloadedService, 1, weight));
				}
			}
		} else if (!selfletOverloaded && !neighnorsOveloaded) {
			services.removeAll(overloadedServices);
			for (Service lowLoadedService : services) {
				if (switchToHighQualityBehaviour(lowLoadedService)) {
					double weight = performanceMonitor
							.getServiceUtilization(lowLoadedService.getName());
					optimizationActions
							.add(new ChangeServiceImplementationAction(
									lowLoadedService, 2, weight));
				}
			}
		}

		return optimizationActions;

	}

	private boolean switchToLowQualityBehaviour(Service service) {
		Set<IBehavior> implementedBehaviors = service
				.getImplementingBehaviors();
		if (implementedBehaviors.size() < 2) {
			return false;
		}

		int currentQuality = getCurrentBehaviorQuality(service
				.getDefaultBehavior());
		if (currentQuality == 1)
			return false;

		return true;
	}

	private boolean switchToHighQualityBehaviour(Service service) {
		Set<IBehavior> implementedBehaviors = service
				.getImplementingBehaviors();
		if (implementedBehaviors.size() < 2) {
			return false;
		}

		int currentQuality = getCurrentBehaviorQuality(service
				.getDefaultBehavior());
		if (currentQuality == 2)
			return false;

		return true;
	}

	private boolean selfletIsOverloaded() {
		double currentUtilization = performanceMonitor
				.getCurrentTotalCPUUtilization();
		double upperBound = performanceMonitor.getCPUUtilizationUpperBound();

		return (currentUtilization > upperBound);
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

	private boolean neighborsAreLoaded() {

		Set<Neighbor> neighbors = neighborStateManager.getNeighbors();

		if (neighbors.isEmpty()) {
			return false;
		}

		return (computeUtilizationAverage() >= performanceMonitor
				.getCPUUtilizationUpperBound());
	}

	private double computeUtilizationAverage() {
		Set<Neighbor> neighbors = neighborStateManager.getNeighbors();
		double utilizationSum = 0;
		int count = 0;

		for (Neighbor neighbor : neighbors) {
			if (neighborStateManager.haveInformationAboutNeighbor(neighbor)) {
				INodeState nodeState = neighborStateManager
						.getNodeStateOfNeighbor(neighbor);
				double neighborUtilization = (Double) nodeState
						.getGenericDataWithKey(CPU_UTILIZATION.toString());
				utilizationSum += neighborUtilization;
				count++;
			}
		}

		if (count == 0) {
			return 0;
		}

		double utilizationAverage = utilizationSum / count;
		return utilizationAverage;
	}

	private int getCurrentBehaviorQuality(IBehavior behavior) {
		String name = behavior.getName();
		int quality = Integer.parseInt(name.substring(name.length() - 1));

		return quality;
	}

}
