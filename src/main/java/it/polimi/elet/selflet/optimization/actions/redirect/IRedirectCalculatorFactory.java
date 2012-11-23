package it.polimi.elet.selflet.optimization.actions.redirect;

import it.polimi.elet.selflet.service.Service;

import java.util.Set;

/**
 * A factory to produce redirect calculators
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IRedirectCalculatorFactory {

	/**
	 * Returns a new redirect calculator based on the given policies for the
	 * given service
	 * */
	IRedirectCalculator create(Service service, Set<RedirectPolicy> redirectPolicies);

}
