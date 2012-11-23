package it.polimi.elet.selflet.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import it.polimi.elet.selflet.knowledge.IGeneralKnowledge;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;

/**
 * Extracts the map of service parameters used as input for the service
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ServiceParametersExtractor {

	private static final Logger LOG = Logger.getLogger(ServiceParametersExtractor.class);

	private final Service service;
	private final IGeneralKnowledge generalKnowledge;

	public ServiceParametersExtractor(Service service, IGeneralKnowledge generalKnowledge) {
		this.service = service;
		this.generalKnowledge = generalKnowledge;
	}

	public ServiceParametersExtractor(String serviceName, IServiceKnowledge serviceKnowledge, IGeneralKnowledge generalKnowledge) {
		this.generalKnowledge = generalKnowledge;
		this.service = serviceKnowledge.getProperty(serviceName);
	}

	/**
	 * Returns the map of service parameters
	 * */
	public Map<String, Object> getServiceParameters() {

		Map<String, Object> parameters = new HashMap<String, Object>();

		Iterator<String> serviceParams = service.getInputParameters().keySet().iterator();

		while (serviceParams.hasNext()) {

			String parameterName = serviceParams.next();
			if (!generalKnowledge.isPropertyPresent(parameterName)) {
				LOG.info("Looking for a non existing value of parameter");
				continue;
			}

			Object parameterValue = generalKnowledge.getProperty(parameterName);

			// type check for the parameter to pass
			if (parameterValue.getClass().equals(service.getInputParameters().get(parameterName))) {
				parameters.put(parameterName, parameterValue);
			} else {
				LOG.info("The parameter passed as input is different than the one expected by the service");
			}
		}

		return parameters;
	}

}
