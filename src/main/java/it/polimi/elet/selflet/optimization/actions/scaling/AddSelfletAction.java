package it.polimi.elet.selflet.optimization.actions.scaling;

import it.polimi.elet.selflet.optimization.actions.IOptimizationAction;
import it.polimi.elet.selflet.optimization.actions.OptimizationActionTypeEnum;

/**
 * An optimization action that represents the need for a new selflet to be
 * istantiated
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class AddSelfletAction implements IOptimizationAction {

	private final double weight;

	public AddSelfletAction(double weight) {
		this.weight = Math.max(weight, MIN_WEIGHT);
	}

	@Override
	public OptimizationActionTypeEnum optimizationType() {
		return OptimizationActionTypeEnum.ADD_SELFLET;
	}

	@Override
	public double weight() {
		return weight;
	}

	@Override
	public String toString() {
		return "Add new selflet";
	}

}
