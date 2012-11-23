package it.polimi.elet.selflet.optimization.actions.redirect;

import it.polimi.elet.selflet.id.ISelfLetID;

/**
 * Interface for redirect calculators
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IRedirectCalculator {

	/**
	 * True iff the computed action is a redirect
	 * */
	boolean performRedirect();

	/**
	 * Returns the provider to which the request need to be redirected
	 * */
	ISelfLetID getRedirectedProvider();

}
