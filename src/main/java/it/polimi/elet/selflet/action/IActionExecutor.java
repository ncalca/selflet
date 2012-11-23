package it.polimi.elet.selflet.action;

/**
 * Interface for classes in charge of executing actions
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IActionExecutor {

	/**
	 * Executes the given action from the given statename and returns the result
	 * */
	Object executeAction(Action action, String stateName);

}
