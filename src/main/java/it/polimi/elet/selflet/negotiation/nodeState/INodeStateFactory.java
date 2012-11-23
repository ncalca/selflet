package it.polimi.elet.selflet.negotiation.nodeState;

/**
 * Interface to create node states. It sets all the necessary information of the
 * node state
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface INodeStateFactory {

	/**
	 * Returns a new node state containing information about this node
	 * */
	INodeState createNodeState();

}
