package it.polimi.elet.selflet.negotiation;

import it.polimi.elet.selflet.exceptions.NegotiationErrorException;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.service.Service;

/**
 * Interface for service provider searcher
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 */
public interface IServiceProviderSearcher {

	/**
	 * Retrieves a service provider for the given service with a default number
	 * of attemps
	 * 
	 * @param serviceName
	 *            the name of the service
	 * @param askedMode
	 *            the asked mode for the service
	 * */
	ServiceProvider getServiceProvider(String serviceName, ServiceAskModeEnum askedMode);

	/**
	 * Retrieves a provider for the given service. In case there are many
	 * providers, one will be randomly chosen. In case the request fails
	 * <code>broadcastAttempts</code> will be done
	 * 
	 * @param serviceName
	 *            the name of the service
	 * 
	 * @param askedMode
	 *            the asked mode for the service
	 * 
	 * @param broadcastAttempts
	 *            number of broadcast attempts that will be done to search for
	 *            the given provider
	 * 
	 * @throws NegotiationErrorException
	 *             in case a provider cannot be found for this service
	 * */
	ServiceProvider getServiceProvider(String serviceName, ServiceAskModeEnum askedMode, int broadcastAttempts);

	/**
	 * Returns a service provider using the direct (
	 * <code>ServiceOfferModeEnum.Both</code>,
	 * <code>ServiceOfferModeEnum.CanTeach</code> and
	 * <code>ServiceOfferModeEnum.CanDo</code>) and undirect knowledge (
	 * <code>ServiceOfferModeEnum.KnowsWhoCanBoth</code>,
	 * <code>ServiceOfferModeEnum.KnowsWhoCanTeach</code>,
	 * <code>ServiceOfferModeEnum.KnowsWhoCanDo</code>).
	 * <p>
	 * If a service provider cannot be found then
	 * <code>NegotiationErrorException</code>
	 * </p>
	 * */
	ServiceProvider getServiceProviderUsingLocalKnowledge(Service neededService, ServiceAskModeEnum askedMode) throws NegotiationErrorException;

	/**
	 * Contacts the given provider and asks for its provider for the given
	 * service in the given mode
	 * 
	 * @param serviceName
	 *            the service name
	 * 
	 * @param providerId
	 *            the id of the provider to contact
	 * 
	 * @param mode
	 *            the desired interaction mode
	 * 
	 * @throws NegotiationErrorException
	 *             if a remote error occurred
	 * 
	 * @return the desired provider if found, null otherwise
	 */
	ServiceProvider getNeededServiceProviderIndirectly(String serviceName, ISelfLetID providerId, ServiceAskModeEnum mode) throws NegotiationErrorException;

	/**
	 * Asks the other SelfLets for a needed service by broadcasting a request.
	 * 
	 * @param serviceName
	 *            the service name
	 * 
	 * @param mode
	 *            the desired interaction mode
	 * 
	 * @return the service provider found
	 * 
	 * @return true if at least one reply was received, false otherwise
	 */
	ServiceProvider askBroadcastForNeededService(String serviceName, ServiceAskModeEnum mode, int timeout);

}
