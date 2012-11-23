package it.polimi.elet.selflet.action;

import it.polimi.elet.selflet.utilities.UtilitiesProvider;

import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

public class ActionLoaderTest {

	private static final String EXISTING_ACTION = "service1.service.behavior1.serviceA.action";
	private static final String NON_EXISTING_ACTION = "non.existing";

	@BeforeClass
	public static void setUp() {
		UtilitiesProvider.setWorkingDir("src/main/resources/selflets/selflet1");
	}

	@Test
	public void testLoadExistingAction() {

		it.polimi.elet.selflet.behaviorParser.selfletbehavior.Action ecoreAction = mock(it.polimi.elet.selflet.behaviorParser.selfletbehavior.Action.class);
		when(ecoreAction.getBody()).thenReturn(EXISTING_ACTION);
		IActionFactory actionFactory = new ActionFactory();
		Action action = actionFactory.createAction(ecoreAction);
		assertFalse(action.getActionExpression().isEmpty());
	}

	@Test
	public void testNonExistingAction() {
		it.polimi.elet.selflet.behaviorParser.selfletbehavior.Action ecoreAction = mock(it.polimi.elet.selflet.behaviorParser.selfletbehavior.Action.class);
		when(ecoreAction.getActionFile()).thenReturn(NON_EXISTING_ACTION);
		IActionFactory actionFactory = new ActionFactory();
		Action action = actionFactory.createAction(ecoreAction);
		assertTrue(action.getActionExpression().isEmpty());
	}
}
