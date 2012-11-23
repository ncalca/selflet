package it.polimi.elet.selflet.service;

/**
 * Storage class containing stats about the execution of services by the running
 * service manager
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ServiceExecutionStats {

	private int activeCount;
	private long completedTaskCount;
	private int queueLength;

	public void setActiveCount(int activeCount) {
		this.activeCount = activeCount;
	}

	public void setCompletedTaskCount(long completedTaskCount) {
		this.completedTaskCount = completedTaskCount;
	}

	public int getActiveCount() {
		return activeCount;
	}

	public long getCompletedTaskCount() {
		return completedTaskCount;
	}

	public int getQueueLength() {
		return queueLength;
	}

	public void setQueueLength(int queueLength) {
		this.queueLength = queueLength;
	}

}
