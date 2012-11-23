package it.polimi.elet.selflet.knowledge.attributes;


public class ServiceAttribute extends Attribute {

	public static final String ATTRIBUTE_NAME = "services.attributes";
	public static final String AVAILABLE_SERVICES = "available.services";
	public static final String SERVICE_OFFERED_BY_NEIGHBORS = "services.offered.by.neighbors";
	public static final String SERVICE_COSTS = "service.costs";

	public String getName() {
		return ATTRIBUTE_NAME;
	}

}
