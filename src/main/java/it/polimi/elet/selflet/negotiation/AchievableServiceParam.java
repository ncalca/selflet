package it.polimi.elet.selflet.negotiation;

import it.polimi.elet.selflet.id.ISelfLetID;

import java.io.Serializable;

/**
 * Contains information about a service advertised by a selflet
 * 
 * @author silvia
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 */
public class AchievableServiceParam implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String achievableService;
	private final ServiceOfferModeEnum mode;
	private final ISelfLetID providerId;

	public AchievableServiceParam(String achievableService, ServiceOfferModeEnum mode, ISelfLetID providerId) {
		this.achievableService = achievableService;
		this.mode = mode;
		this.providerId = providerId;
	}

	public String getAchievableService() {
		return achievableService;
	}

	public ServiceOfferModeEnum getMode() {
		return mode;
	}

	public ISelfLetID getProviderId() {
		return providerId;
	}
}
