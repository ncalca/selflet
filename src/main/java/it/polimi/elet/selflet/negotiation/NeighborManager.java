package it.polimi.elet.selflet.negotiation;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;
import com.google.inject.Singleton;

import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.knowledge.Neighbor;

/**
 * An implementation of neighbor manager
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class NeighborManager implements INeighborManager {

	private static final int MAX_NEIGHBOR_AGE = 7;

	private Cache<ISelfLetID, Neighbor> neighborCache;

	public NeighborManager() {
		neighborCache = CacheBuilder.newBuilder().expireAfterWrite(MAX_NEIGHBOR_AGE, TimeUnit.SECONDS).build();
	}

	@Override
	public void addNeighbor(Neighbor neighbor) {
		ISelfLetID neighborId = neighbor.getId();
		neighborCache.put(neighborId, neighbor);
	}

	@Override
	public Set<Neighbor> getNeighbors() {
		return Sets.newHashSet(neighborCache.asMap().values());
	}

	@Override
	public void addNeighbors(Set<Neighbor> neighbors) {
		for (Neighbor neighbor : neighbors) {
			addNeighbor(neighbor);
		}
	}

	@Override
	public void removeNeighbor(ISelfLetID neighborToRemove) {
		neighborCache.invalidate(neighborToRemove);
	}

}
