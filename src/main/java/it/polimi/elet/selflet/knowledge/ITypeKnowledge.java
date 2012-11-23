package it.polimi.elet.selflet.knowledge;

import it.polimi.elet.selflet.events.ISelfletComponent;
import it.polimi.elet.selflet.knowledge.attributes.Attribute;

/**
 * Interface for type knowledge. The type knowledge contains information about
 * neighbors
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface ITypeKnowledge extends IKnowledge<Object>, ISelfletComponent {

	/**
	 * Adds the given attribute to the type knowledge
	 * */
	void setAttribute(Attribute attribute);

}
