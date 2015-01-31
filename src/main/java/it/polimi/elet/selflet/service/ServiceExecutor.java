package it.polimi.elet.selflet.service;

import it.polimi.elet.selflet.autonomic.IAutonomicActuator;
import it.polimi.elet.selflet.events.SelfletComponent;
import it.polimi.elet.selflet.events.service.LocalReqLocalExeExecuteEvent;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.negotiation.nodeState.INeighborStateManager;
import it.polimi.elet.selflet.optimization.actions.redirect.IRedirectCalculator;
import it.polimi.elet.selflet.optimization.actions.redirect.IRedirectCalculatorFactory;
import it.polimi.elet.selflet.optimization.actions.redirect.RedirectPolicy;

import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * An implementation of service executor
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class ServiceExecutor extends SelfletComponent implements IServiceExecutor {

	private static final Logger LOG = Logger.getLogger(ServiceExecutor.class);
	private static final Logger ACTLOG = Logger
			.getLogger("actionsLogger");

	private final IRedirectCalculatorFactory redirectCalculatorFactory;
	private final IAutonomicActuator autonomicAttuator;
	private final Set<RedirectPolicy> redirectPolicies;
	private final INeighborStateManager neighborStateManager;

	@Inject
	public ServiceExecutor(IRedirectCalculatorFactory redirectCalculatorFactory, IAutonomicActuator autonomicAttuator,
			INeighborStateManager neighborStateManager) {
		this.redirectCalculatorFactory = redirectCalculatorFactory;
		this.autonomicAttuator = autonomicAttuator;
		this.redirectPolicies = Sets.newCopyOnWriteArraySet();
		this.neighborStateManager = neighborStateManager;
	}

	@Override
	public void executeService(Service service, String outputDest, RunningService callingService) {

		if (service.isLocallyAvailable()) {
			manageLocalExecution(service, outputDest, callingService);
		} else {
			manageRemoteExecution(service, outputDest, callingService);
		}
	}

	private void manageRemoteExecution(Service service, String outputDest, RunningService callingService) {

		ISelfLetID serviceProvider = getServiceProviderFor(service);
		LOG.info("Service " + service + " is not local. Trying with remote resources by redirecting it to " + serviceProvider);

		autonomicAttuator.redirectRequestToProvider(service.getName(), serviceProvider, callingService);
		// fireLocalReqRemoteExeExecuteEvent(service.getName(), parameterMap,
		// serviceProvider, callingService);
	}

	private ISelfLetID getServiceProviderFor(Service service) {
		return neighborStateManager.getNeighborHavingService(service.getName());
	}

	private void manageLocalExecution(Service service, String outputDest, RunningService callingService) {
		// compute whether we need to redirect
		IRedirectCalculator redirectCalculator = redirectCalculatorFactory.create(service, redirectPolicies);
		ISelfLetID redirectedProvider = redirectCalculator.getRedirectedProvider();
		if (redirectCalculator.performRedirect() && redirectedProvider != null) {
			ACTLOG.info(System.currentTimeMillis() + ",REDIRECT_SERVICE");
			autonomicAttuator.redirectRequestToProvider(service.getName(), redirectedProvider, callingService);
		} else {
			fireLocalReqLocalExeExecuteEvent(service.getName(), callingService);
		}
	}

	@Override
	public void setRedirectPolicies(Set<RedirectPolicy> redirectPolicies) {
		this.redirectPolicies.clear();
		this.redirectPolicies.addAll(redirectPolicies);
	}

	private void fireLocalReqLocalExeExecuteEvent(String serviceName, RunningService callingService) {
		dispatchEvent(LocalReqLocalExeExecuteEvent.class, serviceName, callingService);
	}

}
