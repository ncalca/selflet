package it.polimi.elet.selflet.negotiation;

import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.knowledge.Neighbor;

import java.util.Set;

/**
 * Contains all nieghbors
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface INeighborManager {

	/**
	 * Returns a set of all known neighbors
	 * */
	Set<Neighbor> getNeighbors();

	/**
	 * Adds a single neighbor to the set of known neighbors by the neighbor
	 * manager
	 * */
	void addNeighbor(Neighbor neighbor);

	/**
	 * Adds the set of neighbors to the set of known neighbors by the neighbor
	 * manager
	 * */
	void addNeighbors(Set<Neighbor> neighbors);

	/**
	 * Removes the neighbor from the set of known neighbors
	 * */
	void removeNeighbor(ISelfLetID neighborId);

}
