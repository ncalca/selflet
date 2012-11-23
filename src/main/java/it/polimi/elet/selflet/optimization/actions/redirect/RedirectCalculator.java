package it.polimi.elet.selflet.optimization.actions.redirect;

import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.service.Service;
import it.polimi.elet.selflet.utilities.RandomDistributions;
import static it.polimi.elet.selflet.utilities.MathUtil.*;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * An implementation of redirect calculator
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class RedirectCalculator implements IRedirectCalculator {

	private static final double DELTA = 1e-5;

	private final Set<RedirectPolicy> redirectPolicies;
	private final Service serviceToRedirect;
	private final boolean performRedirect;
	private final ISelfLetID redirectedProvider;
	private final double redirectProbability;

	private double totalProbability;

	/**
	 * @param serviceToRedirect
	 *            The service that need to be redirected
	 * @param redirectPolicies
	 *            The set of policies regulating the redirect
	 * */
	public RedirectCalculator(Service serviceToRedirect, Set<RedirectPolicy> redirectPolicies, double redirectProbability) {

		if (!isValidProbability(redirectProbability)) {
			throw new IllegalArgumentException("Given number is not a probability " + redirectProbability);
		}

		this.serviceToRedirect = serviceToRedirect;
		this.redirectPolicies = Sets.newCopyOnWriteArraySet(redirectPolicies);
		this.redirectProbability = redirectProbability;
		this.performRedirect = computePerformRedirect();
		this.redirectedProvider = computeRedirectProvider();
	}

	private boolean computePerformRedirect() {
		if (redirectPolicies.isEmpty()) {
			return false;
		}
		double randomNumber = RandomDistributions.randUniform();
		return (redirectProbability >= randomNumber);
	}

	@Override
	public boolean performRedirect() {
		return performRedirect;
	}

	@Override
	public ISelfLetID getRedirectedProvider() {
		return redirectedProvider;
	}

	private ISelfLetID computeRedirectProvider() {
		if (!performRedirect) {
			return null;
		}

		removeUnusedPolicies();

		if (isNormalizationNeeded()) {
			normalizePolicies();
		}

		return extractRedirect();
	}

	private ISelfLetID extractRedirect() {

		if (redirectPolicies.isEmpty()) {
			return null;
		}

		if (redirectPolicies.size() == 1) {
			return redirectPolicies.iterator().next().getReceivingSelfletID();
		}

		double randomNumber = RandomDistributions.randUniform();
		double cumulativeProbability = 0d;

		for (RedirectPolicy redirectPolicy : redirectPolicies) {
			cumulativeProbability += redirectPolicy.getRedirectProbability();
			if (randomNumber <= cumulativeProbability) {
				return redirectPolicy.getReceivingSelfletID();
			}
		}

		throw new IllegalStateException("Cannot find a candidate redirected provider. " + redirectPolicies);
	}

	private void removeUnusedPolicies() {
		for (RedirectPolicy redirectPolicy : redirectPolicies) {
			if (!redirectPolicy.isForService(serviceToRedirect)) {
				redirectPolicies.remove(redirectPolicy);
			}
		}
	}

	private void normalizePolicies() {
		Set<RedirectPolicy> newRedirectPolicies = computeNewRedirectPolicies();
		this.redirectPolicies.clear();
		this.redirectPolicies.addAll(newRedirectPolicies);
	}

	private Set<RedirectPolicy> computeNewRedirectPolicies() {
		Set<RedirectPolicy> newRedirectPolicies = Sets.newHashSet();

		for (RedirectPolicy redirectPolicy : redirectPolicies) {
			double newProbability = redirectPolicy.getRedirectProbability() / totalProbability;
			newRedirectPolicies.add(new RedirectPolicy(redirectPolicy, newProbability));
		}

		return newRedirectPolicies;
	}

	private boolean isNormalizationNeeded() {
		totalProbability = 0;
		for (RedirectPolicy redirectPolicy : redirectPolicies) {
			if (redirectPolicy.isForService(serviceToRedirect)) {
				totalProbability += redirectPolicy.getRedirectProbability();
			}
		}
		return !isInRangeInclusive(totalProbability, 1 - DELTA, 1 + DELTA);
	}

}
