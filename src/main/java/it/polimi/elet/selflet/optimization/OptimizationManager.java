package it.polimi.elet.selflet.optimization;

import java.util.Set;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import it.polimi.elet.selflet.configuration.SelfletConfiguration;
import it.polimi.elet.selflet.optimization.actions.IOptimizationAction;
import it.polimi.elet.selflet.optimization.actions.IOptimizationActionActuator;
import it.polimi.elet.selflet.optimization.actions.IOptimizationActionSelector;
import it.polimi.elet.selflet.optimization.generators.IOptimizationActionGeneratorManager;
import it.polimi.elet.selflet.utilities.RandomDistributions;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * An implementation for optimization manager. It is the main class managing all
 * the optimization aspects
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class OptimizationManager extends TimerTask implements
		IOptimizationManager {

	private static final Logger LOG = Logger
			.getLogger(OptimizationManager.class);

	private static final int SLEEP_TIME = SelfletConfiguration.getSingleton().optimizationManagerSleepTimeInSec * 1000;

	private final IOptimizationActionGeneratorManager optimizationActionGenerator;
	private final IOptimizationActionActuator optimizationActionActuator;
	private final IOptimizationActionSelector optimizationActionSelector;

	@Inject
	public OptimizationManager(
			IOptimizationActionGeneratorManager optimizationActionGenerator,
			IOptimizationActionActuator optimizationActionActuator,
			IOptimizationActionSelector optimizationActionSelector) {
		this.optimizationActionGenerator = optimizationActionGenerator;
		this.optimizationActionActuator = optimizationActionActuator;
		this.optimizationActionSelector = optimizationActionSelector;
	}

	@Override
	public void run() {
		// randomSleep();
		LOG.debug("Performing periodic optimization...");
		try {
			performOptimization();
		} catch (RuntimeException e) {
			LOG.error("Error in optimization manager", e);
		}
	}

	@Override
	public void performOptimization() {
		Set<IOptimizationAction> optimizationActions = optimizationActionGenerator
				.generateActions();

		logOptimizationActions(optimizationActions);
		if (optimizationActions.isEmpty()) {
			return;
		}

		try {
			IOptimizationAction optimizationActionToActuate = optimizationActionSelector
					.selectAction(optimizationActions);
			optimizationActionActuator
					.actuateAction(optimizationActionToActuate);
		} catch (Exception e) {
			LOG.warn(e.getMessage());
			return;
		}
	}

	private void logOptimizationActions(
			Set<IOptimizationAction> optimizationActions) {
		LOG.debug("Generated " + optimizationActions.size()
				+ " optimization actions");
		for (IOptimizationAction optimizationAction : optimizationActions) {
			LOG.debug(optimizationAction);
		}

	}

	private void randomSleep() {
		double randomNumber = RandomDistributions.randUniform();
		long randomSleepTime = (long) (randomNumber * SLEEP_TIME);
		try {
			Thread.sleep(randomSleepTime);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int periodInMsec() {
		return SLEEP_TIME;
	}

}
