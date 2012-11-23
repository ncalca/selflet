package it.polimi.elet.selflet.behavior;

import it.polimi.elet.selflet.knowledge.IGeneralKnowledge;

/**
 * Factory for creating condition evaluators
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class CondiditionEvaluatorFactory {

	private CondiditionEvaluatorFactory() {
		// private constructor
	}

	public static IConditionEvaluator create(IGeneralKnowledge generalKnowledge) {
		return new JEXL2ConditionEvaluator(generalKnowledge);
	}
}
