package it.polimi.elet.selflet.service.serviceEventHandlers;

import org.apache.log4j.Logger;

import it.polimi.elet.selflet.events.service.IServiceEvent;
import it.polimi.elet.selflet.events.service.RemoteReqLocalExeExecuteEvent;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.service.IRunningServiceFactory;
import it.polimi.elet.selflet.service.IRunningServiceManager;
import it.polimi.elet.selflet.service.RunningService;
import it.polimi.elet.selflet.service.Service;

public class RemoteReqLocalExeExecuteEventHandler implements IServiceEventHandler {

	private static final Logger LOG = Logger.getLogger(RemoteReqLocalExeExecuteEventHandler.class);

	private final IServiceKnowledge serviceKnowledge;
	private final IRunningServiceFactory runningServiceFactory;
	private final IRunningServiceManager runningServiceManager;

	public RemoteReqLocalExeExecuteEventHandler(IServiceKnowledge serviceKnowledge, IRunningServiceFactory runningServiceFactory,
			IRunningServiceManager runningServiceManager) {
		this.serviceKnowledge = serviceKnowledge;
		this.runningServiceFactory = runningServiceFactory;
		this.runningServiceManager = runningServiceManager;
	}

	@Override
	public void handleEvent(IServiceEvent serviceEvent) {
		RemoteReqLocalExeExecuteEvent remoteExecuteEvent = (RemoteReqLocalExeExecuteEvent) serviceEvent;
		String serviceName = remoteExecuteEvent.getServiceName();

		if (!serviceKnowledge.isPropertyPresent(serviceName)) {
			LOG.error("Service '" + serviceName + "' does not exists");
			return;
		}

		SelfLetMsg selfletMsg = remoteExecuteEvent.getMsg();
		Service service = serviceKnowledge.getProperty(serviceName);

		// create new remote running service and start it
		RunningService runningService = runningServiceFactory.createRemoteRunningService(selfletMsg, service);

		LOG.debug("Received request for " + serviceName + ", starting new running service");
		runningServiceManager.startService(runningService);
	}

}
