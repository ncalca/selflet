package it.polimi.elet.selflet.knowledge;

import it.polimi.elet.selflet.exceptions.AlreadyPresentException;
import it.polimi.elet.selflet.exceptions.IncompatibleTypeException;
import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.service.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;
import com.google.inject.Singleton;

/**
 * This class contains and managed all the knowledge of an SelfLet related to
 * Services This knowledge includes the correspondence between service and
 * fufilling behavior, the list of services known
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * @author silvia
 */
@Singleton
public class ServiceKnowledge extends Knowledge implements IServiceKnowledge {

	private static final Logger LOG = Logger.getLogger(ServiceKnowledge.class);

	private Map<String, Service> services;

	public ServiceKnowledge() {
		super();
		services = new ConcurrentHashMap<String, Service>();
	}

	public void deleteProperty(String serviceName) {

		if (serviceName == null) {
			throw new IllegalArgumentException("Service name can't be null to delete its properties");
		}

		if (!isPropertyPresent(serviceName)) {
			return;
		}

		Service propertyValue = services.get(serviceName);
		services.remove(serviceName);
		LOG.debug("Deleted Service property " + serviceName);
		firePropertyDeletedEvent(serviceName, propertyValue);
	}

	/**
	 * returns the whole map containing all services
	 * 
	 */
	public Map<String, Service> getProperties() {
		return new HashMap<String, Service>(services);
	}

	public Service getProperty(String name) {

		if (name == null) {
			throw new IllegalArgumentException("Service name can't be null");
		}

		if (services.get(name) == null) {
			LOG.debug("Service property not found " + name);
			throw new NotFoundException("Service Property Not Found : " + name);
		}

		return services.get(name);
	}

	public boolean isPropertyPresent(String name) {
		return services.containsKey(name);
	}

	public void setProperty(String name, Service property) {

		if (name == null) {
			throw new IllegalArgumentException("Service name can't be null");
		}

		if (isPropertyPresent(name)) {
			throw new AlreadyPresentException("The Service " + name + " is already present; use the " + "updateProperty method to update it");
		}

		LOG.debug("Set Service " + name + " to value " + property);
		services.put(name, property);
		firePropertySetEvent(name, property);

	}

	public void updateProperty(String name, Service property) {

		if (name == null) {
			throw new IllegalArgumentException("Property name can't be null");
		}

		if (!isPropertyPresent(name)) {
			try {
				setProperty(name, property);
			} catch (AlreadyPresentException e) {
				// Can't happen
			} catch (IncompatibleTypeException e) {
				LOG.error("Can't update property because the specified service is unknown and the property to add is not a service", e);
			}
		}

		Service previousValue = services.get(name);

		LOG.debug("Updated service property " + name + " to value " + property);
		services.put(name, property);
		firePropertyUpdatedEvent(name, property, previousValue);
	}

	public void setOrUpdateProperty(String name, Service property) {
		try {
			setProperty(name, property);
		} catch (AlreadyPresentException e) {
			updateProperty(name, property);
		}
	}

	@Override
	public Set<Service> getServices() {
		return Sets.newHashSet(services.values());
	}

	/**
	 * @return true <==> if the service is local (i.e. exists at least a
	 *         behavior implementing it)
	 * */
	public boolean isLocalService(String serviceName) {

		if (serviceName == null) {
			throw new IllegalArgumentException("Servicename name can't be null");
		}

		Service serviceProperty = null;

		try {
			serviceProperty = getProperty(serviceName);
		} catch (NotFoundException e) {
			return false;
		}

		return (serviceProperty.hasImplementation() && serviceProperty.isEnabled());
	}

}
