package it.polimi.elet.selflet.load;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class LoadProfileManager implements ILoadProfileManager {

	private static final Logger LOG = Logger.getLogger(LoadProfileManager.class);
	private static final String LOAD_PROFILE_FILE_EXTENSION = ".csv";

	private final Map<String, LoadProfile> profiles = Maps.newHashMap();

	@Override
	public void loadProfiles(String path) {
		List<File> files = getAvailableFiles(path);
		for (File file : files) {
			LOG.warn("Loading load profile: " + file);
			LoadProfile loadProfile = createLoadProfile(file);
			String profileName = file.getName().replace(".csv", "");
			profiles.put(profileName, loadProfile);
		}
	}

	private List<File> getAvailableFiles(String path) {
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

}
