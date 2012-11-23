package it.polimi.elet.selflet.knowledge;

import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.knowledge.attributes.Attribute;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Contains information about each SelfLet's neighbor. Informations are
 * maintained in <code>Attribute</code> classes.
 * 
 * @author silvia
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 */
public class Neighbor {

	private final ISelfLetID selfletID;
	private final Set<Attribute> attributes;

	public Neighbor(ISelfLetID selfletID) {
		this.selfletID = selfletID;
		this.attributes = Sets.newHashSet();
	}

	public Neighbor(ISelfLetID selfletID, Set<Attribute> attributes) {
		this.selfletID = selfletID;
		this.attributes = Sets.newHashSet(attributes);
	}

	public ISelfLetID getId() {
		return selfletID;
	}

	public Attribute getAttribute(String attributeName) {
		for (Attribute attribute : attributes) {
			if (attribute.getName().equals(attributeName)) {
				return attribute;
			}
		}

		throw new NotFoundException("Attribute " + attributeName + " not found");
	}

	public Set<Attribute> getAttributes() {
		return attributes;
	}

	public void addAttributes(Set<Attribute> attributes) {
		this.attributes.addAll(attributes);
	}

	public void addAttribute(Attribute attribute) {
		attributes.add(attribute);
	}

	public void removeAttribute(Attribute attribute) {
		attributes.remove(attribute);
	}

	public String toString() {
		StringBuffer stringBuffer = new StringBuffer(" SelfletID: " + selfletID + " =");

		for (Attribute attribute : attributes) {
			stringBuffer.append("\n");
			stringBuffer.append(attribute);
		}

		return stringBuffer.toString();
	}

}
