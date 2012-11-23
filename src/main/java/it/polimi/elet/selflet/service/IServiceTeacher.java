package it.polimi.elet.selflet.service;

import it.polimi.elet.selflet.id.ISelfLetID;

/**
 * Subsystem used to send a service teach message
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IServiceTeacher {

	/**
	 * Teaches the given service to a remote selflet
	 * */
	void teach(Service service, ISelfLetID otherSelflet);

}
