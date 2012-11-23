package it.polimi.elet.selflet.optimization.actions.changeImplementation;

import it.polimi.elet.selflet.optimization.actions.IOptimizationAction;
import it.polimi.elet.selflet.optimization.actions.OptimizationActionTypeEnum;

/**
 * Action chaning service implementation
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ChangeServiceImplementationAction implements IOptimizationAction {

	@Override
	public OptimizationActionTypeEnum optimizationType() {
		return OptimizationActionTypeEnum.CHANGE_SERVICE_IMPLEMENTATION;
	}

	@Override
	public double weight() {
		return MIN_WEIGHT;
	}

}
