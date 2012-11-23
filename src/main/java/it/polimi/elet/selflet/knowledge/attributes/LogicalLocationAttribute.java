package it.polimi.elet.selflet.knowledge.attributes;

public class LogicalLocationAttribute extends Attribute {

	public static final String ATTRIBUTE_NAME = "logical.location";

	public String getNetName() {
		return (String) parameterMap.get("netName");
	}

	public void setNetName(String netName) {
		parameterMap.put("netName", netName);
	}

	public String getName() {
		return ATTRIBUTE_NAME;
	}

}
