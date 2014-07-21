package it.polimi.elet.selflet.optimization.generators;

import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.knowledge.Neighbor;
import it.polimi.elet.selflet.negotiation.nodeState.INeighborStateManager;
import it.polimi.elet.selflet.negotiation.nodeState.INodeState;
import it.polimi.elet.selflet.optimization.actions.IOptimizationAction;
import it.polimi.elet.selflet.optimization.actions.teach.TeachServiceAction;
import it.polimi.elet.selflet.service.Service;
import it.polimi.elet.selflet.service.utilization.IPerformanceMonitor;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * A class generating teach actions
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class TeachActionGenerator implements IActionGenerator {

	private final INeighborStateManager neighborStateManager;
	private final IServiceKnowledge serviceKnowledge;
	private final IPerformanceMonitor performanceMonitor;

	public TeachActionGenerator(INeighborStateManager neighborStateManager, IServiceKnowledge serviceKnowledge, IPerformanceMonitor performanceMonitor) {
		this.neighborStateManager = neighborStateManager;
		this.serviceKnowledge = serviceKnowledge;
		this.performanceMonitor = performanceMonitor;
	}

	@Override
	public Collection<? extends IOptimizationAction> generateActions() {
		List<IOptimizationAction> actions = Lists.newArrayList();

		if (!performanceMonitor.isCPUUtilizationOverTheThreshold()) {
			return actions;
		}

		for (Neighbor neighbor : neighborStateManager.getNeighbors()) {
			List<IOptimizationAction> actionsForNeighbor = generateTeachActionsForNeighbor(neighbor);
			actions.addAll(actionsForNeighbor);
		}

		return actions;
	}

	private List<IOptimizationAction> generateTeachActionsForNeighbor(Neighbor neighbor) {
		List<IOptimizationAction> actions = Lists.newArrayList();

		for (Service service : serviceKnowledge.getServices()) {

			if (!neighborIsOfferingService(neighbor, service) && !neighborIsOverloaded(neighbor)) {
				double weight = performanceMonitor.getServiceUtilization(service.getName());
				actions.add(new TeachServiceAction(neighbor.getId(), service, weight));
			}
		}

		return actions;
	}

	private boolean neighborCanDirectlyExecuteTheService(Neighbor neighbor, Service service) {
		if (service.isImplementedByElementaryBehavior()) {
			return true;
		}

		List<String> invokedServices = service.getInvokedServices();
		for (String invokedServiceName : invokedServices) {
			Service invokedService = serviceKnowledge.getProperty(invokedServiceName);
			if (!neighborIsOfferingService(neighbor, invokedService)) {
				return false;
			}
		}
		return true;
	}

	private boolean neighborIsOverloaded(Neighbor neighbor) {
		if (!neighborStateManager.haveInformationAboutNeighbor(neighbor)) {
			return false;
		}
		INodeState nodeState = neighborStateManager.getNodeStateOfNeighbor(neighbor.getId());
		double utilization = nodeState.getUtilization();
		return utilization > performanceMonitor.getCPUUtilizationUpperBound();
	}

	private boolean neighborIsOfferingService(Neighbor neighbor, Service service) {
		return neighborStateManager.isNeighborOfferingService(neighbor, service);
	}

}
