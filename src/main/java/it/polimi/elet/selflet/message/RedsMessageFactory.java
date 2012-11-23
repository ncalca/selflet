package it.polimi.elet.selflet.message;

import it.polimi.elet.selflet.knowledge.Neighbor;
import it.polimi.elet.selflet.negotiation.INeighborManager;

import java.util.Set;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * This class is used to create a RedsMessage encapsulating a SelfLetMsg object.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class RedsMessageFactory implements IRedsMessageFactory {

	public static final String BROADCAST = "BROADCAST";

	private final INeighborManager neighborManager;

	@Inject
	public RedsMessageFactory(INeighborManager neighborManager) {
		this.neighborManager = neighborManager;
	}

	public RedsMessage createNewRedsMessage(SelfLetMsg message) {
		if (message == null) {
			throw new IllegalArgumentException("Trying to send a null selflet message");
		}

		return new RedsMessage(message, prepareRecipients(message));
	}

	private Set<String> prepareRecipients(SelfLetMsg message) {
		if (message == null || message.getTo() == null) {
			return Sets.newHashSet();
		}
		// BROADCAST
		if (message.getTo().isBroadcast()) {
			return broadCastRecipients();
		}

		// NEIGHBORS
		if (message.getTo().isNeighbors()) {
			return neighborsRecipients();
		}

		// SINGLE RECIPIENT
		return Sets.newHashSet(message.getTo().toString());
	}

	private Set<String> neighborsRecipients() {
		Set<String> recipients = Sets.newHashSet();
		Set<Neighbor> neighbors = neighborManager.getNeighbors();

		for (Neighbor neighbor : neighbors) {
			recipients.add(neighbor.getId().toString());
		}

		return recipients;
	}

	private Set<String> broadCastRecipients() {
		return Sets.newHashSet(BROADCAST);
	}

}
