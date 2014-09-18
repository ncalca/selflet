package it.polimi.elet.selflet.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableMap;

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

	private static final Logger LOG = Logger
			.getLogger(NextStateExtractor.class);

	private final IBehavior defaultBehavior;
	private final IConditionEvaluator conditionEvaluator;
	private final Map<String, Double> dummyProbabilites = ImmutableMap.of(
			"downloadVideo", 0.95, "getSubtitles", 0.2,
			"checkIfSubtitlesExists", 0.95, "getDefaultSubtitle", 0.9,
			"translateSubtitleToLanguage", 0.3);

	public NextStateExtractor(IBehavior defaultBehavior,
			IConditionEvaluator conditionEvaluator) {
		this.defaultBehavior = defaultBehavior;
		this.conditionEvaluator = conditionEvaluator;
	}

	/**
	 * Returns the next state to be executed in the behavior
	 * 
	 * @throws DeadStateException
	 *             if conditions is false in all directions
	 * */
	public it.polimi.elet.selflet.behavior.State nextState(
			it.polimi.elet.selflet.behavior.State currentState) {

		currentState.incrementVisits();
		List<Transition> outgoingTransitions = defaultBehavior
				.getOutgoingTransitions(currentState);
		Collections.shuffle(outgoingTransitions);

		for (Transition transitionToEvaluate : outgoingTransitions) {

			LOG.info("transition source: "
					+ transitionToEvaluate.getSource().getName());
			LOG.info("transition target: "
					+ transitionToEvaluate.getTarget().getName());
			LOG.info("transition probability: "
					+ transitionToEvaluate.getProbability());
			Condition condition = transitionToEvaluate.getCondition();
			boolean conditionOnArcTrue = conditionEvaluator.evaluate(condition);

			if (conditionOnArcTrue) {
				transitionToEvaluate.incrementVisits();
				return transitionToEvaluate.getTarget();
			}
		}

		throw new DeadStateException("Condition is false in all transitions");
	}

	// TODO very dummy function to use predefined probability for transitions.
	// Just for testing/simulation purposes. To use the standard nexState
	// function go to RunningService.java
	public it.polimi.elet.selflet.behavior.State getNextStateDummy(
			it.polimi.elet.selflet.behavior.State currentState) {
		List<Transition> outgoingTransitions = defaultBehavior
				.getOutgoingTransitions(currentState);
		if (outgoingTransitions.size() == 1) {
//			LOG.info("one target state: "
//					+ outgoingTransitions.get(0).getTarget().getName());
			return outgoingTransitions.get(0).getTarget();
		}

		Random random = new Random();
		double rand = random.nextDouble();
//		LOG.info("random number: " + rand);
		int index = -1;
		if (dummyProbabilites.containsKey(outgoingTransitions.get(0)
				.getTarget().getName())) {
			index = 0;
		} else {
			index = 1;
		}

		String targetName = outgoingTransitions.get(index).getTarget()
				.getName();
//		LOG.info("target name: " + targetName + "; prob: "
//				+ dummyProbabilites.get(targetName) + "; rand: " + rand + ";");
		if (dummyProbabilites.get(targetName) > rand) {
//			LOG.info("next state: " + targetName);
			return outgoingTransitions.get(index).getTarget();
		} else {
//			LOG.info("next state: "
//					+ outgoingTransitions.get(1 - index).getTarget().getName());
			return outgoingTransitions.get(1 - index).getTarget();
		}

	}
}
