package it.polimi.elet.selflet.optimization.actions.redirect;

import it.polimi.elet.selflet.optimization.actions.IOptimizationAction;
import it.polimi.elet.selflet.optimization.actions.OptimizationActionTypeEnum;

import java.util.Set;

/**
 * Action representing a redirect of service requests. It takes as input a list
 * of redirect policies.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class RedirectAction implements IOptimizationAction {

	private final Set<RedirectPolicy> redirectPolicy;
	private final double weight;

	public RedirectAction(Set<RedirectPolicy> redirectPolicy, double weight) {
		this.redirectPolicy = redirectPolicy;
		this.weight = Math.max(weight, MIN_WEIGHT);
	}

	public Set<RedirectPolicy> getRedirectPolicy() {
		return redirectPolicy;
	}

	@Override
	public double weight() {
		return weight;
	}

	@Override
	public String toString() {
		return "Redirect action: " + redirectPolicy.toString();
	}

	@Override
	public OptimizationActionTypeEnum optimizationType() {
		return OptimizationActionTypeEnum.REDIRECT_SERVICE;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((redirectPolicy == null) ? 0 : redirectPolicy.hashCode());
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
		if (!(obj instanceof RedirectAction)) {
			return false;
		}
		RedirectAction other = (RedirectAction) obj;
		if (redirectPolicy == null) {
			if (other.redirectPolicy != null) {
				return false;
			}
		} else if (!redirectPolicy.equals(other.redirectPolicy)) {
			return false;
		}
		return true;
	}

}
