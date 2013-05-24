package it.polimi.elet.selflet.load;

import java.util.List;

/**
 * Manages the tracks used for load
 * 
 * @author Nicola Calcavecchia
 * */
public interface ILoadProfileManager {

	/**
	 * Loads tracks from default location
	 * */
	void loadProfiles();
	
	/**
	 * Loads tracks from disk
	 * */
	void loadProfiles(String path);

	/**
	 * Returns the number of loaded tracks
	 * */
	int getNumberOfLoadProfiles();

	/**
	 * Retrieves all load profiles
	 * */
	List<String> getLoadProfiles();

	/**
	 * Returns the loadProfile with the given name
	 * */
	LoadProfile getLoadProfile(String loadProfileName);

}
