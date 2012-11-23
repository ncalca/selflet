package it.polimi.elet.selflet.action;

import org.apache.log4j.Logger;

import it.polimi.elet.selflet.exceptions.ActionException;
import it.polimi.elet.selflet.knowledge.KnowledgeBase;

import static com.google.common.base.Strings.*;

/**
 * Provides the method to execute an action
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ActionExecutor implements IActionExecutor {

	private static final Logger LOG = Logger.getLogger(IActionExecutor.class);

	private IActionManager actionManager;
	private IActionAPI actionAPI;

	public ActionExecutor(IActionManager actionManager, IActionAPI actionAPI) {
		this.actionManager = actionManager;
		this.actionAPI = actionAPI;
	}

	/**
	 * Execute the given action
	 * */
	public Object executeAction(Action action, String localArea) throws ActionException {

		if (action == null) {
			throw new IllegalArgumentException("Trying to execute null action");
		}

		String language = nullToEmpty(action.getActionLanguage());
		String actionName = nullToEmpty(action.getName());

		if (!language.equals("javassist")) {
			throw new ActionException("Incorrect action language (expecting \"javassist\", found \"" + language + "\")");
		}

		IJavassistAction javassistAction = actionManager.getJavassistAction(action);
		Object result = javassistAction.executeAction(actionAPI);

		LOG.debug("Action " + actionName + " with result [" + result + "]");

		return (result == null) ? KnowledgeBase.ERROR : result;
	}

}
