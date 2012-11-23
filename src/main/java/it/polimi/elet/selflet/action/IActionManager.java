package it.polimi.elet.selflet.action;

import it.polimi.elet.selflet.events.ISelfletComponent;

/**
 * Interface for class storing all loaded actions
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IActionManager extends ISelfletComponent {

	/**
	 * Adds an action in the action manager
	 * */
	void addAction(Action action);

	IJavassistAction getJavassistAction(Action action);

}
