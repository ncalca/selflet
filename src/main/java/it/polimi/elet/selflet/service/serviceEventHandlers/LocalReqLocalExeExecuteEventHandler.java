package it.polimi.elet.selflet.service.serviceEventHandlers;

import org.apache.log4j.Logger;

import it.polimi.elet.selflet.events.service.IServiceEvent;
import it.polimi.elet.selflet.events.service.LocalReqLocalExeExecuteEvent;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.service.IRunningServiceFactory;
import it.polimi.elet.selflet.service.IRunningServiceManager;
import it.polimi.elet.selflet.service.RunningService;
import it.polimi.elet.selflet.service.Service;

public class LocalReqLocalExeExecuteEventHandler implements IServiceEventHandler {

	private static final Logger LOG = Logger.getLogger(LocalReqLocalExeExecuteEventHandler.class);

	private final IServiceKnowledge serviceKnowledge;
	private final IRunningServiceFactory runningServiceFactory;
	private final IRunningServiceManager runningServiceManager;

	public LocalReqLocalExeExecuteEventHandler(IServiceKnowledge serviceKnowledge, IRunningServiceManager runningServiceManager,
			IRunningServiceFactory runningServiceFactory) {
		this.serviceKnowledge = serviceKnowledge;
		this.runningServiceManager = runningServiceManager;
		this.runningServiceFactory = runningServiceFactory;
	}

	@Override
	public void handleEvent(IServiceEvent serviceEvent) {

		LocalReqLocalExeExecuteEvent serviceExecuteEvent = (LocalReqLocalExeExecuteEvent) serviceEvent;
		String serviceName = serviceExecuteEvent.getServiceName();

		if (!serviceKnowledge.isPropertyPresent(serviceName)) {
			LOG.error("Service '" + serviceName + "' does not exists");
			return;
		}

		Service service = serviceKnowledge.getProperty(serviceName);
		RunningService callingService = serviceExecuteEvent.getCallingService();
		
		// create new local running service and start it
		RunningService runningService = runningServiceFactory.createLocalRunningService(service, callingService);

		LOG.debug("Starting internal service " + serviceName);

		// start the job
		runningServiceManager.startService(runningService);
	}

}
