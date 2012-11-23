package it.polimi.elet.selflet.autonomic;

import it.polimi.elet.selflet.ability.IAbilityExecutionEnvironment;
import it.polimi.elet.selflet.exceptions.AutonomicManagerException;
import it.polimi.elet.selflet.knowledge.IKnowledgesContainer;
import it.polimi.elet.selflet.negotiation.INegotiationManager;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static junit.framework.Assert.*;

public class AutonomicManagerTest {

	private IAutonomicManager autonomicManager;
	private INegotiationManager negotiationManager;
	private IAbilityExecutionEnvironment abilityExecutionEnvironment;
	private IKnowledgesContainer knowledges;
	private IAutonomicActuator autonomicAttuator;

	private static final String WORKING_DIR = "src/main/resources/selflets/selflet1/";

	@Before
	public void setUpTest() {
		negotiationManager = mock(INegotiationManager.class);
		abilityExecutionEnvironment = mock(IAbilityExecutionEnvironment.class);
		knowledges = mock(IKnowledgesContainer.class);
		autonomicAttuator = mock(IAutonomicActuator.class);
	}

	@Test
	public void testRuleLoading() {
		String rulePath = "defaultPolicy.drl";
		autonomicManager = new AutonomicManagerDrools(negotiationManager, abilityExecutionEnvironment, knowledges, autonomicAttuator);
		autonomicManager.addRuleFile(WORKING_DIR + "rules/" + rulePath);

		try {
			autonomicManager.start();
		} catch (AutonomicManagerException e) {
			fail();
		}

		assertEquals(1, autonomicManager.getNumberOfLoadedPackages());
	}
}
