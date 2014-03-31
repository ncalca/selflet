package it.polimi.elet.selflet.service;

import it.polimi.elet.selflet.behavior.IBehavior;
import it.polimi.elet.selflet.behavior.State;
import it.polimi.elet.selflet.events.service.ProvidersForService;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.negotiation.ServiceAskModeEnum;
import it.polimi.elet.selflet.negotiation.ServiceOfferModeEnum;
import it.polimi.elet.selflet.negotiation.ServiceProvider;
import it.polimi.elet.selflet.utilities.CollectionUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * A class representing a Service.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 */
public class Service implements Serializable {

	private static final long serialVersionUID = 1L;

	private final long creationTime = System.currentTimeMillis();

	private final String name;
	private final Map<String, Class<?>> inputParameters = Maps.newConcurrentMap();
	private final Set<IBehavior> implementingBehaviors = Sets.newHashSet();
	private final ProvidersForService providersForService;

	private IBehavior defaultBehavior;

	private boolean enabled = true;
	private double revenue = 0;
	private long maxResponseTimeInMsec;
	private double serviceDemand;

	/**
	 * Creates a service with the given name
	 * */
	public Service(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Error while creating service; some parameter is null ");
		}
		this.name = name;
		this.providersForService = new ProvidersForService(name);
	}

	/**
	 * Creates a service with the given name and default behavior
	 * */
	public Service(String name, IBehavior defaultBehavior) {
		if (name == null || defaultBehavior == null) {
			throw new IllegalArgumentException("Error while creating service; some parameter is null ");
		}

		this.name = name;
		this.defaultBehavior = defaultBehavior;
		this.implementingBehaviors.add(defaultBehavior);
		this.providersForService = new ProvidersForService(name);
	}

	public String getName() {
		return name;
	}

	public void setDefaultBehavior(IBehavior newDefaultBehavior) {
		this.defaultBehavior = newDefaultBehavior;
	}

	public Set<IBehavior> getImplementingBehaviors() {
		return new HashSet<IBehavior>(implementingBehaviors);
	}

	public void setImplementingBehaviors(Set<IBehavior> implementingBehaviors) {
		implementingBehaviors.clear();
		implementingBehaviors.addAll(implementingBehaviors);
	}

	public void addImplementingBehavior(IBehavior behavior) {
		implementingBehaviors.add(behavior);
	}

	public void addImplementingBehavior(Set<IBehavior> implementingBehaviors) {
		this.implementingBehaviors.addAll(implementingBehaviors);
	}

	public void disable() {
		setLocalOfferMode(ServiceOfferModeEnum.None, true);
		enabled = false;
	}

	public void enable() {
		enabled = true;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isLocallyAvailable() {
		return (isEnabled() && hasImplementation());
	}

	public boolean isRemote() {
		return (!isLocallyAvailable());
	}

	public IBehavior getDefaultBehavior() {
		return defaultBehavior;
	}

	public boolean isImplementedByElementaryBehavior() {
		if (!isLocallyAvailable()) {
			return false;
		}
		return defaultBehavior.isElementaryBehavior();
	}

	public void setRevenue(double revenue) {
		if (revenue <= 0) {
			throw new IllegalArgumentException("Revenue cannot be <=0");
		}
		this.revenue = revenue;
	}

	public double getRevenue() {
		return revenue;
	}

	public void setMaxResponseTimeInMsec(long maxResponseTime) {
		if (maxResponseTime <= 0) {
			throw new IllegalArgumentException("Max response time cannot be <=0. Given " + maxResponseTime);
		}
		this.maxResponseTimeInMsec = maxResponseTime;
	}

	public long getMaxResponseTimeInMsec() {
		return maxResponseTimeInMsec;
	}
	
	public void setServiceDemand(double serviceDemand){
		this.serviceDemand = serviceDemand;
	}
	
	public double getServiceDemand(){
		return this.serviceDemand;
	}

	public String toString() {
		return name;
	}

	/**
	 * removes a given Behavior from the list of those implementing this service
	 * 
	 * @return
	 */
	public void removeImplementingBehaviorSignature(IBehavior behavior) {
		implementingBehaviors.remove(behavior);
		if (defaultBehavior.equals(behavior)) {
			if (getImplementingBehaviors().isEmpty()) {
				disable();
			} else {
				defaultBehavior = CollectionUtils.randomElement(implementingBehaviors);
			}
		}
	}

	public List<String> getInvokedServices() {
		List<String> invokedServices = Lists.newArrayList();
		List<State> states = defaultBehavior.getStates();

		for (State state : states) {
			if (state.isFinalState() || state.isInitialState()) {
				continue;
			}

			invokedServices.add(state.getName());
		}
		return invokedServices;
	}

	public boolean hasImplementation() {
		return !implementingBehaviors.isEmpty();
	}

	public Map<String, Class<?>> getInputParameters() {
		return inputParameters;
	}

	public void setInputParameters(Map<String, Class<?>> parameters) {
		inputParameters.clear();
		inputParameters.putAll(parameters);
	}

	public void addProvider(ServiceProvider provider) {
		providersForService.addProvider(provider);
	}

	public void removeProvider(ISelfLetID providerName) {
		providersForService.removeProvider(providerName);
	}

	public void setLocalOfferMode(ServiceOfferModeEnum mode, boolean isOffered) {
		providersForService.setLocalOfferMode(mode, isOffered);
	}

	public boolean hasLocalOfferMode(ServiceOfferModeEnum mode) {
		return providersForService.hasLocalOfferMode(mode);
	}

	public ServiceProvider getPreferredProvider(ServiceAskModeEnum mode) {
		return providersForService.getPreferredProvider(mode);
	}

	public List<Object> getProviders() {
		return providersForService.getAllProviders();
	}

	public List<ServiceOfferModeEnum> getOfferModes() {
		return providersForService.getOfferModes();
	}

	public boolean isRecentlyCreated() {
		long now = System.currentTimeMillis();
		return (now - creationTime) < 120 * 1000;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Service)) {
			return false;
		}
		Service other = (Service) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

}