package it.polimi.elet.selflet.action;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.Assert.*;

import static org.mockito.Mockito.*;

public class ActionManagerTest {

	private IActionManager actionManager;

	@BeforeClass
	public static void setup() {
	}

	@Before
	public void setupTest() {
		actionManager = new ActionManager();
	}

	@Test
	public void testGetExistingAction() {
		Action action = mock(Action.class);
		when(action.getName()).thenReturn("actions/service1.service.behavior1.serviceA.action");
		when(action.getActionExpression()).thenReturn("");

		IJavassistAction javassistAction = actionManager.getJavassistAction(action);
		assertNotNull(javassistAction);
	}

}
