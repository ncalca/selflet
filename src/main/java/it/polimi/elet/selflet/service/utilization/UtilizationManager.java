package it.polimi.elet.selflet.service.utilization;

import it.polimi.elet.selflet.behavior.BehaviorUtilities;
import it.polimi.elet.selflet.behavior.IBehavior;
import it.polimi.elet.selflet.behavior.State;
import it.polimi.elet.selflet.configuration.SelfletConfiguration;
import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.service.Service;
import it.polimi.elet.selflet.utilities.CircularFifoQueue;
import it.polimi.elet.selflet.utilities.CollectionUtils;

/**
 * An implementation of utilization manager. Computes total CPU utilization and
 * service CPU utilization
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class UtilizationManager implements IUtilizationManager {

	private static final int HISTORY_LENGTH = 30;
	private static final double UTILIZATION_FOR_REMOTE_SERVICE = SelfletConfiguration
			.getSingleton().utilizationForRemoteService;
	private static final double UTILIZATION_LOWER_BOUND = SelfletConfiguration
			.getSingleton().utilizationLowerBound;
	private static final boolean USE_DYNAMIC_STRATEGY = SelfletConfiguration
			.getSingleton().useDynamicStrategy;

	private final CircularFifoQueue<Double> utilizationHistoryBuffer;
	private final IPerformanceMonitor performanceMonitor;
	private final IServiceKnowledge serviceKnowledge;

	// Strategy for the computation of the CPU upper bound
	private IUtilizationStrategy utilizationStrategy;

	public UtilizationManager(IPerformanceMonitor performanceMonitor,
			IServiceKnowledge serviceKnowledge) {
		this.performanceMonitor = performanceMonitor;
		this.serviceKnowledge = serviceKnowledge;
		this.utilizationHistoryBuffer = new CircularFifoQueue<Double>(
				HISTORY_LENGTH, Double.valueOf(0));
		if (USE_DYNAMIC_STRATEGY)
			this.utilizationStrategy = new UtilizationUpperBoundDynamic(
					serviceKnowledge);
		else
			this.utilizationStrategy = new UtilizationUpperBoundDefault();
	}

	/**
	 * Get the total node utilization. Utilization is computed as in formula
	 * (4.6) of book chapter.
	 */
	@Override
	public double getCurrentTotalCPUUtilization() {
		double totalNodeUtilization = computeTotalUtilization();
		insertInTotalUtilizationHistory(totalNodeUtilization);
		return CollectionUtils.computeAverage(utilizationHistoryBuffer);
	}

	/**
	 * Utilization of a service is computed as formula (4.5) of book chapter
	 * 
	 * @return a double representing the utilization of the given service
	 */
	@Override
	public double getCurrentServiceUtilization(Service service) {
		String serviceName = service.getName();
		double throughput = performanceMonitor
				.getServiceThroughput(serviceName);

		if (throughput <= 0) {
			return 0;
		}

		if (service.isRemote()) {
			return UTILIZATION_FOR_REMOTE_SERVICE;
		}

		if (service.isImplementedByElementaryBehavior()) {
			return computeElementaryUtilizationForService(service);
		}

		return computeUtilizationForComplexBehavior(service);
	}

	private double computeUtilizationForComplexBehavior(Service service) {
		double utilization = 0;
		IBehavior defaultBehavior = service.getDefaultBehavior();

		for (State state : defaultBehavior.getStates()) {

			if (state.isFinalState() || state.isInitialState()) {
				continue;
			}

			Service stateService;
			try {
				stateService = serviceKnowledge.getProperty(state.getName());
			} catch (NotFoundException e) {
				continue;
			}

			utilization += getCurrentServiceUtilization(stateService)
					* BehaviorUtilities.computeStateProbability(
							defaultBehavior, state);
		}
		return utilization;
	}

	private double computeElementaryUtilizationForService(Service service) {
		// computed according to the formula CPUTime x throughput

		double throughput = performanceMonitor.getServiceThroughput(service
				.getName());

		double responseTimeInMsec = performanceMonitor
				.getServiceResponseTimeInMsec(service.getName());
		double responseTimeInSec = responseTimeInMsec / 1000;
		return responseTimeInSec * throughput;
	}

	private double computeTotalUtilization() {
		double totalNodeUtilization = 0;
		for (Service service : serviceKnowledge.getServices()) {
			if (service.isImplementedByElementaryBehavior()) {
				totalNodeUtilization += getCurrentServiceUtilization(service);
			}
		}
		return Math.min(totalNodeUtilization, 1);
	}

	@Override
	public double getPredictedTotalCPUUtilization() {
		// TODO should compute the prediction based on utilizationHistory array
		return getCurrentTotalCPUUtilization();
	}

	@Override
	public double getUtilizationUpperBound() {
		return utilizationStrategy.computeUtilizationUpperBound();
	}

	@Override
	public double getUtilizationLowerBound() {
		return UTILIZATION_LOWER_BOUND;
	}

	public boolean isCPUUtilizationOutOfRange() {
		double currentCPUUTilization = getCurrentTotalCPUUtilization();
		return isCPUUtilizationOutOfRange(currentCPUUTilization);
	}

	@Override
	public boolean isCPUUtilizationOutOfRange(double utilization) {
		return (utilization < getUtilizationLowerBound() || utilization > getUtilizationUpperBound());
	}

	private void insertInTotalUtilizationHistory(double totalUtilization) {
		utilizationHistoryBuffer.add(Double.valueOf(totalUtilization));
	}

	@Override
	public boolean isCPUUtilizationOverTheThreshold() {
		return computeTotalUtilization() > getUtilizationUpperBound();
	}

}
