package it.polimi.elet.selflet.behavior;

import it.polimi.elet.selflet.action.IActionFactory;
import it.polimi.elet.selflet.behaviorParser.selfletbehavior.Behavior;
import it.polimi.elet.selflet.behaviorParser.selfletbehavior.Condition;
import it.polimi.elet.selflet.behaviorParser.selfletbehavior.Selflet;
import it.polimi.elet.selflet.behaviorParser.selfletbehavior.SelfletbehaviorPackage;
import it.polimi.elet.selflet.behaviorParser.selfletbehavior.State;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import com.google.common.collect.Maps;

import static com.google.common.base.Strings.*;

/**
 * This class build a behavior structure form a given EMF model of the behavior
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class EMFBehaviorBuilder {

	private static final double DEFAULT_ELEMENTARY_BEHAVIOR_COST = 1e2;

	private final IActionFactory actionFactory;

	private EList<Behavior> eBehaviors;

	public EMFBehaviorBuilder(IActionFactory actionFactory, File behaviorFile) {
		this.actionFactory = actionFactory;
		init(behaviorFile);
	}

	private void init(File file) {
		SelfletbehaviorPackage.eINSTANCE.eClass();
		Resource.Factory.Registry registry = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> map = registry.getExtensionToFactoryMap();
		map.put("service", new XMIResourceFactoryImpl());
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.getResource(URI.createURI(file.getAbsolutePath()), true);
		Selflet selflet = (Selflet) resource.getContents().get(0);
		this.eBehaviors = selflet.getService().get(0).getBehavior();
	}

	public List<IBehavior> getBehaviors() {
		List<IBehavior> behaviors = new ArrayList<IBehavior>();

		for (Behavior eBehavior : eBehaviors) {
			IBehavior behavior = createBehavior(eBehavior);
			behaviors.add(behavior);
		}

		return behaviors;
	}

	private IBehavior createBehavior(Behavior eBehavior) {
		EList<State> states = eBehavior.getState();
		String behaviorName = eBehavior.getName();

		Map<State, Integer> stateMap = Maps.newHashMap();
		BehaviorStructure behaviorStructure = new BehaviorStructure(behaviorName);
		int stateId = 0;

		for (State eState : states) {
			stateMap.put(eState, stateId);
			String stateName = getStateName(eState);

			it.polimi.elet.selflet.action.Action action = getAction(eState);
			it.polimi.elet.selflet.behavior.State state = new it.polimi.elet.selflet.behavior.State(stateName, Integer.toString(stateId),
					isInitialState(eState), isFinalState(eState), action);

			behaviorStructure.addState(state);
			stateId++;
		}

		Integer transitionId = 0;

		for (State sourceEState : states) {
			EList<Condition> conditions = sourceEState.getNext();

			for (Condition eCondition : conditions) {
				State targetEState = eCondition.getTargetState();
				String transitionName = "transition_" + transitionId;

				it.polimi.elet.selflet.behavior.State sourceState = behaviorStructure.getStateById(stateMap.get(sourceEState).toString());
				it.polimi.elet.selflet.behavior.State targetState = behaviorStructure.getStateById(stateMap.get(targetEState).toString());
				it.polimi.elet.selflet.behavior.Condition condition = getCondition(eCondition, transitionId);

				Transition transition = new Transition(transitionId.toString(), sourceState, targetState, condition, transitionName);

				behaviorStructure.addTransition(transition);
				transitionId++;
			}

		}

		boolean isComplexBehavior = isComplexBehavior(eBehavior);
		if (isComplexBehavior) {
			return new it.polimi.elet.selflet.behavior.Behavior(behaviorStructure);
		} else {
			return new ElementaryBehavior(behaviorStructure, DEFAULT_ELEMENTARY_BEHAVIOR_COST);
		}
	}

	private boolean isComplexBehavior(Behavior eBehavior) {
		String className = eBehavior.eClass().getName();

		if (className.equalsIgnoreCase("Complex")) {
			return true;
		}

		if (className.equalsIgnoreCase("Elementary")) {
			return false;
		}

		throw new IllegalArgumentException("Wrong type for behavior class: " + className);
	}

	private it.polimi.elet.selflet.behavior.Condition getCondition(Condition eCondition, Integer transitionId) {

		String name = "Condition" + transitionId;
		String uniqueId = transitionId.toString();
		String expression = nullToEmpty(eCondition.getBody());
		String language = JEXL2ConditionEvaluator.JEXL2_CONDITION_LANGUAGE;

		return new it.polimi.elet.selflet.behavior.Condition(name, uniqueId, expression, language);
	}

	private String getStateName(State eState) {
		if (isInitialState(eState)) {
			return "Init";
		}

		if (isFinalState(eState)) {
			return "Final";
		}

		return nullToEmpty(eState.getName());
	}

	private boolean isFinalState(State eState) {
		String className = eState.eClass().getName();
		return className.equalsIgnoreCase(SelfletbehaviorPackage.Literals.FINAL.getName());
	}

	private boolean isInitialState(State eState) {
		String className = eState.eClass().getName();
		return className.equalsIgnoreCase(SelfletbehaviorPackage.Literals.INIT.getName());
	}

	private it.polimi.elet.selflet.action.Action getAction(State eState) {
		return actionFactory.createAction(eState.getAction());
	}

}
