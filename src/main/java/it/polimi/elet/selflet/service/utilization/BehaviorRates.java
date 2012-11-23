package it.polimi.elet.selflet.service.utilization;

import it.polimi.elet.selflet.behavior.BehaviorStructure;
import it.polimi.elet.selflet.behavior.IBehavior;
import it.polimi.elet.selflet.behavior.TransitionProbability;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.Maps;

import Jama.Matrix;

/**
 * Utility class to compute the rates for behaviors
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class BehaviorRates {

	private static final Logger LOG = Logger.getLogger(BehaviorRates.class);

	private BehaviorRates() {
		// private counstructor
	};

	/**
	 * Return the probabilities to execute each single state of the behavior.
	 * 
	 * @param behavior
	 *            behavior we want to calculate the probabilities
	 */
	public static Map<String, Double> getProbabilities(IBehavior behavior) {
		return getInternalRates(behavior, 1);
	}

	/**
	 * Given a behavior, this method returns a Map containing with key the
	 * service name and for value the request rate for that service within the
	 * behavior
	 * 
	 * @param defaultBehavior
	 *            the default behavior implementing the service
	 * @param serviceCompletionRate
	 *            the request rate of the service which is implemented by the
	 *            behavior passed as input
	 * */
	public static Map<String, Double> getInternalRates(IBehavior defaultBehavior, double serviceCompletionRate) {

		BehaviorStructure structure = defaultBehavior.getBehaviorStructure();

		List<TransitionProbability> probabilities = structure.getTransitionProbabilities();

		List<String> stateNames = defaultBehavior.getStateNames();
		int size = stateNames.size() - 1;

		Matrix probabilityMatrix = new Matrix(size, size);

		for (TransitionProbability transition : probabilities) {
			// don't consider transitions toward final states because we don't
			// need them
			if (transition.isTowardFinalState()) {
				continue;
			}

			int i = stateNames.indexOf(transition.getFrom());
			int j = stateNames.indexOf(transition.getTo());

			probabilityMatrix.set(i, j, transition.getProbability());
		}

		checkNormalizationConstraint(probabilityMatrix, size);

		// probabilityMatrix.print(4, 1);

		// get the tranpose matrix
		probabilityMatrix = probabilityMatrix.transpose();

		// A = A*(-1) + I
		probabilityMatrix = probabilityMatrix.times(-1).plus(Matrix.identity(size, size));

		// probabilityMatrix.print(4, 1);

		Matrix constantTerm = new Matrix(size, 1);
		constantTerm.set(0, 0, serviceCompletionRate);

		Matrix lambda = probabilityMatrix.inverse().times(constantTerm);

		// lambda.print(4, 1);

		// put results in the Map
		Map<String, Double> internalServiceRequestRate = Maps.newHashMap();

		for (int i = 0; i < stateNames.size() - 1; i++) {
			Double req = Double.valueOf(lambda.get(i, 0));
			internalServiceRequestRate.put(stateNames.get(i), req);
		}

		return internalServiceRequestRate;
	}

	private static void checkNormalizationConstraint(Matrix probabilityMatrix, int size) {
		// check normalization constraint
		for (int i = 0; i < size; i++) {

			double sum = 0;
			int j = 0;

			for (; j < size; j++) {
				sum += probabilityMatrix.get(i, j);
			}

			if ((sum != 1) && (j < (size - 1))) {
				LOG.error("Wrong probabilities on behavior! Sum at row " + i + " is " + sum);
			}
		}

	}

}
