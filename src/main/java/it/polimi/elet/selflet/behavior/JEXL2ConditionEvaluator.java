package it.polimi.elet.selflet.behavior;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

import com.google.inject.Inject;

import it.polimi.elet.selflet.exceptions.ParserException;
import it.polimi.elet.selflet.knowledge.IGeneralKnowledge;

/**
 * An evaluator for conditions written in JEXL2 language
 * (http://commons.apache.org/jexl)
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class JEXL2ConditionEvaluator implements IConditionEvaluator {

	public static final String JEXL2_CONDITION_LANGUAGE = "jexl2";

	private final JexlEngine jexl;
	private final IGeneralKnowledge generalKnowledge;

	@Inject
	public JEXL2ConditionEvaluator(IGeneralKnowledge generalKnowledge) {
		this.generalKnowledge = generalKnowledge;
		this.jexl = new JexlEngine();
	}

	@Override
	public boolean evaluate(Condition condition) throws ParserException {

		if (condition == null) {
			// interpret null condition as true
			return true;
		}

		String language = condition.getLanguage();

		if (!language.equalsIgnoreCase(JEXL2_CONDITION_LANGUAGE)) {
			throw new IllegalArgumentException("Error while parsing condition. Expected " + JEXL2_CONDITION_LANGUAGE + " received " + language);
		}

		String stringExpression = condition.getExpression();
		if (stringExpression.isEmpty()) {
			return true;
		}

		Expression expression = jexl.createExpression(stringExpression);
		JexlContext jexlContext = createJexlContext();
		return (Boolean) expression.evaluate(jexlContext);
	}

	private JexlContext createJexlContext() {
		JexlContext context = new MapContext();
		Map<String, Object> properties = generalKnowledge.getProperties();

		for (Entry<String, Object> entry : properties.entrySet()) {
			context.set(entry.getKey(), entry.getValue());
		}

		return context;
	}

}
