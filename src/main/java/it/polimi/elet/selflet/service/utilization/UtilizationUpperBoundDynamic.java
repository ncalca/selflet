package it.polimi.elet.selflet.service.utilization;

import it.polimi.elet.selflet.configuration.SelfletConfiguration;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.service.Service;

/**
 * An implementation of the strategy to compute the utilization upper bound of a
 * selflet. Use the formula: U_max = ( 1 - Dk / Rk_max).
 * 
 * @author lucaflorio
 * 
 */
public class UtilizationUpperBoundDynamic implements IUtilizationStrategy {

	private static final double UTILIZATION_LOWER_BOUND = SelfletConfiguration
			.getSingleton().utilizationLowerBound;
	private final double UTILIZATION_DELTA = 0.1;

	private IServiceKnowledge myServiceKnolegde;
	private IPerformanceMonitor myPerformanceMonitor;
	private double utilizationUpperBound;

	/**
	 * Create an instance of this strategy
	 * @param serviceKnoledge the service knowledge of the selflet
	 * @param performanceMonitor the performance monitor of the selflet
	 */
	public UtilizationUpperBoundDynamic(IServiceKnowledge serviceKnoledge,
			IPerformanceMonitor performanceMonitor) {
		this.myServiceKnolegde = serviceKnoledge;
		this.myPerformanceMonitor = performanceMonitor;
		this.utilizationUpperBound = 1;
	}

	@Override
	public double computeUtilizationUpperBound() {
		double serviceDemand = 0;
		double tempUtilization = 0;

		utilizationUpperBound = 1;
		for (Service service : myServiceKnolegde.getServices()) {
			try {

				serviceDemand = myPerformanceMonitor
						.getServicePredictedCPUTimeInSec(service) * 1000;
				
//				serviceDemand = service.getServiceDemand();

				tempUtilization = (1 - (serviceDemand / service
						.getMaxResponseTimeInMsec()));

				if (tempUtilization < utilizationUpperBound)
					utilizationUpperBound = tempUtilization;

				if (utilizationUpperBound <= UTILIZATION_LOWER_BOUND
						+ UTILIZATION_DELTA) {
					utilizationUpperBound = UTILIZATION_LOWER_BOUND
							+ UTILIZATION_DELTA;
					break;
				}
			} catch (Exception e) {
				// System.out.println("[UtilizationUpperBoundComputation] Error in cpu upper bound computation");
				// System.out.println("[UtilizationUpperBoundComputation] service: " + service.getName());
				// System.out.println("[UtilizationUpperBoundComputation]service demand: " + serviceDemand);
				e.printStackTrace();
			}
		}

		return utilizationUpperBound;
	}

}
