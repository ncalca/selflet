package it.polimi.elet.selflet.negotiation;

import it.polimi.elet.selflet.id.ISelfLetID;

import java.io.Serializable;

/**
 * A simple "storage" class to hold an SelfLet ID and a ServiceOfferModeEnum.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * @author Davide Devescovi
 */
public class ServiceProvider implements Serializable {

	private static final long serialVersionUID = 1L;

	private final ISelfLetID providerID;
	private final ServiceOfferModeEnum mode;

	public ServiceProvider(ISelfLetID serviceProviderID, ServiceOfferModeEnum offerMode) {
		this.providerID = serviceProviderID;
		this.mode = offerMode;
	}

	public ISelfLetID getProviderID() {
		return providerID;
	}

	public ServiceOfferModeEnum getOfferMode() {
		return mode;
	}
}