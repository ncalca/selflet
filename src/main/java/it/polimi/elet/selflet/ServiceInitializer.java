package it.polimi.elet.selflet;

import it.polimi.elet.selflet.action.IActionFactory;
import it.polimi.elet.selflet.behavior.EMFBehaviorBuilder;
import it.polimi.elet.selflet.behavior.IBehavior;
import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.negotiation.ServiceOfferModeEnum;
import it.polimi.elet.selflet.service.Service;
import it.polimi.elet.selflet.utilities.StringToClassConversion;
import it.polimi.elet.selflet.utilities.UtilitiesProvider;
import it.polimi.elet.selflet.utilities.parser.ParamType;
import it.polimi.elet.selflet.utilities.parser.ServiceType;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class ServiceInitializer {

	private static final Logger LOG = Logger.getLogger(ServiceInitializer.class);

	private final IServiceKnowledge serviceKnowledge;
	private final IActionFactory actionFactory;

	public ServiceInitializer(IServiceKnowledge serviceKnowledge, IActionFactory actionFactory) {
		this.serviceKnowledge = serviceKnowledge;
		this.actionFactory = actionFactory;
	}

	/**
	 * Registering services
	 * */
	public void addServices(List<ServiceType> services) {

		SelfLetParser selfletParser = new SelfLetParser(UtilitiesProvider.getWorkingDir());

		for (ServiceType serviceType : services) {
			String serviceName = serviceType.getName();
			LOG.debug("Adding service " + serviceName + "...");
			addKnownService(serviceName, serviceType);
			ServiceOfferModeEnum mode = ServiceOfferModeEnum.valueOf(serviceType.getOfferMode().value());
			Boolean active = serviceType.isActive() == null ? false : true;
			setServiceOfferMode(serviceName, mode, active);

			List<File> behaviorFiles = selfletParser.parseBehaviorOfService(serviceName);
			loadBehaviors(serviceName, behaviorFiles);
		}

	}

	private void setServiceOfferMode(String serviceName, ServiceOfferModeEnum mode, Boolean active) {
		Service service = serviceKnowledge.getProperty(serviceName);

		if (service == null) {
			throw new NotFoundException("Couldn't find service " + serviceName);
		}
		service.setLocalOfferMode(mode, active);
	}

	/**
	 * Sets the ask mode of a certain Service.
	 * 
	 * @param serviceName
	 *            the name of the service
	 * 
	 * @param serviceType
	 *            object retrieve from the XML parsing
	 * 
	 */
	public void addKnownService(String serviceName, ServiceType serviceType) {

		Service service = new Service(serviceName);
		List<ParamType> input = serviceType.getInput().getParam();

		Map<String, Class<?>> inputParameters = new HashMap<String, Class<?>>();

		for (ParamType param : input) {
			Class<?> classType = StringToClassConversion.convert(param.getType());
			inputParameters.put(param.getName(), classType);
		}

		service.setInputParameters(inputParameters);
		service.setRevenue(serviceType.getRevenue());
		service.setMaxResponseTimeInMsec((long) serviceType.getMaxResponseTime());
		service.setServiceDemand(serviceType.getServiceDemand());

		serviceKnowledge.setProperty(serviceName, service);
		LOG.debug("Added known service " + serviceName);
	}

	private void loadBehaviors(String serviceName, List<File> behaviorFiles) {

		Service service = serviceKnowledge.getProperty(serviceName);
		for (File file : behaviorFiles) {
			EMFBehaviorBuilder behaviorBuilder = new EMFBehaviorBuilder(actionFactory, file);
			List<IBehavior> behaviors = behaviorBuilder.getBehaviors();
			for (IBehavior behavior : behaviors) {
				service.addImplementingBehavior(behavior);
			}

			// TODO for now I'm setting the default behavior as the first
			// behavior however this should be specified in the selflet project
			if (!behaviors.isEmpty()) {
				service.setDefaultBehavior(behaviors.get(0));
			}
		}

	}

}
