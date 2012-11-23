package it.polimi.elet.selflet.negotiation.messageHandlers;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import it.polimi.elet.selflet.action.IActionManager;
import it.polimi.elet.selflet.events.IEventDispatcher;
import it.polimi.elet.selflet.events.SelfletComponent;
import it.polimi.elet.selflet.knowledge.IKnowledgesContainer;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.message.IMessageHandler;
import it.polimi.elet.selflet.message.ISelfLetMsgFactory;
import it.polimi.elet.selflet.message.SelfLetMessageTypeEnum;
import it.polimi.elet.selflet.negotiation.INeighborManager;
import it.polimi.elet.selflet.negotiation.nodeState.INeighborStateManager;
import it.polimi.elet.selflet.service.IRunningServiceFactory;
import it.polimi.elet.selflet.service.utilization.IRedirectMonitor;

/**
 * An implementation for ISelfletMessageHandlerFactory
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class SelfletMessageHandlerFactory extends SelfletComponent implements ISelfletMessageHandlerFactory {

	private final IKnowledgesContainer knowledges;
	private final ISelfLetMsgFactory selfletMessageFactory;
	private final IMessageHandler messageHandler;
	private final IRunningServiceFactory runningServiceFactory;
	private final IServiceKnowledge serviceKnowledge;
	private final IActionManager actionManager;
	private final INeighborManager neighborManager;
	private final INeighborStateManager neighborStateManager;
	private final IRedirectMonitor redirectMonitor;

	// TODO remove setEventDispatcher and have the dispatcher injected
	@Inject
	public SelfletMessageHandlerFactory(IKnowledgesContainer knowledges, ISelfLetMsgFactory selfletMessageFactory, IMessageHandler messageHandler,
			IRunningServiceFactory runningServiceFactory, IActionManager actionManager, INeighborManager neighborManager,
			INeighborStateManager neighborStateManager, IRedirectMonitor redirectMonitor) {
		this.knowledges = knowledges;
		this.serviceKnowledge = knowledges.getServiceKnowledge();
		this.neighborManager = neighborManager;
		this.selfletMessageFactory = selfletMessageFactory;
		this.messageHandler = messageHandler;
		this.runningServiceFactory = runningServiceFactory;
		this.actionManager = actionManager;
		this.neighborStateManager = neighborStateManager;
		this.redirectMonitor = redirectMonitor;
	}

	@Override
	public ISelfletMessageHandler create(SelfLetMessageTypeEnum messageHandlerType) {

		switch (messageHandlerType) {

		case ASK_NEEDED_SERVICE:
			return new AskNeededServiceMessageHandler(serviceKnowledge, selfletMessageFactory, messageHandler);

		case GET_SERVICE_PROVIDER_INDIRECTLY:
			return new GetServiceProviderIndirectlyMessageHandler(serviceKnowledge, selfletMessageFactory, messageHandler);

		case EXECUTE_ACHIEVABLE_SERVICE:
			return new ExecuteAchievableServiceMessageHandler(knowledges, selfletMessageFactory, messageHandler, dispatcher, runningServiceFactory);

		case DOWNLOAD_ACHIEVABLE_SERVICE:
			return new DownloadAchievableServiceMessageHandler(serviceKnowledge, selfletMessageFactory, messageHandler, dispatcher);

		case ADVERTISE_ACHIEVABLE_SERVICE:
			return new AdvertiseAchievableServiceMessageHandler(serviceKnowledge, dispatcher);

		case NODE_STATE:
			return new NodeStateMessageHandler(neighborStateManager);

		case REDIRECT_REQUEST:
			return new RedirectRequestMessageHandler(dispatcher, redirectMonitor);

		case REDIRECT_REQUEST_REPLY:
			return new RedirectRequestReplyMessageHandler(dispatcher);

		case SERVICE_TEACH:
			return new ServiceTeachMessageHandler(serviceKnowledge, actionManager);

		case NEIGHBORS:
			return new NeighborsMessageHandler(neighborManager);

		case REMOVE_SELFLET:
			return new RemoveNeighborMessageHandler(neighborManager, neighborStateManager);

		default:
			throw new IllegalArgumentException("Invalid enumeration type for message handler " + messageHandlerType);
		}
	}

	@Override
	public void setDispatcher(IEventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

}
