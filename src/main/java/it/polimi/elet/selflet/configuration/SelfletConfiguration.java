package it.polimi.elet.selflet.configuration;

/**
 * Class containing all the parameters for the selflet
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 */
public class SelfletConfiguration extends Configuration {

	private SelfletConfiguration() {
		// private singleton
	}

	// singleton instance
	private static volatile SelfletConfiguration singleton;


	public int minimumTimeBetweenTwoRemovalActionsInSec;

	public long maxNeighborAgeInSec;
	public long minimumTimeToRemoveSelfletInSec;
	public long billTime;
	public int minimumTimeBetweenNewSelfletActionsInSec;

	public double utilizationLowerBound;
	public double utilizationUpperBound;
	public double utilizationForRemoteService;
	public boolean useDynamicStrategy;
	public String availableActions;

	public int requestRateWindowMonitorInSec;
	public int max_response_time_age_in_sec;
	public int optimizationManagerSleepTimeInSec;
	public double remoteServiceExecutionTimeInSec;
	public int aliveMessagePeriodInSec;
	public int periodicLoggerIntervalInSec;
	public int initialDelayAfterSelfletInitializationInMs;
	public int neighborUpdateManagerIntervalInSec;
	public int utilizationCheckerPeriodInSec;

	/* Service execution parameters */
	public int maxServiceExecutionTimeInSec;
	public int serviceThreadPoolSize;
	public int genericThreadPoolSize;
	public int requestCleanerPeriodInSec;

	public String loadProfileTrafficMix;

	public static SelfletConfiguration getSingleton() {
		if (singleton == null) {
			singleton = new SelfletConfiguration();
		}
		return singleton;
	}

}
