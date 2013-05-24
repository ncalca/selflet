package it.polimi.elet.selflet.load;

/**
 * Simple storage class representing an item for the traffic mix.
 * 
 * The item contains two elements: 1) a name for the traffic profile and 2) a
 * probability for that profile.
 * 
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class TrafficMixItem {

	public final String profileName;
	public final double probability;

	public TrafficMixItem(String profileName, double probability) {
		this.profileName = profileName;
		this.probability = probability;
	}

	/**
	 * Expects as input a string "profileName:probability"
	 * */
	public TrafficMixItem(String item) {
		String[] pieces = item.split(":");
		if (pieces == null || pieces.length != 2) {
			throw new IllegalArgumentException("Invalid traffic mix item string");
		}

		this.profileName = pieces[0];
		this.probability = Double.valueOf(pieces[1]);
	}

	@Override
	public String toString() {
		return profileName + ":" + Double.toString(probability);
	}

}
