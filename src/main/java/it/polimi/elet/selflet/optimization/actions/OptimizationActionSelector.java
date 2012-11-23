package it.polimi.elet.selflet.optimization.actions;

import it.polimi.elet.selflet.utilities.CollectionUtils;

import java.util.Set;

import com.google.inject.Singleton;

/**
 * An implementation of optimization action selector
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class OptimizationActionSelector implements IOptimizationActionSelector {

	@Override
	public IOptimizationAction selectAction(Set<IOptimizationAction> optimizationActions) {

		if (optimizationActions == null || optimizationActions.isEmpty()) {
			throw new IllegalArgumentException("The given set of optimization actions is empty");
		}

		return CollectionUtils.weightedRandomElement(optimizationActions);
	}

}
