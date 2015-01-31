package it.polimi.elet.selflet.service.serviceEventHandlers;

import org.apache.log4j.Logger;

import it.polimi.elet.selflet.SelfletInstance;
import it.polimi.elet.selflet.events.service.IServiceEvent;
import it.polimi.elet.selflet.events.service.LocalReqLocalExeCompletedEvent;
import it.polimi.elet.selflet.knowledge.IGeneralKnowledge;
import it.polimi.elet.selflet.service.RunningService;

public class LocalReqLocalExeCompletedEventHandler implements
		IServiceEventHandler {

	private static final Logger LOG = Logger.getLogger("resultsLogger");

	private final IGeneralKnowledge generalKnowledge;

	public LocalReqLocalExeCompletedEventHandler(
			IGeneralKnowledge generalKnowledge) {
		this.generalKnowledge = generalKnowledge;
	}

	@Override
	public void handleEvent(IServiceEvent serviceEvent) {
		long arrivalTime = System.currentTimeMillis();
		LocalReqLocalExeCompletedEvent event = (LocalReqLocalExeCompletedEvent) serviceEvent;
		long responseTime = arrivalTime
				- event.getRunningService().getServiceCreationTime();
		LOG.info(System.currentTimeMillis() + ",LocalReqLocalExe,"
				+ event.getServiceName() + "," + responseTime + ","
				+ SelfletInstance.myID + ",1");
		setOutputInKnowledge(event);
		resumeService(event);
	}

	private void setOutputInKnowledge(LocalReqLocalExeCompletedEvent event) {
		Object output = event.getOutput() == null ? "null" : event.getOutput();
		generalKnowledge.setOrUpdateProperty("output" + event.getServiceName(),
				output);
	}

	private void resumeService(LocalReqLocalExeCompletedEvent event) {
		RunningService callingService = event.getCallingService();
		if (callingService != null) {
			callingService.resumeService();
		}
	}
}
