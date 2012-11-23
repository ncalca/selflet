package it.polimi.elet.selflet.lifecycle;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import it.polimi.elet.selflet.message.IMessageHandler;
import it.polimi.elet.selflet.negotiation.INegotiationManager;
import it.polimi.elet.selflet.service.IRunningServiceManager;
import it.polimi.elet.selflet.threadUtilities.PeriodicThreadStarter;

/**
 * Implementation of selflet shutdown interface
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class SelfletShutdown implements ISelfletShutdown {

	private static final Logger LOG = Logger.getLogger(SelfletShutdown.class);

	private static final long SLEEP_BEFORE_KILL = 60 * 1000;

	private final INegotiationManager negotiationManager;
	private final IMessageHandler messageHandler;
	private final IRunningServiceManager runningServiceManager;

	private PeriodicThreadStarter periodicThreadsStarter;

	@Inject
	public SelfletShutdown(INegotiationManager negotiationManager, IMessageHandler messageHandler, IRunningServiceManager runningServiceManager) {
		this.negotiationManager = negotiationManager;
		this.messageHandler = messageHandler;
		this.runningServiceManager = runningServiceManager;
	}

	@Override
	public void shutDown() {
		LOG.warn("Notifying dispatcher and removing selflet");
		negotiationManager.notifySelfletRemoval();
		periodicThreadsStarter.stop();
		while (runningServiceManager.getServiceExecutionStats().getActiveCount() > 0) {
			sleep();
		}
//		messageHandler.disconnect();
		
		// TODO for now brutally kill this selflet
//		System.exit(0);
	}

	private void sleep() {
		try {
			Thread.sleep(SLEEP_BEFORE_KILL);
		} catch (InterruptedException e) {
			LOG.error(e);
		}
	}

	@Override
	public void addPeriodicThreadStarter(PeriodicThreadStarter periodicThreadsStarter) {
		this.periodicThreadsStarter = periodicThreadsStarter;
	}

}
