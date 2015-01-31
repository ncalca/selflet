package it.polimi.elet.selflet.service.serviceEventHandlers;

import org.apache.log4j.Logger;

import it.polimi.elet.selflet.events.service.IServiceEvent;
import it.polimi.elet.selflet.events.service.LocalReqRemoteExeCompletedEvent;
import it.polimi.elet.selflet.service.IRunningServiceManager;
import it.polimi.elet.selflet.service.RunningService;

public class LocalReqRemoteExeCompletedEventHandler implements IServiceEventHandler {

	private static final Logger LOG = Logger.getLogger("resultsLogger");

	private final IRunningServiceManager runningServiceManager;

	public LocalReqRemoteExeCompletedEventHandler(IRunningServiceManager runningServiceManager) {
		this.runningServiceManager = runningServiceManager;
	}

	@Override
	public void handleEvent(IServiceEvent serviceEvent) {
		long arrivalTime = System.currentTimeMillis();
		LocalReqRemoteExeCompletedEvent event = (LocalReqRemoteExeCompletedEvent) serviceEvent;
		String[] output = ((String)event.getOutput()).split(":");
		long startTime = Long.parseLong(output[0]);
		long responseTime = arrivalTime - startTime;
		LOG.info(arrivalTime +  ",LocalReqRemoteExe," + event.getServiceName() +"," + responseTime + "," + output[1] +",1");
		runningServiceManager.resumePendingService(event.getMessageID());
	}

}
