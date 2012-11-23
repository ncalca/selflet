package it.polimi.elet.selflet.action;

import it.polimi.elet.selflet.service.RunningService;

/**
 * Factory for action executors
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IActionExecutorFactory {

	IActionExecutor createActionExecutor(RunningService runningService);

}
