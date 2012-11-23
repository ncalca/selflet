package it.polimi.elet.selflet.service.utilization;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Contains an history of monitored consumption and computes an weighted average
 * on historic values
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
class MonitoredConsumption {

	private static final int HISTORY_SIZE = 4;
	private double[] weights = { 0.4, 0.3, 0.2, 0.1 };
	// they must sum up to 1

	private final List<Double> CPUTimeHistory = Lists.newArrayList();
	private final List<Double> wallTimeHistory = Lists.newArrayList();

	/**
	 * Returns the CPU time of this monitored values
	 * */
	public double getCPUTime() {
		double weightedAvg = computeWeightedAverage(CPUTimeHistory);
		addToHistory(CPUTimeHistory, weightedAvg);
		return weightedAvg;
	}

	/**
	 * Returns the wall time of this monitored values
	 * */
	public double getWallTime() {
		double weightedAvg = computeWeightedAverage(wallTimeHistory);
		addToHistory(wallTimeHistory, weightedAvg);
		return weightedAvg;
	}

	/**
	 * Adds the couple of values of the history of monitored values
	 * */
	public void addToHistory(double totalCPUTime, double totalWallTime) {
//		if (totalCPUTime <= 0 || totalWallTime <= 0) {
//			throw new IllegalArgumentException("Wall time or CPU time is negative");
//		}

		addToHistory(CPUTimeHistory, totalCPUTime);
		addToHistory(wallTimeHistory, totalWallTime);
	}

	private double computeWeightedAverage(List<Double> history) {
		double weightedAvg = 0;
		for (int i = 0; i < history.size(); i++) {
			weightedAvg += history.get(i) * weights[i];
		}
		return weightedAvg;
	}

	private void addToHistory(List<Double> array, double value) {

		if (array.isEmpty()) {
			// not yet initialized -> put the same value for all history
			for (int i = 0; i < HISTORY_SIZE; i++) {
				array.add(value);
			}
			return;
		}

		for (int i = HISTORY_SIZE - 1; i > 0; i--) {
			array.set(i, array.get(i - 1));
		}

		array.set(0, value);
	}

}
