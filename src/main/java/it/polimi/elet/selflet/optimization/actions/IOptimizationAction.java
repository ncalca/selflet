package it.polimi.elet.selflet.optimization.actions;

import it.polimi.elet.selflet.utilities.IWeightedItem;


/**
 * Interface describing a generic optimization action that can be performed in
 * the Selflet
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IOptimizationAction extends IWeightedItem {

	/**
	 * Returns the type of optimization for this action
	 * */
	OptimizationActionTypeEnum optimizationType();

}
