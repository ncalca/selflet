package it.polimi.elet.selflet.negotiation.servicepack;

import it.polimi.elet.selflet.events.IEventDispatcher;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.negotiation.IServicePackInstallerFactory;

import com.google.inject.Inject;

/**
 * A service pack installer factory
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ServicePackInstallerFactory implements IServicePackInstallerFactory {

	private final IEventDispatcher eventDispatcher;
	private final ISelfLetID selfletID;

	@Inject
	public ServicePackInstallerFactory(ISelfLetID selfletID, IEventDispatcher eventDispatcher) {
		this.selfletID = selfletID;
		this.eventDispatcher = eventDispatcher;
	}

	public ServicePackInstaller createInstance() {
		return new ServicePackInstaller(selfletID, eventDispatcher);
	}

}
