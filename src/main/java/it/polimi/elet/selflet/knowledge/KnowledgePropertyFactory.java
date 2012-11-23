package it.polimi.elet.selflet.knowledge;

import it.polimi.elet.selflet.negotiation.ServiceAskModeEnum;
import it.polimi.elet.selflet.negotiation.ServiceOfferModeEnum;
import it.polimi.elet.selflet.utilities.parser.SelfLetProperty;

/**
 * The main method is create. This method receives a
 * <code>SelfLetProperty</code> and returns an object instance representing that
 * property.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class KnowledgePropertyFactory {

	private KnowledgePropertyFactory() {
		// private
	}

	/**
	 * Returns an instance of the property as described in the
	 * <code>property</code> object
	 * */
	public static Object create(SelfLetProperty property) {

		String type = property.getType();
		String value = property.getValue();

		if (type.equalsIgnoreCase("double")) {
			return Double.valueOf(value);
		}

		if (type.equalsIgnoreCase("integer")) {
			return Integer.valueOf(value);
		}

		if (type.equalsIgnoreCase("string")) {
			return value;
		}

		if (type.equalsIgnoreCase("boolean")) {
			return Boolean.valueOf(value);
		}

		if (type.equalsIgnoreCase("ServiceAskMode")) {

			if (value.equalsIgnoreCase("Teach")) {
				return ServiceAskModeEnum.Teach;
			}

			if (value.equalsIgnoreCase("Do")) {
				return ServiceAskModeEnum.Do;
			}
		}

		if (type.equalsIgnoreCase("ServiceOfferMode")) {

			if (value.equalsIgnoreCase("CanTeach")) {
				return ServiceOfferModeEnum.CanTeach;
			}

			if (value.equalsIgnoreCase("CanDo")) {
				return ServiceOfferModeEnum.CanDo;
			}
		}

		throw new IllegalArgumentException("Impossible to instantiate object for property " + property);
	}

}
