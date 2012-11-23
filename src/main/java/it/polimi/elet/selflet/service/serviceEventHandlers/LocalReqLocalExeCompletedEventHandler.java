package it.polimi.elet.selflet.service.serviceEventHandlers;

import it.polimi.elet.selflet.events.service.IServiceEvent;
import it.polimi.elet.selflet.events.service.LocalReqLocalExeCompletedEvent;
import it.polimi.elet.selflet.knowledge.IGeneralKnowledge;
import it.polimi.elet.selflet.service.RunningService;

public class LocalReqLocalExeCompletedEventHandler implements IServiceEventHandler {

	private final IGeneralKnowledge generalKnowledge;

	public LocalReqLocalExeCompletedEventHandler(IGeneralKnowledge generalKnowledge) {
		this.generalKnowledge = generalKnowledge;
	}

	@Override
	public void handleEvent(IServiceEvent serviceEvent) {
		LocalReqLocalExeCompletedEvent event = (LocalReqLocalExeCompletedEvent) serviceEvent;
		setOutputInKnowledge(event);
		resumeService(event);
	}

	private void setOutputInKnowledge(LocalReqLocalExeCompletedEvent event) {
		Object output = event.getOutput() == null ? "null" : event.getOutput();
		generalKnowledge.setOrUpdateProperty("output" + event.getServiceName(), output);
	}

	private void resumeService(LocalReqLocalExeCompletedEvent event) {
		RunningService callingService = event.getCallingService();
		if (callingService != null) {
			callingService.resumeService();
		}
	}
}
