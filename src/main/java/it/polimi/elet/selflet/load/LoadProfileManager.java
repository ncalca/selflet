package it.polimi.elet.selflet.load;

import static it.polimi.elet.selflet.utilities.MathUtil.*;

import it.polimi.elet.selflet.configuration.SelfletConfiguration;
import it.polimi.elet.selflet.utilities.RandomDistributions;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Singleton;

/**
 * An implementation for load profile manager
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class LoadProfileManager implements ILoadProfileManager {

	private static final Logger LOG = Logger.getLogger(LoadProfileManager.class);
	private static final String LOAD_PROFILE_FILE_EXTENSION = ".csv";
	private static final String DEFAULT_PATH_LOCATION = "../../../../../load_profiles/";

	private final Map<String, LoadProfile> profiles = Maps.newHashMap();
	private final List<TrafficMixItem> loadProfilesMix = Lists.newArrayList();

	@Override
	public void loadProfiles() {
		loadProfiles(DEFAULT_PATH_LOCATION, SelfletConfiguration.getSingleton().loadProfileTrafficMix);
	}

	@Override
	public void loadProfiles(String path, String trafficMix) {
		List<File> files = getAvailableProfileFiles(path);
		loadFiles(files);
		loadProfileMix(trafficMix);
	}

	private void loadProfileMix(String loadProfileTrafficMix) {
		String stringItems[] = loadProfileTrafficMix.split(",");
		for (String item : stringItems) {
			TrafficMixItem mixItem = new TrafficMixItem(item);
			loadProfilesMix.add(mixItem);
		}

		checkValidityOfProfileMix();
	}

	private void checkValidityOfProfileMix() {
		Set<String> profileNames = profiles.keySet();
		for (TrafficMixItem item : loadProfilesMix) {
			if (!profileNames.contains(item.profileName)) {
				throw new IllegalArgumentException("Invalid profile mix item: " + item.profileName);
			}
		}
	}

	private void loadFiles(List<File> files) {
		for (File file : files) {
			LOG.info("Loading load profile: " + file);
			LoadProfile loadProfile = createLoadProfile(file);
			String profileName = file.getName().replace(".csv", "");
			profiles.put(profileName, loadProfile);
		}
	}

	private List<File> getAvailableProfileFiles(String path) {
		URL folderURL = LoadProfileManager.class.getResource(path);
		File folder = null;
		try {
			folder = new File(folderURL.toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		File[] files = folder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File fileName) {
				return fileName.getName().toLowerCase().endsWith(LOAD_PROFILE_FILE_EXTENSION);
			}
		});

		return Lists.newArrayList(files);
	}

	private LoadProfile createLoadProfile(File file) {
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			new IllegalStateException(e);
		}

		LoadProfile loadProfile = new LoadProfile();
		Scanner scanner = new Scanner(fileInputStream);
		while (scanner.hasNext()) {
			float duration = scanner.nextFloat();
			float probability = scanner.nextFloat();
			loadProfile.addCouple(duration, probability);
		}

		return loadProfile;
	}

	@Override
	public LoadProfile getLoadProfile(String profileName) {
		return profiles.get(profileName);
	}

	@Override
	public int getNumberOfLoadProfiles() {
		return profiles.size();
	}

	@Override
	public List<String> getLoadProfiles() {
		return Lists.newArrayList(profiles.keySet());
	}

	@Override
	public List<TrafficMixItem> getLoadProfilesMix() {
		return loadProfilesMix;
	}

	@Override
	public String extractLoadProfile() {
		double random = RandomDistributions.randUniform();
		double prevProb = 0;
		for (int i = 0; i < loadProfilesMix.size() - 1; i++) {
			TrafficMixItem item = loadProfilesMix.get(i);
			if (isInRangeInclusive(random, prevProb, item.probability)) {
				return item.profileName;
			}
			prevProb = item.probability;
		}

		return loadProfilesMix.get(loadProfilesMix.size() - 1).profileName;
	}

}
