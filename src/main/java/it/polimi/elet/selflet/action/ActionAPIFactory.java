package it.polimi.elet.selflet.action;

import it.polimi.elet.selflet.ability.IAbilityExecutionEnvironment;
import it.polimi.elet.selflet.knowledge.IKnowledgesContainer;
import it.polimi.elet.selflet.service.RunningService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Creates action APIs used by actions
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class ActionAPIFactory implements IActionAPIFactory {

	private final IKnowledgesContainer knowledges;
	private final IAbilityExecutionEnvironment abilityExecutionEnvironment;

	@Inject
	public ActionAPIFactory(IKnowledgesContainer knowledges, IAbilityExecutionEnvironment abilityExecutionEnvironment) {
		this.knowledges = knowledges;
		this.abilityExecutionEnvironment = abilityExecutionEnvironment;
	}

	public IActionAPI createActionAPI(RunningService runningService) {
		return new ActionAPI(abilityExecutionEnvironment, knowledges, runningService);
	}

}
