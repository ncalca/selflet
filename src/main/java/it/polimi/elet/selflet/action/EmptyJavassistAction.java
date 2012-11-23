package it.polimi.elet.selflet.action;

import it.polimi.elet.selflet.knowledge.KnowledgeBase;

/**
 * An empty javassist action.
 * 
 * Code is attached to the method of this class at runtime
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class EmptyJavassistAction implements IJavassistAction {

	@Override
	public Object executeAction(IActionAPI actionExecutor) {
		return KnowledgeBase.EMPTY_FIELD;
	}

}
