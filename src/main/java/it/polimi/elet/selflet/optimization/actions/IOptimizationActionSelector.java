package it.polimi.elet.selflet.optimization.actions;


import java.util.Set;

/**
 * This class is used to select an optimization action among a set of
 * optimization actions
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IOptimizationActionSelector {

	/**
	 * Returns an optimization action among a set of given optimization actions
	 * */
	IOptimizationAction selectAction(Set<IOptimizationAction> optimizationActions);

}
