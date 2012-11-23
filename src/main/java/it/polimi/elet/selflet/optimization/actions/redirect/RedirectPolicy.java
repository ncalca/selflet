package it.polimi.elet.selflet.optimization.actions.redirect;

import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.service.Service;

import static it.polimi.elet.selflet.utilities.MathUtil.*;

/**
 * A redirect policy is a triplet indicating for each service where to redirect
 * requests and with which probability.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class RedirectPolicy {

	private final String serviceName;
	private final ISelfLetID receivingSelfletID;
	private final double redirectProbability;

	public RedirectPolicy(String serviceName, ISelfLetID receivingSelfletID, double redirectProbability) {
		if (!isValidProbability(redirectProbability)) {
			throw new IllegalArgumentException("Passed number is not a valid probability: " + redirectProbability);
		}
		this.serviceName = serviceName;
		this.receivingSelfletID = receivingSelfletID;
		this.redirectProbability = redirectProbability;
	}

	/**
	 * Takes as input an existing redirect policy and creates a new redirect
	 * policy with the same characteristics and a different redirect probability
	 * */
	public RedirectPolicy(RedirectPolicy redirectPolicy, double redirectProbability) {
		if (!isValidProbability(redirectProbability)) {
			throw new IllegalArgumentException("Passed number is not a valid probability: " + redirectProbability);
		}
		this.serviceName = redirectPolicy.serviceName;
		this.receivingSelfletID = redirectPolicy.receivingSelfletID;
		this.redirectProbability = redirectProbability;
	}

	public String getServiceName() {
		return serviceName;
	}

	public ISelfLetID getReceivingSelfletID() {
		return receivingSelfletID;
	}

	public double getRedirectProbability() {
		return redirectProbability;
	}

	public boolean isForService(Service service) {
		return serviceName.equals(service.getName());
	}

	@Override
	public String toString() {
		return "Redirect " + serviceName + " to " + receivingSelfletID + " with probability " + redirectProbability;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((receivingSelfletID == null) ? 0 : receivingSelfletID.hashCode());
		long temp;
		temp = Double.doubleToLongBits(redirectProbability);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		if (!(obj instanceof RedirectPolicy)) {
			return false;
		}
		RedirectPolicy other = (RedirectPolicy) obj;
		if (receivingSelfletID == null) {
			if (other.receivingSelfletID != null) {
				return false;
			}
		} else if (!receivingSelfletID.equals(other.receivingSelfletID)) {
			return false;
		}
		if (Double.doubleToLongBits(redirectProbability) != Double.doubleToLongBits(other.redirectProbability)) {
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

}
