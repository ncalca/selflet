package it.polimi.elet.selflet.behavior;

/**
 * Represents a utility which is used to evaluate a condition.
 * 
 * @author Davide Devescovi
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 */
public interface IConditionEvaluator {

	/**
	 * Evaluates the given condition.
	 * 
	 * @param condition
	 *            an object representing a condition of a behaviour
	 * @param lastOutput
	 *            the output produced by the last service execution
	 * 
	 * @return true or false, depending on the evaluation result
	 * 
	 * @throws ParserException
	 *             if an error occurs
	 */
	boolean evaluate(Condition condition);
}