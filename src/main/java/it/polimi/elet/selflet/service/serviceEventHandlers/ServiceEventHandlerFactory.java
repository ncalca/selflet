package it.polimi.elet.selflet.service.serviceEventHandlers;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.knowledge.IGeneralKnowledge;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.message.IMessageHandler;
import it.polimi.elet.selflet.message.ISelfLetMsgFactory;
import it.polimi.elet.selflet.service.IRunningServiceFactory;
import it.polimi.elet.selflet.service.IRunningServiceManager;

/**
 * An implementation of service event handler factory
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class ServiceEventHandlerFactory implements IServiceEventHandlerFactory {

	private final IServiceKnowledge serviceKnowledge;
	private final IGeneralKnowledge generalKnowledge;
	private final IRunningServiceManager runningServiceManager;
	private final IRunningServiceFactory runningServiceFactory;
	private final ISelfLetMsgFactory selfletMsgFactory;
	private final IMessageHandler messageHandler;

	@Inject
	public ServiceEventHandlerFactory(IServiceKnowledge serviceKnowledge, IGeneralKnowledge generalKnowledge, IRunningServiceManager runningServiceManager,
			IRunningServiceFactory runningServiceFactory, ISelfLetMsgFactory selfletMsgFactory, IMessageHandler messageHandler) {
		this.serviceKnowledge = serviceKnowledge;
		this.generalKnowledge = generalKnowledge;
		this.runningServiceManager = runningServiceManager;
		this.runningServiceFactory = runningServiceFactory;
		this.selfletMsgFactory = selfletMsgFactory;
		this.messageHandler = messageHandler;
	}

	@Override
	public IServiceEventHandler create(EventTypeEnum eventType) {

		switch (eventType) {

		case LOCAL_REQUEST_REMOTE_EXECUTION_COMPLETED:
			return new LocalReqRemoteExeCompletedEventHandler(runningServiceManager);

		case LOCAL_REQUEST_LOCAL_EXECUTION_COMPLETED:
			return new LocalReqLocalExeCompletedEventHandler(generalKnowledge);

		case LOCAL_REQUEST_LOCAL_EXECUTION_EXECUTE:
			return new LocalReqLocalExeExecuteEventHandler(serviceKnowledge, runningServiceManager, runningServiceFactory);

		case REMOTE_REQUEST_LOCAL_EXECUTION_COMPLETED:
			return new RemoteReqLocalExeCompletedEventHandler(messageHandler, selfletMsgFactory);

		case REMOTE_REQUEST_LOCAL_EXECUTION_EXECUTE:
			return new RemoteReqLocalExeExecuteEventHandler(serviceKnowledge, runningServiceFactory, runningServiceManager);

		case LOCAL_REQUEST_REMOTE_EXECUTION_EXECUTE:
			return new LocalReqRemoteExeExecuteEventHandler(selfletMsgFactory, messageHandler, runningServiceManager);

		default:
			throw new IllegalArgumentException("Trying to create service event handler for non registered event type: " + eventType);
		}

	}
}
