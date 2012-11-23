package it.polimi.elet.selflet.service.utilization;

import it.polimi.elet.selflet.configuration.SelfletConfiguration;
import it.polimi.elet.selflet.threadUtilities.IPeriodicTask;

import java.util.TimerTask;

import com.google.inject.Inject;

/**
 * Completely fill this class
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class UtilizationCheckerTimerTask extends TimerTask implements IPeriodicTask {

	private static final int SLEEP_TIME_MSEC = SelfletConfiguration.getSingleton().utilizationCheckerPeriodInSec * 1000;

	@Inject
	public UtilizationCheckerTimerTask() {

	}

	public void run() {

	}

	@Override
	public int periodInMsec() {
		return SLEEP_TIME_MSEC;
	}

}
