package it.polimi.elet.selflet.optimization.actions;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import it.polimi.elet.selflet.autonomic.IAutonomicActuator;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.optimization.actions.changeImplementation.ChangeServiceImplementationAction;
import it.polimi.elet.selflet.optimization.actions.redirect.RedirectAction;
import it.polimi.elet.selflet.optimization.actions.scaling.AddSelfletAction;
import it.polimi.elet.selflet.optimization.actions.scaling.RemoveSelfletAction;
import it.polimi.elet.selflet.optimization.actions.teach.TeachServiceAction;
import it.polimi.elet.selflet.service.IServiceExecutor;
import it.polimi.elet.selflet.utilities.RandomDistributions;

/**
 * An implementation of IOptimizationActionActuator
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class OptimizationActionActuator implements IOptimizationActionActuator {

	private static final Logger LOG = Logger.getLogger("actionsLogger");

	private final IServiceExecutor serviceExecutor;
	private final IAutonomicActuator autonomicAttuator;

	@Inject
	public OptimizationActionActuator(IServiceExecutor serviceExecutor,
			IAutonomicActuator autonomicAttuator) {
		this.serviceExecutor = serviceExecutor;
		this.autonomicAttuator = autonomicAttuator;
	}

	@Override
	public void actuateAction(IOptimizationAction optimizationAction) {
		try {
			if (optimizationAction.optimizationType() != OptimizationActionTypeEnum.REDIRECT_SERVICE) {
				LOG.info(System.currentTimeMillis() + ","
						+ optimizationAction.optimizationType());
			}

			switch (optimizationAction.optimizationType()) {

			case REDIRECT_SERVICE:
				RedirectAction redirectAction = (RedirectAction) optimizationAction;
				actuateRedirect(redirectAction);
				break;

			case ADD_SELFLET:
				AddSelfletAction addSelfletAction = (AddSelfletAction) optimizationAction;
				actuateNewSelflet(addSelfletAction);
				break;

			case REMOVE_SELFLET:
				RemoveSelfletAction removeSelfletAction = (RemoveSelfletAction) optimizationAction;
				actuateRemoveSelflet(removeSelfletAction);
				break;

			case TEACH_SERVICE:
				TeachServiceAction teachServiceAction = (TeachServiceAction) optimizationAction;
				actuateTeachService(teachServiceAction);
				break;

			case CHANGE_SERVICE_IMPLEMENTATION:
				ChangeServiceImplementationAction changeServiceImplementationAction = (ChangeServiceImplementationAction) optimizationAction;
				actuateChangeServiceImplementation(changeServiceImplementationAction);
				break;

			default:
				throw new IllegalStateException(
						"Trying to actuate an action for which the actuation procedure has not been already specified");
			}
		} catch (Exception e) {
			LOG.error("error in optimization action");
		}

	}

	private void actuateTeachService(TeachServiceAction optimizationAction) {
		try {
			String serviceName = optimizationAction.getService().getName();
			ISelfLetID receiverSelflet = optimizationAction
					.getReceiverSelflet();
			autonomicAttuator.teachServiceToProvider(serviceName,
					receiverSelflet);
		} catch (Exception e) {
			LOG.error("error actuating tech service "
					+ optimizationAction.getService().getName() + ": " + e);
		}
	}

	private void actuateRemoveSelflet(RemoveSelfletAction optimizationAction) {
		double randomNumber = RandomDistributions.randUniform();
		if (randomNumber > optimizationAction.weight()) {
			autonomicAttuator.removeSelflet();
		}
	}

	private void actuateNewSelflet(AddSelfletAction optimizationAction) {
		autonomicAttuator.istantiateNewSelflet();
	}

	private void actuateRedirect(RedirectAction redirectOptimizationAction) {
		serviceExecutor.setRedirectPolicies(redirectOptimizationAction
				.getRedirectPolicy());
	}

	private void actuateChangeServiceImplementation(
			ChangeServiceImplementationAction changeServiceImplementationAction) {
		autonomicAttuator.changeServiceImplementation(
				changeServiceImplementationAction.getService().getName(),
				changeServiceImplementationAction.getBehaviorQuality());
	}

}
