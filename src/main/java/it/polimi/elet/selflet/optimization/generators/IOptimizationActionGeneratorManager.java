package it.polimi.elet.selflet.optimization.generators;


import it.polimi.elet.selflet.optimization.actions.IOptimizationAction;

import java.util.Set;

/**
 * Interface for optimization action generators
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IOptimizationActionGeneratorManager {

	
	/**
	 * Returns a set of possible optimization actions
	 * */
	Set<IOptimizationAction> generateActions();
	
	
	

}
