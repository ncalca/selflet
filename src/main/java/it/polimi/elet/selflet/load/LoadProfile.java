package it.polimi.elet.selflet.load;

import static it.polimi.elet.selflet.utilities.MathUtil.*;
import it.polimi.elet.selflet.utilities.RandomDistributions;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * A class representing a request track
 * 
 * @author Nicola Calcavecchia
 * */
public class LoadProfile {

	private final List<Couple> couples;

	public LoadProfile() {
		this.couples = Lists.newArrayList();
	}

	public void addCouple(float duration, float probability) {
		Couple couple = new Couple(duration, probability);
		this.couples.add(couple);
	}

	public List<Couple> getTrack() {
		return Lists.newArrayList(couples);
	}

	public int size() {
		return couples.size();
	}

	public double extractNewWaitTimeInMillis() {
		double randomNumber = RandomDistributions.randUniform();
		double prevProbability = 0;
		for (int i = 0; i < couples.size() - 1; i++) {
			Couple couple = couples.get(i);
			if (isInRangeInclusive(randomNumber, prevProbability, couple.probability)) {
				return couple.duration;
			}
			prevProbability = couple.probability;
		}
		return couples.get(couples.size() - 1).duration;
	}

	/**
	 * Inner class containing a couple of values for duration of and probability
	 * */
	public class Couple {

		public final float duration;
		public final float probability;

		public Couple(float duration, float probability) {
			this.duration = duration;
			this.probability = probability;
		}
	}

}
