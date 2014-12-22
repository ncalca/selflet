package it.polimi.elet.selflet.optimization.generators;

import it.polimi.elet.selflet.optimization.actions.IOptimizationAction;

import java.util.Collection;

import com.google.common.collect.Lists;

public class NoActionGenerator implements IActionGenerator {

	@Override
	public Collection<? extends IOptimizationAction> generateActions() {
		return Lists.newArrayList();
	}

}
