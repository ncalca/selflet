package it.polimi.elet.selflet.negotiation.servicepack;

import it.polimi.elet.selflet.negotiation.IServicePackFactory;
import it.polimi.elet.selflet.negotiation.ServicePack;
import it.polimi.elet.selflet.service.Service;

/**
 * Factory for service pack
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ServicePackFactory implements IServicePackFactory {

	public ServicePack createServicePackForService(Service service) {
		ServicePack servicePack = new ServicePack(service.getName(), service.getImplementingBehaviors());
		
		servicePack.setDefaultBehavior(service.getDefaultBehavior());
		servicePack.setMaxResponseTime(service.getMaxResponseTimeInMsec());
		
		return servicePack;
	}
}
