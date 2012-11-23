package it.polimi.elet.selflet.optimization.generators;

import it.polimi.elet.selflet.optimization.actions.IOptimizationAction;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * A class generating change service implementation actions
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ChangeServiceImplementationGenerator implements IActionGenerator {

	@Override
	public Collection<? extends IOptimizationAction> generateActions() {
		Set<IOptimizationAction> optimizationActions = Sets.newHashSet();
		// TODO
		return optimizationActions;
	}

}
