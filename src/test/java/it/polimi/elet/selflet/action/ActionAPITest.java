package it.polimi.elet.selflet.action;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import it.polimi.elet.selflet.ability.IAbilityExecutionEnvironment;
import it.polimi.elet.selflet.knowledge.IKnowledgesContainer;
import it.polimi.elet.selflet.knowledge.KnowledgeBase;
import it.polimi.elet.selflet.knowledge.Knowledges;
import it.polimi.elet.selflet.load.ILoadProfileManager;
import it.polimi.elet.selflet.service.RunningService;

import org.junit.Before;
import org.junit.Test;

public class ActionAPITest {

	private IActionAPI actionAPI;
	private IAbilityExecutionEnvironment abilityExecutionEnvironment;
	private IKnowledgesContainer knowledges;
	private ILoadProfileManager loadProfiler;

	@Before
	public void setup() {
		RunningService runningService = mock(RunningService.class);
		knowledges = mock(Knowledges.class);
		loadProfiler = mock(ILoadProfileManager.class);
		actionAPI = new ActionAPI(abilityExecutionEnvironment, knowledges, runningService, loadProfiler);
	}

	@Test
	public void testNonExistingService() {
		assertEquals(KnowledgeBase.ERROR, actionAPI.needService(null));
	}
}
