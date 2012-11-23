package it.polimi.elet.selflet.action;

import it.polimi.elet.selflet.service.RunningService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * An implementation for action executor factory
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class ActionExecutorFactory implements IActionExecutorFactory {

	private IActionManager actionManager;
	private IActionAPIFactory actionAPIFactory;

	@Inject
	public ActionExecutorFactory(IActionManager actionManager, IActionAPIFactory actionAPIFactory) {
		this.actionManager = actionManager;
		this.actionAPIFactory = actionAPIFactory;
	}

	@Override
	public IActionExecutor createActionExecutor(RunningService runningService) {
		IActionAPI actionAPI = actionAPIFactory.createActionAPI(runningService);
		return new ActionExecutor(actionManager, actionAPI);
	}

}
