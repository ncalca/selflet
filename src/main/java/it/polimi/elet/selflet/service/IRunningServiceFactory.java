package it.polimi.elet.selflet.service;

import it.polimi.elet.selflet.message.SelfLetMsg;

/**
 * Factory to create instances of various type of running services
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IRunningServiceFactory {

	RunningService createRemoteRunningService(SelfLetMsg msg, Service service);

	RunningService createLocalRunningService(Service service, RunningService callingService);

}
