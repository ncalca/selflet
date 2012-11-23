package it.polimi.elet.selflet.service.utilization;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Singleton;

import it.polimi.elet.selflet.id.ISelfLetID;

/**
 * Implementation of redirect monitor
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class RedirectMonitor implements IRedirectMonitor {

	private static final int MAX_REDIRECT_AGE = 30;
	private Cache<RedirectRequest, RedirectRequest> redirectCache;

	public RedirectMonitor() {
		redirectCache = CacheBuilder.newBuilder().expireAfterWrite(MAX_REDIRECT_AGE, TimeUnit.SECONDS).build();
	}

	@Override
	public void addReceivedRedirect(ISelfLetID from, String serviceName) {
		RedirectRequest redirectRequest = new RedirectRequest(from, serviceName);
		redirectCache.put(redirectRequest, redirectRequest);
	}

	@Override
	public boolean isNeighborPerformingRedirectToMe(ISelfLetID neighbor, String serviceName) {
		RedirectRequest redirectRequest = new RedirectRequest(neighbor, serviceName);
		// if different from null then neighbor is doing redirect
		return (redirectCache.getIfPresent(redirectRequest) != null);
	}

	/**
	 * Inner class
	 * */
	class RedirectRequest {

		private final ISelfLetID from;
		private final String serviceName;

		RedirectRequest(ISelfLetID from, String serviceName) {
			this.from = from;
			this.serviceName = serviceName;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((from == null) ? 0 : from.hashCode());
			result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof RedirectRequest)) {
				return false;
			}
			RedirectRequest other = (RedirectRequest) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (from == null) {
				if (other.from != null) {
					return false;
				}
			} else if (!from.equals(other.from)) {
				return false;
			}
			if (serviceName == null) {
				if (other.serviceName != null) {
					return false;
				}
			} else if (!serviceName.equals(other.serviceName)) {
				return false;
			}
			return true;
		}

		private RedirectMonitor getOuterType() {
			return RedirectMonitor.this;
		}

	}

}
