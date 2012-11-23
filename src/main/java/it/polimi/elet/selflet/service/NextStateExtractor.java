package it.polimi.elet.selflet.service;

import java.util.Collections;
import java.util.List;

import it.polimi.elet.selflet.behavior.Condition;
import it.polimi.elet.selflet.behavior.IBehavior;
import it.polimi.elet.selflet.behavior.IConditionEvaluator;
import it.polimi.elet.selflet.behavior.Transition;
import it.polimi.elet.selflet.exceptions.DeadStateException;

/**
 * Extract the next state from a behavior evaluating the conditions on the arcs
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class NextStateExtractor {

	private final IBehavior defaultBehavior;
	private final IConditionEvaluator conditionEvaluator;

	public NextStateExtractor(IBehavior defaultBehavior, IConditionEvaluator conditionEvaluator) {
		this.defaultBehavior = defaultBehavior;
		this.conditionEvaluator = conditionEvaluator;
	}

	/**
	 * Returns the next state to be executed in the behavior
	 * 
	 * @throws DeadStateException
	 *             if conditions is false in all directions
	 * */
	public it.polimi.elet.selflet.behavior.State nextState(it.polimi.elet.selflet.behavior.State currentState) {

		currentState.incrementVisits();
		List<Transition> outgoingTransitions = defaultBehavior.getOutgoingTransitions(currentState);
		Collections.shuffle(outgoingTransitions);

		for (Transition transitionToEvaluate : outgoingTransitions) {

			Condition condition = transitionToEvaluate.getCondition();
			boolean conditionOnArcTrue = conditionEvaluator.evaluate(condition);

			if (conditionOnArcTrue) {
				transitionToEvaluate.incrementVisits();
				return transitionToEvaluate.getTarget();
			}
		}

		throw new DeadStateException("Condition is false in all transitions");
	}
}
