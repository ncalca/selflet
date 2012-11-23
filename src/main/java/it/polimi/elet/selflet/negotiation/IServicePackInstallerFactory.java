package it.polimi.elet.selflet.negotiation;

import it.polimi.elet.selflet.negotiation.servicepack.ServicePackInstaller;

/**
 * Interface producing classes to install service packs after a service teach
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IServicePackInstallerFactory {

	/**
	 * Create a new instance of a <code>ServicePackInstaller</code>
	 * */
	ServicePackInstaller createInstance();

}
