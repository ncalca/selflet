package it.polimi.elet.selflet.optimization.generators;

import static it.polimi.elet.selflet.negotiation.nodeState.NodeStateGenericDataEnum.CPU_UTILIZATION;
import it.polimi.elet.selflet.autonomic.IAutonomicActuator;
import it.polimi.elet.selflet.configuration.SelfletConfiguration;
import it.polimi.elet.selflet.knowledge.Neighbor;
import it.polimi.elet.selflet.negotiation.nodeState.INeighborStateManager;
import it.polimi.elet.selflet.negotiation.nodeState.INodeState;
import it.polimi.elet.selflet.optimization.actions.IOptimizationAction;
import it.polimi.elet.selflet.optimization.actions.scaling.AddSelfletAction;
import it.polimi.elet.selflet.service.utilization.IPerformanceMonitor;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * Action to istantiate a new selflet
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class IstantiateNewSelfletActionGenerator implements IActionGenerator {

	private final INeighborStateManager neighborStateManager;
	private final IPerformanceMonitor performanceMonitor;
	private final IAutonomicActuator autonomicAttuator;

	private final static int MINIMUM_TIME_BETWEEN_ISTANTIATIONS = SelfletConfiguration.getSingleton().minimumTimeBetweenNewSelfletActionsInSec * 1000;

	@Inject
	public IstantiateNewSelfletActionGenerator(INeighborStateManager neighborStateManager, IPerformanceMonitor performanceMonitor,
			IAutonomicActuator autonomicAttuator) {
		this.neighborStateManager = neighborStateManager;
		this.performanceMonitor = performanceMonitor;
		this.autonomicAttuator = autonomicAttuator;
	}

	@Override
	public Collection<? extends IOptimizationAction> generateActions() {

		if (recentlyIstantiatedNewSelflet()) {
			return Lists.newArrayList();
		}

		if (!selfletIsOverloaded()) {
			return Lists.newArrayList();
		}

		if ((theOnlySelflet() && selfletIsOverloaded()) || neighborsAreLoaded()) {
			//TODO define the best policy to compute weight
//			double upperBound = performanceMonitor.getCPUUtilizationUpperBound();
//			double weight = Math.max((computeUtilizationAverage() - upperBound) / (1 - upperBound), 0);
			double weight = computeUtilizationAverage() - performanceMonitor.getCPUUtilizationUpperBound();
			return Lists.newArrayList(new AddSelfletAction(weight));
		}

		return Lists.newArrayList();
	}
	
	private boolean selfletIsOverloaded(){
		double currentUtilization = performanceMonitor.getCurrentTotalCPUUtilization();
		double upperBound = performanceMonitor.getCPUUtilizationUpperBound();
		
		return currentUtilization > upperBound;
	}

	private boolean neighborsAreLoaded() {

		Set<Neighbor> neighbors = neighborStateManager.getNeighbors();

		if (neighbors.isEmpty()) {
			return false;
		}

		return (computeUtilizationAverage() >= performanceMonitor.getCPUUtilizationUpperBound());
	}

	private double computeUtilizationAverage() {
		Set<Neighbor> neighbors = neighborStateManager.getNeighbors();
		double utilizationSum = 0;
		int count = 0;

		for (Neighbor neighbor : neighbors) {
			if (neighborStateManager.haveInformationAboutNeighbor(neighbor)) {
				INodeState nodeState = neighborStateManager.getNodeStateOfNeighbor(neighbor);
				double neighborUtilization = (Double) nodeState.getGenericDataWithKey(CPU_UTILIZATION.toString());
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

	private boolean theOnlySelflet() {
		return neighborStateManager.getNeighbors().isEmpty();
	}

	private boolean recentlyIstantiatedNewSelflet() {
		long lastTimeActivatedAction = autonomicAttuator.getLastTimeIstantiatedSelflet();
		long now = System.currentTimeMillis();
		long elapsed = (now - lastTimeActivatedAction);
		return elapsed < MINIMUM_TIME_BETWEEN_ISTANTIATIONS;
	}

}
