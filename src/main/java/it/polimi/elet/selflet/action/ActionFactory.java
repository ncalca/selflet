package it.polimi.elet.selflet.action;

import static com.google.common.base.Strings.nullToEmpty;
import it.polimi.elet.selflet.behaviorParser.selfletbehavior.Action;
import it.polimi.elet.selflet.utilities.UtilitiesProvider;

/**
 * An implementation of IActionFactory interface
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ActionFactory implements IActionFactory {

	private static final String JAVASSIST_LANGUAGE = "javassist";
	private static final String ACTIONS_FOLDER = "/actions/";

	@Override
	public it.polimi.elet.selflet.action.Action createAction(Action ecoreAction) {
		String uniqueId = getUniqueId(ecoreAction);
		String actionFile = getActionFile(ecoreAction);
		String actionExpression = getActionExpression(actionFile);
		return new it.polimi.elet.selflet.action.Action(actionFile, uniqueId, actionExpression, JAVASSIST_LANGUAGE);
	}

	private String getActionExpression(String actionFile) {
		return nullToEmpty(actionFile).isEmpty() ? "" : loadActionExpression(actionFile);
	}

	private String getActionFile(Action ecoreAction) {
		return cleanActionFileName((ecoreAction == null) ? "" : nullToEmpty(ecoreAction.getBody()));
	}

	private String getUniqueId(Action ecoreAction) {
		return (ecoreAction == null) ? "" : nullToEmpty(ecoreAction.getActionFile());
	}

	private String loadActionExpression(String actionFile) {
		String fullFilePath = UtilitiesProvider.getWorkingDir() + ACTIONS_FOLDER + actionFile;
		ActionFileReader actionFileReader = new ActionFileReader(fullFilePath);
		return actionFileReader.loadCode();
	}

	private String cleanActionFileName(String actionFile) {
		return actionFile.replace("do /", "");
	}

}
