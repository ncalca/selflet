package it.polimi.elet.selflet.behavior;

import it.polimi.elet.selflet.action.ActionFactory;
import it.polimi.elet.selflet.action.IActionFactory;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class TestEMFBehaviorBuilder {

	private static final String SERVICE1_PATH = "/behaviors/service1.service";
	private EMFBehaviorBuilder behaviorBuilder;
	private List<IBehavior> behaviors;
	private IBehavior behavior;

	@Before
	public void setUp() {
		File file = getFile(SERVICE1_PATH);
		IActionFactory actionFactory = new ActionFactory();
		this.behaviorBuilder = new EMFBehaviorBuilder(actionFactory, file);
		this.behaviors = behaviorBuilder.getBehaviors();
		this.behavior = behaviors.get(0);
	}

	@Test
	public void testNumberOfReturnedBehaviors() {
		assertEquals(1, behaviors.size());
	}

	@Test
	public void testBehaviorName() {
		assertEquals("behavior1", behavior.getName());
	}

	@Test
	public void testNumberOfStates() {
		assertEquals(5, behavior.getStates().size());
	}

	@Test
	public void testInitialState() {
		State initialState = behavior.getBehaviorStructure().getInitialState();
		assertTrue(initialState.isInitialState());
	}

	@Test
	public void testOutgoingTransitionsFromInitialState() {
		State initialState = behavior.getBehaviorStructure().getInitialState();
		assertEquals(behavior.getBehaviorStructure().getOutgoingTransitions(initialState).size(), 2);
	}

	private File getFile(String resourcePath) {
		URL resource = this.getClass().getResource(resourcePath);
		return new File(resource.getPath());
	}

}
