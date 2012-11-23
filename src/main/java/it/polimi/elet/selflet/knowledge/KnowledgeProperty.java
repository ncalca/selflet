package it.polimi.elet.selflet.knowledge;

/**
 * Simple storage class representing a generic knowledge property
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class KnowledgeProperty {

	private final String key;
	private final Object value;

	public KnowledgeProperty(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

}
