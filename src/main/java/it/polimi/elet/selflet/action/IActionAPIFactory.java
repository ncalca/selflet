package it.polimi.elet.selflet.action;

import it.polimi.elet.selflet.service.RunningService;

/**
 * Creates an action API which is passed to the action
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IActionAPIFactory {

	IActionAPI createActionAPI(RunningService runningService);

}
