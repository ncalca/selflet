package it.polimi.elet.selflet.optimization.generators;

import it.polimi.elet.selflet.configuration.SelfletConfiguration;
import it.polimi.elet.selflet.optimization.actions.IOptimizationAction;
import it.polimi.elet.selflet.optimization.actions.OptimizationActionTypeEnum;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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
public class OptimizationActionGeneratorManager implements
		IOptimizationActionGeneratorManager {

	private static final Logger LOG = Logger
			.getLogger(OptimizationActionGeneratorManager.class);
	private static ImmutableSet<OptimizationActionTypeEnum> OPTIMIZATION_ACTIONS;

	private final IOptimizationActionGeneratorFactory optimizationActionGeneratorFactory;
	private final List<IActionGenerator> actionGenerators;
	private long startOptimizationTime;

	@Inject
	public OptimizationActionGeneratorManager(
			IOptimizationActionGeneratorFactory optimizationActionGeneratorFactory) {
		this.actionGenerators = Lists.newArrayList();
		this.optimizationActionGeneratorFactory = optimizationActionGeneratorFactory;
		populateActionsList();
		registerActionGenerators();
	}

	@Override
	public Set<IOptimizationAction> generateActions() {
		startMeasuringGenerationTime();
		Set<IOptimizationAction> optimizationActions = Sets.newHashSet();

		for (IActionGenerator actionGenerator : actionGenerators) {
			Collection<? extends IOptimizationAction> actionsGenerated = actionGenerator
					.generateActions();
			optimizationActions.addAll(actionsGenerated);
		}
		logGenerationTime();
		return optimizationActions;
	}

	private void populateActionsList() {
		try {
			List<String> actions = Arrays.asList(SelfletConfiguration
					.getSingleton().availableActions.split(","));
			Set<OptimizationActionTypeEnum> actionsSet = new HashSet<OptimizationActionTypeEnum>();
			for (String action : actions) {
				actionsSet.add(OptimizationActionTypeEnum.valueOf(action));
				LOG.info("loaded action: " + action);
			}
			if (!actionsSet.isEmpty()) {
				OPTIMIZATION_ACTIONS = ImmutableSet.copyOf(actionsSet);
			} else {
				OPTIMIZATION_ACTIONS = ImmutableSet
						.of(OptimizationActionTypeEnum.REDIRECT_SERVICE,
								OptimizationActionTypeEnum.ADD_SELFLET,
								OptimizationActionTypeEnum.REMOVE_SELFLET,
								OptimizationActionTypeEnum.CHANGE_SERVICE_IMPLEMENTATION,
								OptimizationActionTypeEnum.TEACH_SERVICE);
			}
		} catch (Exception e) {
			LOG.error("Error in loading possible actions. Using default set");
			OPTIMIZATION_ACTIONS = ImmutableSet
					.of(OptimizationActionTypeEnum.REDIRECT_SERVICE,
							OptimizationActionTypeEnum.ADD_SELFLET,
							OptimizationActionTypeEnum.REMOVE_SELFLET,
							OptimizationActionTypeEnum.CHANGE_SERVICE_IMPLEMENTATION,
							OptimizationActionTypeEnum.TEACH_SERVICE);
		}
	}

	private void registerActionGenerators() {
		Set<OptimizationActionTypeEnum> generatorsToRegister = getEnabledActionTypes();

		for (OptimizationActionTypeEnum actionType : generatorsToRegister) {
			LOG.debug("Registering optimization action generator for "
					+ actionType);
			actionGenerators.add(optimizationActionGeneratorFactory
					.createActionGenerator(actionType));
		}
	}

	private void logGenerationTime() {
		long endOptimizationTime = System.currentTimeMillis();
		LOG.debug("Action generation time: "
				+ (endOptimizationTime - startOptimizationTime));
	}

	private void startMeasuringGenerationTime() {
		startOptimizationTime = System.currentTimeMillis();
	}

	private ImmutableSet<OptimizationActionTypeEnum> getEnabledActionTypes() {
		return OPTIMIZATION_ACTIONS;
	}
}
