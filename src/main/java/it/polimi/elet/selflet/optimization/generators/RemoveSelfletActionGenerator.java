package it.polimi.elet.selflet.optimization.generators;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import it.polimi.elet.selflet.configuration.SelfletConfiguration;
import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.knowledge.Neighbor;
import it.polimi.elet.selflet.negotiation.nodeState.INeighborStateManager;
import it.polimi.elet.selflet.optimization.actions.IOptimizationAction;
import it.polimi.elet.selflet.optimization.actions.scaling.RemoveSelfletAction;
import it.polimi.elet.selflet.service.Service;
import it.polimi.elet.selflet.service.utilization.IPerformanceMonitor;

/**
 * Generators for actions removing the current selflet
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class RemoveSelfletActionGenerator implements IActionGenerator {

	private static final long MINIMUM_TIME_TO_REMOVE_SELFLET = SelfletConfiguration
			.getSingleton().minimumTimeToRemoveSelfletInSec * 1000;
	private static final long MINIMUM_TIME_BETWEEN_TWO_REMOVAL_ACTIONS = SelfletConfiguration
			.getSingleton().minimumTimeBetweenTwoRemovalActionsInSec * 1000;

	private final INeighborStateManager neighborStateManager;
	private final IPerformanceMonitor performanceMonitor;
	private final IServiceKnowledge serviceKnowledge;
	private final long startupTime;
	private long lastTimeCreatedARemoval;

	@Inject
	public RemoveSelfletActionGenerator(
			INeighborStateManager neighborStateManager,
			IPerformanceMonitor performanceMonitor,
			IServiceKnowledge serviceKnowledge) {
		this.neighborStateManager = neighborStateManager;
		this.performanceMonitor = performanceMonitor;
		this.serviceKnowledge = serviceKnowledge;
		this.startupTime = System.currentTimeMillis();
		this.lastTimeCreatedARemoval = System.currentTimeMillis();
	}

	@Override
	public Collection<? extends IOptimizationAction> generateActions() {

		// || stillReceivingRequests()
		if (selfletRecentlyCreated() || selfletIsLoaded() || theOnlySelflet()
				|| removalActionRecentlyCreated() || theOnlyOneProvidingSomeService()) {
			return Lists.newArrayList();
		}

		IOptimizationAction removalAction = createRemovalAction();
		return Lists.newArrayList(removalAction);
	}

	private IOptimizationAction createRemovalAction() {
		this.lastTimeCreatedARemoval = System.currentTimeMillis();
		double weight = computeWeight();
		return new RemoveSelfletAction(weight);
	}

	private double computeWeight() {
		double lowerBound = performanceMonitor.getCPUUtilizationLowerBound();
		double currentUtilization = performanceMonitor
				.getCurrentTotalCPUUtilization();
		return Math.max(lowerBound - currentUtilization, 0);
	}

	private boolean removalActionRecentlyCreated() {
		long now = System.currentTimeMillis();
		long elapsed = now - lastTimeCreatedARemoval;
		return (elapsed < MINIMUM_TIME_BETWEEN_TWO_REMOVAL_ACTIONS);
	}

	private boolean stillReceivingRequests() {
		for (Service service : serviceKnowledge.getServices()) {
			double requestRate = performanceMonitor
					.getServiceRequestRate(service.getName());
			if (requestRate > 0) {
				return true;
			}
		}
		return false;
	}

	private boolean selfletIsLoaded() {
		double currentUtilization = performanceMonitor
				.getCurrentTotalCPUUtilization();
		return (currentUtilization >= performanceMonitor
				.getCPUUtilizationLowerBound());
	}

	private boolean theOnlySelflet() {
		return neighborStateManager.getNeighbors().isEmpty();
	}

	private boolean theOnlyOneProvidingSomeService() {
		boolean theOnlyProvider = false;

		for (Service service : serviceKnowledge.getServices()) {
			try {
				neighborStateManager
						.getNeighborHavingService(service.getName());
			} catch (NotFoundException e) {
				theOnlyProvider = true;
			}
		}

		return theOnlyProvider;
	}

	private boolean selfletRecentlyCreated() {
		long now = System.currentTimeMillis();
		long elapsed = now - startupTime;
		return elapsed < MINIMUM_TIME_TO_REMOVE_SELFLET;
	}

}
