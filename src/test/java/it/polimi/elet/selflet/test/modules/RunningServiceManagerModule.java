package it.polimi.elet.selflet.test.modules;

import it.polimi.elet.selflet.action.ActionAPIFactory;
import it.polimi.elet.selflet.action.IActionAPIFactory;
import it.polimi.elet.selflet.knowledge.IGeneralKnowledge;
import it.polimi.elet.selflet.knowledge.KnowledgeBase;

import com.google.inject.AbstractModule;

public class RunningServiceManagerModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IGeneralKnowledge.class).to(KnowledgeBase.class);
		bind(IActionAPIFactory.class).to(ActionAPIFactory.class);
	}

}
