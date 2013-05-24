package it.polimi.elet.selflet.load;

import it.polimi.elet.selflet.utilities.RandomDistributions;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.apache.log4j.Logger;

/**
 * Generates actual load for CPU and DISK
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 */
public class LoadGenerator {

	private static final Logger LOG = Logger.getLogger(LoadGenerator.class);

	private final ThreadMXBean bean = ManagementFactory.getThreadMXBean();;

	private static final String TEMPFILETEST = "./TempFileTest.txt";
	private static final int MAX_PI_ITERATIONS = 100000;
	private static final double BILLION = 1e9;

	private final boolean doCpuTest;
	private final boolean doHdTest;
	private final double averageExecutionTimeInSec;
	private final LoadType distTest;

	/**
	 * Use data extracted from a load profile
	 * */
	public LoadGenerator(LoadProfile loadProfile) {
		this.doCpuTest = true;
		this.doHdTest = false;
		this.distTest = LoadType.Constant;
		this.averageExecutionTimeInSec = loadProfile.extractNewWaitTimeInMillis() / 1000;
	}

	public LoadGenerator(boolean doCpuTest, boolean doHdTest, LoadType loadType, double averageExecutionTimeInSec) {
		this.doCpuTest = doCpuTest;
		this.doHdTest = doHdTest;
		this.distTest = loadType;
		this.averageExecutionTimeInSec = averageExecutionTimeInSec;
	}

	public LoadGenerator(boolean doCpuTest, boolean doHdTest, LoadType loadType, int averageExecutionTimeInSec) {
		this.doCpuTest = doCpuTest;
		this.doHdTest = doHdTest;
		this.distTest = loadType;
		this.averageExecutionTimeInSec = averageExecutionTimeInSec;
	}

	public double runTest() {

		long deltaTime = (long) (getGenerateServiceTimeInSec() * BILLION); // nanoseconds
		long initialThreadExecutionTime = bean.getCurrentThreadCpuTime();

		long currentTestTime;
		long threadExecutionTime;

		do {
			if (doCpuTest) {
				runCpuTest();
			}
			if (doHdTest) {
				runHdTest();
			}
			threadExecutionTime = bean.getCurrentThreadCpuTime();
			currentTestTime = threadExecutionTime - initialThreadExecutionTime;

		} while (currentTestTime <= deltaTime);

		return (currentTestTime - deltaTime);
	}

	// Pi-calculation
	private double runCpuTest() {
		boolean plusMinus = false;
		double pi = 0;
		for (int i = 1; i <= MAX_PI_ITERATIONS; i = i + 2) {
			if (plusMinus) {
				pi -= (4 / (double) i);
				plusMinus = false;
			} else {
				pi += (4 / (double) i);
				plusMinus = true;
			}
		}
		return pi;
	}

	private void runHdTest() {
		File file = new File(TEMPFILETEST);
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file, true);
		} catch (FileNotFoundException e1) {
			LOG.error("Error while opening file " + TEMPFILETEST, e1);
		}

		PrintStream printStream = new PrintStream(fileOutputStream);
		for (int i = 1; i <= 10; i++) {
			printStream.print("a");
		}

		try {
			fileOutputStream.close();
		} catch (IOException e) {
			LOG.error("IO error " + TEMPFILETEST, e);
		} finally {
			file.delete();
			printStream.close();
		}
	}

	private double getGenerateServiceTimeInSec() {
		double serviceTime = 0;
		switch (distTest) {

		case Constant:
			serviceTime = averageExecutionTimeInSec;
			break;

		case Poisson:
			serviceTime = RandomDistributions.randExponential(averageExecutionTimeInSec);
			break;

		case LogNormal:
			serviceTime = averageExecutionTimeInSec;
			break;

		default:
			throw new IllegalArgumentException("Invalid kind of load for load generator");
		}

		return serviceTime;
	}

}