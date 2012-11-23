package it.polimi.elet.selflet.events.service;

import it.polimi.elet.selflet.events.ISelfletEvent;

public interface IServiceEvent extends ISelfletEvent {

	boolean isRequestEvent();

	boolean isLocalExecutedService();

	String getServiceName();
}
