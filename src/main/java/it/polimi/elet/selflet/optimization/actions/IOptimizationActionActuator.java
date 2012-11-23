package it.polimi.elet.selflet.optimization.actions;


/**
 * This interface is used to actuate an optimization action
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IOptimizationActionActuator {

	/**
	 * Actuates the given optimization action
	 * */
	void actuateAction(IOptimizationAction optimizationAction);
}
