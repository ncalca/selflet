package it.polimi.elet.selflet.service.utilization;

import it.polimi.elet.selflet.configuration.SelfletConfiguration;

/**
 * Default implementation of the utilization strategy. Simply return the upper
 * bound read from configuration file.
 * 
 * @author lucaflorio
 * 
 */
public class UtilizationUpperBoundDefault implements IUtilizationStrategy {

	private static final double UTILIZATION_UPPER_BOUND = SelfletConfiguration
			.getSingleton().utilizationUpperBound;

	@Override
	public double computeUtilizationUpperBound() {
		return UTILIZATION_UPPER_BOUND;
	}

}
