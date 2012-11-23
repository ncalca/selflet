package it.polimi.elet.selflet.utilities;

import it.polimi.elet.selflet.configuration.SelfletConfiguration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

/**
 * Utility class for threads
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ThreadUtilities {

	private static final Logger LOG = Logger.getLogger(ThreadUtilities.class);

	private ThreadUtilities() {
		// private constructor
	}

	private static final int GENERIC_THREAD_POOL_SIZE = SelfletConfiguration.getSingleton().genericThreadPoolSize;
	private static final int GENERIC_THREAD_POOL_PRIORITY = 5;

	private static final ExecutorService GENERIC_THREAD_POOL = Executors.newFixedThreadPool(GENERIC_THREAD_POOL_SIZE, new PriorityThreadFactory(
			GENERIC_THREAD_POOL_PRIORITY, "Generic Thread"));

	/**
	 * Submits a thread to the generic thread pool
	 * */
	public static void submitGenericJob(Runnable job) {
		GENERIC_THREAD_POOL.execute(job);
	}

	public static void sleepForSeconds(int seconds) {
		int SLEEP_TIME = seconds * 1000;
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			LOG.error(e);
		}

	}
}
