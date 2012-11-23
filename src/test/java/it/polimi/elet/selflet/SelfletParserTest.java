package it.polimi.elet.selflet;

import java.util.List;

import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Test selflet parser
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class SelfletParserTest {

	private static final String WORKING_DIR = "src/main/resources/selflets/selflet1/";
	private static final String POLICY_NAME = "defaultPolicy.drl";

	@Test
	public void testLoadRules() {
		SelfLetParser selfletParser = new SelfLetParser(WORKING_DIR);
		List<String> rules = selfletParser.parseRules();
		assertNotNull(rules);
		assertEquals(1, rules.size());

		String rule = rules.get(0);
		assertTrue(rule.endsWith(POLICY_NAME));
	}
}
