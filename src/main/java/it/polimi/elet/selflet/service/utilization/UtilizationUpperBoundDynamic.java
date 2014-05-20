package it.polimi.elet.selflet.service.utilization;

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

	private IServiceKnowledge myServiceKnowlegde;
	private double utilizationUpperBound;

	/**
	 * Create an instance of this strategy
	 * 
	 * @param serviceKnowledge
	 *            the service knowledge of the selflet
	 */
	public UtilizationUpperBoundDynamic(IServiceKnowledge serviceKnowledge) {
		this.myServiceKnowlegde = serviceKnowledge;
		this.utilizationUpperBound = 1;
	}

	@Override
	public double computeUtilizationUpperBound() {
		double serviceDemand = 0;
		double tempUtilization = 0;

		utilizationUpperBound = 1;
		for (Service service : myServiceKnowlegde.getServices()) {
			try {

//				if (service.isLocallyAvailable()) {

					serviceDemand = service.getServiceDemand();

					tempUtilization = (1 - (serviceDemand / service
							.getMaxResponseTimeInMsec()));

					if (tempUtilization < utilizationUpperBound)
						utilizationUpperBound = tempUtilization;
//				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return utilizationUpperBound;
	}

}
