package it.polimi.elet.selflet.load;

/**
 * Manages the tracks used for load
 * 
 * @author Nicola Calcavecchia
 * */
public interface ILoadProfileManager {

	/**
	 * Loads tracks from disk
	 * */
	void loadProfiles(String path);

	/**
	 * Returns the number of loaded tracks
	 * */
	int getNumberOfLoadProfiles();

}
