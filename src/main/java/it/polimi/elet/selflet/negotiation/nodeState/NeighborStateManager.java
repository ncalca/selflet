package it.polimi.elet.selflet.negotiation.nodeState;

import it.polimi.elet.selflet.configuration.SelfletConfiguration;
import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletComponent;
import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.knowledge.Neighbor;
import it.polimi.elet.selflet.negotiation.INeighborManager;
import it.polimi.elet.selflet.service.Service;
import it.polimi.elet.selflet.utilities.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * An implementation for neighbor state manager
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 **/
@Singleton
public class NeighborStateManager extends SelfletComponent implements INeighborStateManager {

	private static final long MAX_STATE_AGE_IN_SEC = SelfletConfiguration.getSingleton().maxNeighborAgeInSec;

	private final INeighborManager neighborManager;
	private final Cache<ISelfLetID, INodeState> neighborStateCache;

	@Inject
	public NeighborStateManager(INeighborManager neighborManager) {
		this.neighborStateCache = CacheBuilder.newBuilder().expireAfterWrite(MAX_STATE_AGE_IN_SEC, TimeUnit.SECONDS).build();
		this.neighborManager = neighborManager;
	}

	@Override
	public void addNeighborState(INodeState nodeState) {
		neighborStateCache.put(nodeState.getSelfletID(), nodeState);
		neighborManager.addNeighbor(new Neighbor(nodeState.getSelfletID()));
	}

	@Override
	public Neighbor getNeighbor(ISelfLetID selfletID) {

		Set<Neighbor> neighbors = getNeighbors();
		for (Neighbor neighbor : neighbors) {
			if (neighbor.getId().equals(selfletID)) {
				return neighbor;
			}
		}

		throw new NotFoundException("Node " + selfletID + " not found");
	}

	@Override
	public Set<Neighbor> getNeighbors() {
		return neighborManager.getNeighbors();
	}

	@Override
	public void removeNeighbor(ISelfLetID neighborId) {
		neighborManager.removeNeighbor(neighborId);
	}

	@Override
	public boolean isNeighborOfferingService(Neighbor neighbor, Service service) {

		INodeState nodeState = neighborStateCache.getIfPresent(neighbor.getId());

		if (nodeState == null) {
			return false;
		}

		return nodeState.getAvailableServices().contains(service.getName());
	}

	@Override
	public INodeState getNodeStateOfNeighbor(Neighbor neighbor) {

		if (neighbor == null) {
			throw new NotFoundException("Neighbor is null");
		}

		return getNodeStateOfNeighbor(neighbor.getId());

	}

	@Override
	public INodeState getNodeStateOfNeighbor(ISelfLetID selfletID) {
		INodeState nodeState = neighborStateCache.getIfPresent(selfletID);
		if (nodeState != null)
			return nodeState;
		else {
			throw new NotFoundException("Cannot find state for neighbor with id " + selfletID);
		}
	}

	@Override
	public boolean haveInformationAboutNeighbor(Neighbor neighbor) {
		return (neighborStateCache.getIfPresent(neighbor.getId()) != null);
	}

	@Override
	public ImmutableSet<EventTypeEnum> getReceivedEvents() {
		return ImmutableSet.of();
	}

	@Override
	public ISelfLetID getNeighborHavingService(String serviceName) {
		List<ISelfLetID> neighborsWithService = Lists.newArrayList();

		for (INodeState nodeState : neighborStateCache.asMap().values()) {
			if (nodeState.getAvailableServices().contains(serviceName)) {
				neighborsWithService.add(nodeState.getSelfletID());
			}
		}

		if (neighborsWithService.isEmpty()) {
			throw new NotFoundException("Cannot find provider for service " + serviceName + " among those offered by neighbors");
		}

		return CollectionUtils.randomElement(neighborsWithService);
	}
}
