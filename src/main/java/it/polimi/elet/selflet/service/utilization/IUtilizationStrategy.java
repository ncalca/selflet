package it.polimi.elet.selflet.service.utilization;

/**
 * Interface to encapsulate the strategy to calculate the utilization upper bound of a selflet (Strategy Pattern)
 * @author lucaflorio
 *
 */
public interface IUtilizationStrategy {
	
	/**
	 * Compute the utilization upper bound of the selflet
	 * @return utilization upper bound of the selflet
	 */
	double computeUtilizationUpperBound();

}
