package it.polimi.elet.selflet.action;

/**
 * Factory for action objects
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IActionFactory {

	/**
	 * Creates an action form the the ecore object created whil parsing selflet
	 * xml file
	 * */
	it.polimi.elet.selflet.action.Action createAction(it.polimi.elet.selflet.behaviorParser.selfletbehavior.Action ecoreAction);
}
