package it.polimi.elet.selflet.negotiation.nodeState;

import it.polimi.elet.selflet.events.ISelfletComponent;
import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.knowledge.Neighbor;
import it.polimi.elet.selflet.negotiation.nodeState.INodeState;
import it.polimi.elet.selflet.service.Service;

import java.util.Set;

/**
 * This interface contains all the operations related to the management of
 * neighbors
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface INeighborStateManager extends ISelfletComponent {

	/**
	 * @return a <code>Set</code> containing all the neighbors
	 * */
	Set<Neighbor> getNeighbors();

	/**
	 * Get the information about a specific neighbor
	 * 
	 * @param the
	 *            neighbor Id
	 * */
	Neighbor getNeighbor(ISelfLetID neighborID);

	/**
	 * Update the current knowledge about a specific neighbor given its new
	 * state
	 * 
	 * @param nodeState
	 *            the new neighbor state
	 * */
	void addNeighborState(INodeState nodeState);

	/**
	 * Remove the knowledge about the given neighbor. For example it can be used
	 * when a neighbor goes down
	 * 
	 * @param selfletID
	 *            the neighbor id to eliminate
	 * */
	void removeNeighbor(ISelfLetID selfletID);

	/**
	 * Returns the node state given a neighbor
	 * */
	INodeState getNodeStateOfNeighbor(Neighbor neighbor);

	/**
	 * Returns the node state given a selfletid
	 * */
	INodeState getNodeStateOfNeighbor(ISelfLetID selfletID);

	/**
	 * Returns true if the given neighbor is offering the given service
	 * 
	 * @param service
	 * @param neighbor
	 * */
	boolean isNeighborOfferingService(Neighbor neighbor, Service service);

	/**
	 * Returns true if have node state of neighbor
	 * 
	 * @param neighbor
	 * */
	boolean haveInformationAboutNeighbor(Neighbor neighbor);

	/**
	 * Returns a neighbor having the given service
	 * 
	 * @throws NotFoundException
	 *             if no neighbor is offering that service
	 * */
	ISelfLetID getNeighborHavingService(String service);

}
