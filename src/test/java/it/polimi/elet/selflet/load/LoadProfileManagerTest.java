package it.polimi.elet.selflet.load;

import static junit.framework.Assert.*;
import it.polimi.elet.selflet.load.LoadProfile.Couple;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

public class LoadProfileManagerTest {

	private final String PATH = "../../../../../load_profiles/";
	private final String TEST_PROFILE = "test";
	private final double DELTA = 1e-3;

	private LoadProfileManager profilesManager;
	private LoadProfile loadProfile;

	@Before
	public void setUp() {
		this.profilesManager = new LoadProfileManager();
		this.profilesManager.loadProfiles(PATH);
		this.loadProfile = profilesManager.getLoadProfile(TEST_PROFILE);
	}

	@Test
	public void testLoadFiles() {
		assertEquals(5, profilesManager.getNumberOfLoadProfiles());
	}

	@Test
	public void testFilePresence() {
		assertNotNull(loadProfile);
	}

	@Test
	public void testFileContent() {
		assertEquals(3, loadProfile.size());
		List<Couple> track = loadProfile.getTrack();
		assertEquals(100.0, track.get(0).duration, DELTA);
		assertEquals(0.2, track.get(0).probability, DELTA);
		assertEquals(200.0, track.get(1).duration, DELTA);
		assertEquals(0.8, track.get(1).probability, DELTA);
		assertEquals(300.0, track.get(2).duration, DELTA);
		assertEquals(1, track.get(2).probability, DELTA);
	}

	@Test
	public void testProbability() {
		final double REPETITIONS = 10000.0;
		final double LARGER_DELTA = DELTA * 10;
		Map<Integer, Integer> values = Maps.newHashMap();

		for (int i = 0; i < REPETITIONS; i++) {
			int duration = (int) loadProfile.extractANewDuration();
			if (values.containsKey(duration)) {
				values.put(duration, values.get(duration) + 1);
			} else {
				values.put(duration, 1);
			}
		}

		assertEquals(0.2, values.get(100) / REPETITIONS, LARGER_DELTA);
		assertEquals(0.6, values.get(200) / REPETITIONS, LARGER_DELTA);
		assertEquals(0.2, values.get(300) / REPETITIONS, LARGER_DELTA);
	}
}
