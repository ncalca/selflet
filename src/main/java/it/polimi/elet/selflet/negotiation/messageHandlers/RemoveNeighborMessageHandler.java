package it.polimi.elet.selflet.negotiation.messageHandlers;

import it.polimi.elet.selflet.message.SelfLetMessageTypeEnum;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.negotiation.INeighborManager;
import it.polimi.elet.selflet.negotiation.nodeState.INeighborStateManager;

/**
 * Receives REMOVE_SELFLET messages
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class RemoveNeighborMessageHandler implements ISelfletMessageHandler {

	private final INeighborManager neighborManager;
	private final INeighborStateManager neighborStateManager;

	public RemoveNeighborMessageHandler(INeighborManager neighborManager, INeighborStateManager neighborStateManager) {
		this.neighborManager = neighborManager;
		this.neighborStateManager = neighborStateManager;
	}

	@Override
	public void handleMessage(SelfLetMsg message) {
		neighborManager.removeNeighbor(message.getFrom());
		neighborStateManager.removeNeighbor(message.getFrom());
	}

	@Override
	public SelfLetMessageTypeEnum getTypeOfHandledMessage() {
		return SelfLetMessageTypeEnum.REMOVE_SELFLET;
	}

}
