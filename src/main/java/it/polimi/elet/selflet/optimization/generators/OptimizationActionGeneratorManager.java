package it.polimi.elet.selflet.optimization.generators;

import it.polimi.elet.selflet.optimization.actions.IOptimizationAction;
import it.polimi.elet.selflet.optimization.actions.OptimizationActionTypeEnum;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static it.polimi.elet.selflet.optimization.actions.OptimizationActionTypeEnum.*;

/**
 * This class contains the methods to generate all possible optimization
 * actions: change a service implementation, teach a behavior, cloud expansion.
 * 
 * It register all possible action generators and then invokes them to produce
 * possible optimization actions
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class OptimizationActionGeneratorManager implements IOptimizationActionGeneratorManager {

	private static final Logger LOG = Logger.getLogger(OptimizationActionGeneratorManager.class);
	private static final ImmutableSet<OptimizationActionTypeEnum>

//	OPTIMIZATION_ACTIONS = ImmutableSet.of(REDIRECT_SERVICE, ADD_SELFLET, REMOVE_SELFLET, CHANGE_SERVICE_IMPLEMENTATION, TEACH_SERVICE);
	OPTIMIZATION_ACTIONS = ImmutableSet.of(CHANGE_SERVICE_IMPLEMENTATION, TEACH_SERVICE, REDIRECT_SERVICE);

	private final IOptimizationActionGeneratorFactory optimizationActionGeneratorFactory;
	private final List<IActionGenerator> actionGenerators;
	private long startOptimizationTime;

	@Inject
	public OptimizationActionGeneratorManager(IOptimizationActionGeneratorFactory optimizationActionGeneratorFactory) {
		this.actionGenerators = Lists.newArrayList();
		this.optimizationActionGeneratorFactory = optimizationActionGeneratorFactory;
		registerActionGenerators();
	}

	@Override
	public Set<IOptimizationAction> generateActions() {
		startMeasuringGenerationTime();
		Set<IOptimizationAction> optimizationActions = Sets.newHashSet();

		for (IActionGenerator actionGenerator : actionGenerators) {
			Collection<? extends IOptimizationAction> actionsGenerated = actionGenerator.generateActions();
			optimizationActions.addAll(actionsGenerated);
		}
		logGenerationTime();
		return optimizationActions;
	}

	private void registerActionGenerators() {
		Set<OptimizationActionTypeEnum> generatorsToRegister = getEnabledActionTypes();

		for (OptimizationActionTypeEnum actionType : generatorsToRegister) {
			LOG.debug("Registering optimization action generator for " + actionType);
			actionGenerators.add(optimizationActionGeneratorFactory.createActionGenerator(actionType));
		}
	}

	private void logGenerationTime() {
		long endOptimizationTime = System.currentTimeMillis();
		LOG.debug("Action generation time: " + (endOptimizationTime - startOptimizationTime));
	}

	private void startMeasuringGenerationTime() {
		startOptimizationTime = System.currentTimeMillis();
	}

	private ImmutableSet<OptimizationActionTypeEnum> getEnabledActionTypes() {
		return OPTIMIZATION_ACTIONS;
	}
}
