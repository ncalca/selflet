package it.polimi.elet.selflet.negotiation;

import it.polimi.elet.selflet.service.Service;

/**
 * Factory for service packs
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IServicePackFactory {

	ServicePack createServicePackForService(Service service);

}
