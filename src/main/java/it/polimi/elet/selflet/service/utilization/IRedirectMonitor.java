package it.polimi.elet.selflet.service.utilization;

import it.polimi.elet.selflet.id.ISelfLetID;

/**
 * Monitors requests received through redirects
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IRedirectMonitor {

	
	/**
	 * Stores the request received from the given selflet for service
	 * */
	void addReceivedRedirect(ISelfLetID from, String serviceName);

	/**
	 * Returns true if the given neighbor is performing redirect to this selflet
	 * */
	boolean isNeighborPerformingRedirectToMe(ISelfLetID neighbor, String name);

	
}
