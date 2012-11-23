package it.polimi.elet.selflet.negotiation.messageHandlers;

import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.knowledge.Neighbor;
import it.polimi.elet.selflet.message.SelfLetMessageTypeEnum;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.negotiation.INeighborManager;

/**
 * An implementation for message handler receiving NEIGHBORS kind of messages
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class NeighborsMessageHandler implements ISelfletMessageHandler {

	private static final Logger LOG = Logger.getLogger(NeighborsMessageHandler.class);

	private final INeighborManager neighborManager;

	public NeighborsMessageHandler(INeighborManager neighborManager) {
		this.neighborManager = neighborManager;
	}

	@Override
	public void handleMessage(SelfLetMsg message) {
		@SuppressWarnings("unchecked")
		Set<ISelfLetID> receivedNeighbors = (Set<ISelfLetID>) message.getContent();
		LOG.debug("Received new neighbors set: " + receivedNeighbors);
		addNeighbors(receivedNeighbors);
	}

	private void addNeighbors(Set<ISelfLetID> receivedNeighbors) {
		
		Set<Neighbor> neighbors = Sets.newHashSet();
		for (ISelfLetID selfletID : receivedNeighbors) {
			neighbors.add(new Neighbor(selfletID));
		}
		neighborManager.addNeighbors(neighbors);
	}

	@Override
	public SelfLetMessageTypeEnum getTypeOfHandledMessage() {
		return SelfLetMessageTypeEnum.NEIGHBORS;
	}

}
