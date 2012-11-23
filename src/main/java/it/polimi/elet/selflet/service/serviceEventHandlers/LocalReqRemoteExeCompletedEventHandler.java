package it.polimi.elet.selflet.service.serviceEventHandlers;

import org.apache.log4j.Logger;

import it.polimi.elet.selflet.events.service.IServiceEvent;
import it.polimi.elet.selflet.events.service.LocalReqRemoteExeCompletedEvent;
import it.polimi.elet.selflet.service.IRunningServiceManager;

public class LocalReqRemoteExeCompletedEventHandler implements IServiceEventHandler {

	private static final Logger LOG = Logger.getLogger(LocalReqRemoteExeCompletedEventHandler.class);

	private final IRunningServiceManager runningServiceManager;

	public LocalReqRemoteExeCompletedEventHandler(IRunningServiceManager runningServiceManager) {
		this.runningServiceManager = runningServiceManager;
	}

	@Override
	public void handleEvent(IServiceEvent serviceEvent) {
		LocalReqRemoteExeCompletedEvent event = (LocalReqRemoteExeCompletedEvent) serviceEvent;
		LOG.debug("Awakening service with message ID " + event.getMessageID());
		runningServiceManager.resumePendingService(event.getMessageID());
	}

}
