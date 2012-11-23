package it.polimi.elet.selflet.negotiation.messageHandlers;

import org.apache.log4j.Logger;

import it.polimi.elet.selflet.message.SelfLetMessageTypeEnum;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.negotiation.nodeState.INeighborStateManager;
import it.polimi.elet.selflet.negotiation.nodeState.INodeState;

/**
 * Receives and analyzes node states
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class NodeStateMessageHandler implements ISelfletMessageHandler {

	private static final Logger LOG = Logger.getLogger(NodeStateMessageHandler.class);

	private final INeighborStateManager neighborStateManager;

	public NodeStateMessageHandler(INeighborStateManager neighborStateManager) {
		this.neighborStateManager = neighborStateManager;
	}

	@Override
	public void handleMessage(SelfLetMsg message) {
		INodeState nodeState = (INodeState) message.getContent();
		LOG.debug("Received state " + nodeState);
		neighborStateManager.addNeighborState(nodeState);
	}

	@Override
	public SelfLetMessageTypeEnum getTypeOfHandledMessage() {
		return SelfLetMessageTypeEnum.NODE_STATE;
	}

}
