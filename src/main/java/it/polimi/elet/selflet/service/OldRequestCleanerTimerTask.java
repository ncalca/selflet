package it.polimi.elet.selflet.service;

import it.polimi.elet.selflet.configuration.SelfletConfiguration;
import it.polimi.elet.selflet.threadUtilities.IPeriodicTask;

import java.util.TimerTask;

import com.google.inject.Inject;

/**
 * Periodically invokes the running service manager to remove old requests
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class OldRequestCleanerTimerTask extends TimerTask implements IPeriodicTask {

	public static final int SLEEP_TIME = SelfletConfiguration.getSingleton().requestCleanerPeriodInSec;

	private IRunningServiceManager runningServiceManager;

	@Inject
	public OldRequestCleanerTimerTask(IRunningServiceManager runningServiceManager) {
		this.runningServiceManager = runningServiceManager;
	}

	@Override
	public int periodInMsec() {
		return SLEEP_TIME * 1000;
	}

	@Override
	public void run() {
		runningServiceManager.cleanOldRequests();
	}

}
