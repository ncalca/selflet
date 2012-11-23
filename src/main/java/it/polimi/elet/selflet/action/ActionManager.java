package it.polimi.elet.selflet.action;

import it.polimi.elet.selflet.events.SelfletComponent;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.inject.Singleton;

/**
 * This class is in charge of creating and returning actions
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * @author silvia
 */
@Singleton
public class ActionManager extends SelfletComponent implements IActionManager {

	// association between action name and actions instance
	private final Map<Action, IJavassistAction> actions;

	public ActionManager() {
		actions = Maps.newHashMap();
	}

	@Override
	public void addAction(Action action) {
		if (actions.containsKey(action)) {
			return;
		}
		IJavassistAction javassistAction = loadJavassistAction(action);
		actions.put(action, javassistAction);
	}

	@Override
	public synchronized IJavassistAction getJavassistAction(Action action) {
		if (!actions.containsKey(action)) {
			addAction(action);
		}
		return actions.get(action);
	}

	private IJavassistAction loadJavassistAction(Action action) {
		JavassistActionCreator actionLoader = new JavassistActionCreator(action);
		return actionLoader.createAction();
	}

}
