package it.polimi.elet.selflet.optimization.actions.scaling;

import it.polimi.elet.selflet.optimization.actions.IOptimizationAction;
import it.polimi.elet.selflet.optimization.actions.OptimizationActionTypeEnum;

/**
 * Action to removed the current selflet
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class RemoveSelfletAction implements IOptimizationAction {

	private final double weight;

	public RemoveSelfletAction(double weight) {
		this.weight = Math.max(weight, MIN_WEIGHT);
	}

	@Override
	public double weight() {
		return weight;
	}

	@Override
	public OptimizationActionTypeEnum optimizationType() {
		return OptimizationActionTypeEnum.REMOVE_SELFLET;
	}

	@Override
	public String toString() {
		return "Remove selflet";
	}

}
