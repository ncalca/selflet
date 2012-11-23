package it.polimi.elet.selflet.knowledge.attributes;

import java.util.HashMap;
import java.util.Map;

/**
 * this class contains some information about the physical Location where the
 * ace is settled. It's an implementation of a possible criterion for the
 * grouping of the aces
 * 
 * @author silvia
 */
public class PhysicalLocationAttribute extends Attribute {

	private static final String ATTRIBUTE_NAME = "physical.location";

	private Map<String, Object> parameterMap = new HashMap<String, Object>();

	public String getCountry() {
		return (String) parameterMap.get("country");
	}

	public void setCountry(String country) {
		parameterMap.put("country", country);
	}

	public String getCity() {
		return (String) parameterMap.get("city");
	}

	public void setCity(String city) {
		parameterMap.put("city", city);
	}

	public String getHostName() {
		return (String) parameterMap.get("hostName");
	}

	public void setHostName(String hostName) {
		parameterMap.put("hostName", hostName);
	}

	public String getName() {
		return ATTRIBUTE_NAME;
	}

}
