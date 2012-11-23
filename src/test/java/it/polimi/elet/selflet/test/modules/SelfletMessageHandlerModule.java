package it.polimi.elet.selflet.test.modules;

import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.knowledge.IGeneralKnowledge;
import it.polimi.elet.selflet.knowledge.IKnowledgesContainer;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.knowledge.ITypeKnowledge;
import it.polimi.elet.selflet.knowledge.KnowledgeBase;
import it.polimi.elet.selflet.knowledge.Knowledges;
import it.polimi.elet.selflet.knowledge.ServiceKnowledge;
import it.polimi.elet.selflet.knowledge.TypeKnowledge;
import it.polimi.elet.selflet.message.IMessageHandler;
import it.polimi.elet.selflet.message.IRedsMessageFactory;
import it.polimi.elet.selflet.message.ISelfLetMsgFactory;
import it.polimi.elet.selflet.message.RedsMessageFactory;
import it.polimi.elet.selflet.message.RedsMessageHandler;
import it.polimi.elet.selflet.message.SelfLetMsgFactory;
import it.polimi.elet.selflet.negotiation.INeighborManager;
import it.polimi.elet.selflet.negotiation.NeighborManager;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class SelfletMessageHandlerModule extends AbstractModule {

	private final ISelfLetID selfletId;

	public SelfletMessageHandlerModule(ISelfLetID selfletId) {
		this.selfletId = selfletId;
	}

	@Override
	protected void configure() {
		bind(ISelfLetID.class).toInstance(selfletId);
		bind(IServiceKnowledge.class).to(ServiceKnowledge.class);
		bind(IGeneralKnowledge.class).to(KnowledgeBase.class);
		bind(ITypeKnowledge.class).to(TypeKnowledge.class);
		bind(IKnowledgesContainer.class).to(Knowledges.class);
		bind(IMessageHandler.class).to(RedsMessageHandler.class);
		bind(IRedsMessageFactory.class).to(RedsMessageFactory.class);
		bind(INeighborManager.class).to(NeighborManager.class);
	}

	@Provides
	public ISelfLetMsgFactory getSelfLetMsgFactory(ITypeKnowledge typeKnowledge) {
		return new SelfLetMsgFactory(selfletId);
	}

}
