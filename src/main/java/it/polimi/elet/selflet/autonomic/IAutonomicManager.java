package it.polimi.elet.selflet.autonomic;

import java.util.List;

import it.polimi.elet.selflet.events.ISelfletComponent;

/**
 * The interface which represents an Autonomic Manager
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * @author Davide Devescovi
 */
public interface IAutonomicManager extends ISelfletComponent {

	void addRuleFile(String ruleFilePath);

	void addRuleFiles(List<String> ruleFilePaths);

	List<String> getRuleFiles();

	/**
	 * Starts looping the Autonomic Manager
	 */
	void start();

	/**
	 * Adds a rule to the manager
	 * 
	 * @param rule
	 *            the rule to be added (implementation dependent)
	 */
	void addRule(Object rule);

	/**
	 * Removes a rule from the manager
	 * 
	 * @param rule
	 *            the rule to be removed (implementation dependent)
	 */
	void removeRule(Object rule);

	int getNumberOfLoadedPackages();

}