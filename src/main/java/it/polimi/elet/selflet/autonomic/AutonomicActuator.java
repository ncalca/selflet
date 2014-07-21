package it.polimi.elet.selflet.autonomic;


import it.polimi.elet.selflet.ability.IAbilityExecutionEnvironment;
import it.polimi.elet.selflet.events.SelfletComponent;
import it.polimi.elet.selflet.events.service.ServiceNeededEvent;
import it.polimi.elet.selflet.exceptions.NotImplementedExeception;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.knowledge.IGeneralKnowledge;
import it.polimi.elet.selflet.knowledge.IKnowledgesContainer;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.lifecycle.ISelfletShutdown;
import it.polimi.elet.selflet.negotiation.INegotiationManager;
import it.polimi.elet.selflet.negotiation.ServiceAskModeEnum;
import it.polimi.elet.selflet.negotiation.ServiceExecutionParameter;
import it.polimi.elet.selflet.negotiation.ServiceOfferModeEnum;
import it.polimi.elet.selflet.service.IServiceExecutor;
import it.polimi.elet.selflet.service.IServiceImplementationChanger;
import it.polimi.elet.selflet.service.IServiceTeacher;
import it.polimi.elet.selflet.service.RunningService;
import it.polimi.elet.selflet.service.Service;
import it.polimi.elet.selflet.service.ServiceParametersExtractor;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * An implementation for the autonomic actuator
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * @author silvia
 */
@Singleton
public class AutonomicActuator extends SelfletComponent implements
		IAutonomicActuator {

	private static final Logger LOG = Logger.getLogger(AutonomicActuator.class);

	private final IServiceKnowledge serviceKnowledge;
	private final IGeneralKnowledge generalKnowledge;
	private final INegotiationManager negotiationManager;
	private final IServiceTeacher serviceTeacher;
	private final IServiceExecutor serviceExecutor;
	private final IAbilityExecutionEnvironment abilityExecutionEnvironment;
	private final ISelfletShutdown selfletShutdown;
	private final IServiceImplementationChanger serviceImplementationChanger;

	private long lastTimeIstantiatedSelflet = 0;

	@Inject
	public AutonomicActuator(IKnowledgesContainer knowledges,
			INegotiationManager negotiationManager,
			IAbilityExecutionEnvironment abilityExecutionEnvironment,
			IServiceTeacher serviceTeacher, IServiceExecutor serviceExecutor,
			ISelfletShutdown selfletShutdown,
			IServiceImplementationChanger serviceImplementationChanger) {
		this.serviceKnowledge = knowledges.getServiceKnowledge();
		this.generalKnowledge = knowledges.getGeneralKnowledge();
		this.negotiationManager = negotiationManager;
		this.abilityExecutionEnvironment = abilityExecutionEnvironment;
		this.serviceTeacher = serviceTeacher;
		this.serviceExecutor = serviceExecutor;
		this.selfletShutdown = selfletShutdown;
		this.serviceImplementationChanger = serviceImplementationChanger;
	}

	public void executeService(String serviceName, String outputDest,
			RunningService callingService) {
		addServiceIfNotKnown(serviceName);
		Service service = serviceKnowledge.getProperty(serviceName);
		serviceExecutor.executeService(service, outputDest, callingService);
	}

	public void modifyServiceOfferMode(String serviceName,
			ServiceOfferModeEnum mode, boolean active) {
		negotiationManager.setAchievableServiceOfferMode(serviceName, mode,
				active);
		negotiationManager.advertiseAchievableService(serviceName, mode);
	}

	@Override
	public void redirectRequest(ServiceNeededEvent serviceNeededEvent,
			RunningService callingService) {
		throw new NotImplementedExeception("redirectRequest");
	}

	@Override
	public void redirectRequestToProvider(
			ServiceNeededEvent serviceNeededEvent, ISelfLetID provider,
			RunningService callingService) {
		redirectRequestToProvider(serviceNeededEvent.getServiceName(),
				provider, callingService);
	}

	@Override
	public void redirectRequestToProvider(String serviceName,
			ISelfLetID provider, RunningService callingService) {
		LOG.debug("Redirecting request for service " + serviceName
				+ " to provider " + provider);
		ServiceParametersExtractor serviceParametersExtractor = new ServiceParametersExtractor(
				serviceName, serviceKnowledge, generalKnowledge);
		ServiceExecutionParameter serviceExecutionParameter = new ServiceExecutionParameter(
				serviceName, serviceParametersExtractor.getServiceParameters());
		negotiationManager.redirectRequestToProvider(serviceExecutionParameter,
				provider, callingService);
	}

	@Override
	public void istantiateNewSelflet() {
		negotiationManager.istantiateNewSelflet();
		lastTimeIstantiatedSelflet = System.currentTimeMillis();
	}

	@Override
	public void removeSelflet() {
		selfletShutdown.shutDown();
	}

	public void modifyServiceAskMode(String service, ServiceAskModeEnum mode) {
		throw new NotImplementedExeception(
				"Modify service ask mode is not implemented yet");
	}

	@Override
	public void teachServiceToProvider(String serviceName, ISelfLetID provider) {
		Service service = serviceKnowledge.getProperty(serviceName);
		LOG.debug("Teaching service " + service + " to " + provider);
		serviceTeacher.teach(service, provider);
	}

	public void removeBehavior(String behavior) {
		throw new NotImplementedExeception("remove behavior");
	}

	public void addBehaviorAndService(String fileName, String serviceName) {
		throw new NotImplementedExeception("add behavior and service");
	}

	public void enableService(String serviceName) {
		if (serviceKnowledge.isPropertyPresent(serviceName)) {
			Service service = serviceKnowledge.getProperty(serviceName);
			service.enable();
		}
	}

	public void disableService(String serviceName) {
		if (serviceKnowledge.isPropertyPresent(serviceName)) {
			Service service = serviceKnowledge.getProperty(serviceName);
			service.disable();
		}
	}

	public void installAbility(String inputFileName, String abilityName,
			String serviceName, String methodName) {
		abilityExecutionEnvironment.installAbility(inputFileName, abilityName,
				serviceName, methodName);
	}

	public void restartDefault() {
		throw new NotImplementedExeception("Restart default");
	}

	private void addServiceIfNotKnown(String serviceName) {
		if (serviceKnowledge.isPropertyPresent(serviceName)) {
			return;
		}
		serviceKnowledge.setProperty(serviceName, new Service(serviceName));
	}

	@Override
	public long getLastTimeIstantiatedSelflet() {
		return lastTimeIstantiatedSelflet;
	}

	@Override
	public void changeServiceImplementation(String serviceName,
			int qualityOfBehavior) {
		serviceImplementationChanger.changeServiceImplementation(serviceName, qualityOfBehavior);
	}

}
