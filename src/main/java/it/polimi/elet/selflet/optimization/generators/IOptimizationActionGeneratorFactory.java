package it.polimi.elet.selflet.optimization.generators;

import it.polimi.elet.selflet.optimization.actions.OptimizationActionTypeEnum;


/**
 * Factory for action generators
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IOptimizationActionGeneratorFactory {

	/**
	 * Returns an action generator of the given type
	 * */
	IActionGenerator createActionGenerator(OptimizationActionTypeEnum actionType);
}
