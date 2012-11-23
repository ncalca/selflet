package it.polimi.elet.selflet.behavior;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import it.polimi.elet.selflet.knowledge.IGeneralKnowledge;
import it.polimi.elet.selflet.knowledge.KnowledgeBase;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.junit.Before;
import org.junit.Test;

/**
 * Simple test just to understand how the JEXL2 library works...and it works
 * great!
 * */
public class TestJEXL2ConditionEvaluator {

	private JexlEngine jexl;
	private JEXL2ConditionEvaluator conditionEvaluator;
	private IGeneralKnowledge generalKnowledge;

	@Before
	public void setUp() {
		this.jexl = new JexlEngine();
		this.generalKnowledge = new KnowledgeBase();
		this.conditionEvaluator = new JEXL2ConditionEvaluator(generalKnowledge);

	}

	@Test
	public void testSimpleExpression() {
		String stringExpression = "(foo + 2) > 4";
		Expression expression = jexl.createExpression(stringExpression);

		JexlContext jexlContext = new MapContext();
		jexlContext.set("foo", new Integer(3));

		Boolean result = (Boolean) expression.evaluate(jexlContext);
		assertTrue(result);
	}

	@Test
	public void testPositiveExpression() {
		generalKnowledge.setProperty("A", "2");
		generalKnowledge.setProperty("B", "1");
		String stringExpression = "A > B";
		Condition condition = new Condition("TestCondition", "1", stringExpression, JEXL2ConditionEvaluator.JEXL2_CONDITION_LANGUAGE);
		assertTrue(conditionEvaluator.evaluate(condition));
	}

	@Test
	public void testNegativeExpression() {
		generalKnowledge.setProperty("A", "2");
		generalKnowledge.setProperty("B", "1");
		String stringExpression = "A < B";
		Condition condition = new Condition("TestCondition", "1", stringExpression, JEXL2ConditionEvaluator.JEXL2_CONDITION_LANGUAGE);
		assertFalse(conditionEvaluator.evaluate(condition));
	}

}
