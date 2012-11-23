package it.polimi.elet.selflet;

import static com.google.common.base.Strings.nullToEmpty;
import it.polimi.elet.selflet.ability.AbstractAbility;
import it.polimi.elet.selflet.ability.IAbilityExecutionEnvironment;
import it.polimi.elet.selflet.action.IActionFactory;
import it.polimi.elet.selflet.action.IActionManager;
import it.polimi.elet.selflet.autonomic.IAutonomicActuator;
import it.polimi.elet.selflet.autonomic.IAutonomicManager;
import it.polimi.elet.selflet.configuration.SelfletConfiguration;
import it.polimi.elet.selflet.events.IEventDispatcher;
import it.polimi.elet.selflet.events.ISelfletComponent;
import it.polimi.elet.selflet.events.service.LocalReqLocalExeExecuteEvent;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.id.SelfLetID;
import it.polimi.elet.selflet.injectorModules.GuiceModuleFactory;
import it.polimi.elet.selflet.knowledge.IGeneralKnowledge;
import it.polimi.elet.selflet.knowledge.IKnowledgesContainer;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.lifecycle.ISelfletShutdown;
import it.polimi.elet.selflet.message.IMessageHandler;
import it.polimi.elet.selflet.message.SelfLetAliveTimerTask;
import it.polimi.elet.selflet.negotiation.INegotiationEventReceiver;
import it.polimi.elet.selflet.negotiation.INegotiationManager;
import it.polimi.elet.selflet.negotiation.NegotiationEventReceiver;
import it.polimi.elet.selflet.negotiation.nodeState.INeighborStateManager;
import it.polimi.elet.selflet.negotiation.nodeState.INeighborStateUpdaterTimerTask;
import it.polimi.elet.selflet.optimization.IOptimizationManager;
import it.polimi.elet.selflet.service.IRunningServiceManager;
import it.polimi.elet.selflet.service.IServiceExecutor;
import it.polimi.elet.selflet.service.utilization.IPerformanceMonitor;
import it.polimi.elet.selflet.service.utilization.UtilizationCheckerTimerTask;
import it.polimi.elet.selflet.threadUtilities.PeriodicThreadStarter;
import it.polimi.elet.selflet.utilities.ThreadUtilities;
import it.polimi.elet.selflet.utilities.UtilitiesProvider;
import it.polimi.elet.selflet.utilities.parser.ServiceType;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

/**
 * This SelfLetInstance is a container for all the SelfLet components; it is
 * used to setup and activate a SelfLet, and to interact with it by adding or
 * removing Rules and setting Properties.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * @author Davide Devescovi
 * @author Silvia Bindelli
 */
public class SelfletInstance {

	private static final Logger LOG = Logger.getLogger(SelfletInstance.class);

	public static long startTime;

	private final Injector injector;

	private final ISelfLetID selfLetId;

	/** Internal subsystems */
	private final IActionManager actionManager;
	private final IAbilityExecutionEnvironment abilityExecutionEnvironment;
	private final IAutonomicManager autonomicManager;
	private final IAutonomicActuator autonomicAttuator;
	private final IEventDispatcher dispatcher;
	private final INegotiationManager negotiationManager;
	private final IMessageHandler messageHandler;
	private final IRunningServiceManager runningServiceManager;
	private final IPerformanceMonitor performanceMonitor;
	private final INegotiationEventReceiver negotiationEventReceiver;
	private final IActionFactory actionFactory;
	private final INeighborStateManager neighborStateManager;
	private final IServiceExecutor serviceExecutor;

	/** Knowledges */
	private final IKnowledgesContainer knowledges;
	private final IGeneralKnowledge generalKnowledge;
	private final IServiceKnowledge serviceKnowledge;

	/**
	 * Constructs an SelfLet with the given ID, type, list of neighbors and
	 * logging states.
	 * 
	 * @param selfLetIdString
	 *            the ID of the SelfLet
	 * @param selfletType
	 *            the type to which the SelfLet belongs
	 * @param neighbors
	 *            a List of IDs, representing the known neighbors of the SelfLet
	 * @param services
	 *            the list of services installed within the selflet
	 * @param workingDir
	 *            the base directory where any component of the current SelfLet
	 *            is stored
	 * @param ruleFilePath
	 *            the rule file path for the autonomic manager
	 */
	public SelfletInstance(String selfLetIdString, String selfletType, List<ServiceType> services, String workingDir, List<String> ruleFiles) {
		UtilitiesProvider.setWorkingDir(workingDir);

		this.selfLetId = new SelfLetID(selfLetIdString);
		this.injector = GuiceModuleFactory.buildProductionModule(selfLetId);

		this.dispatcher = injector.getInstance(IEventDispatcher.class);
		this.knowledges = injector.getInstance(IKnowledgesContainer.class);
		this.generalKnowledge = knowledges.getGeneralKnowledge();
		this.serviceKnowledge = knowledges.getServiceKnowledge();
		this.messageHandler = injector.getInstance(IMessageHandler.class);
		this.performanceMonitor = injector.getInstance(IPerformanceMonitor.class);
		this.actionManager = injector.getInstance(IActionManager.class);
		this.abilityExecutionEnvironment = injector.getInstance(IAbilityExecutionEnvironment.class);
		this.negotiationManager = injector.getInstance(INegotiationManager.class);
		this.runningServiceManager = injector.getInstance(IRunningServiceManager.class);
		this.autonomicManager = injector.getInstance(IAutonomicManager.class);
		this.autonomicAttuator = injector.getInstance(IAutonomicActuator.class);
		this.negotiationEventReceiver = injector.getInstance(NegotiationEventReceiver.class);
		this.actionFactory = injector.getInstance(IActionFactory.class);
		this.neighborStateManager = injector.getInstance(INeighborStateManager.class);
		this.serviceExecutor = injector.getInstance(IServiceExecutor.class);

		registerSelfletComponents();

		// TODO fix this, use add rule
		// method (move to the startup method... but check first if that works!)
		this.autonomicManager.addRuleFiles(ruleFiles);

		ServiceInitializer serviceInitializer = new ServiceInitializer(serviceKnowledge, actionFactory);
		serviceInitializer.addServices(services);
	}

	/**
	 * Starts up the SelfLet, connecting it to the given address and port, and
	 * installing the specified Rules, Behaviour and set of Abilities.
	 * 
	 * @param redsAddress
	 *            the address the SelfLet has to connect to
	 * @param redsPort
	 *            the port the SelfLet has to connect to
	 * @param workingDir
	 *            the paths of the behaviors added to the SelfLet
	 * @param initialbehaviourPath
	 *            the path to the initial SelfLet behavior file
	 * @param abilities
	 *            a List of AbstractAbility objects, representing the Abilities
	 *            to be installed in the SelfLet upon startup...
	 * @param initialService
	 *            the name of the service to be ran at the initialization of the
	 *            selflet
	 * @param dummyRequestFile
	 *            path of the file containing the request track that the selflet
	 *            should be perform. For testing/experimental reasons... it
	 *            should be removed from here
	 */

	public void startup(String redsAddress, int redsPort, int limePort, List<AbstractAbility> abilities, String initialService) {

		LOG.info("Starting selflet...");
		startTime = System.currentTimeMillis();

		LOG.info("Starting dispatcher");
		ThreadUtilities.submitGenericJob(dispatcher);

		messageHandler.connect(redsAddress, redsPort);

		LOG.info("Starting negotiation event receiver");
		ThreadUtilities.submitGenericJob(negotiationEventReceiver);

		autonomicManager.start();
		abilityExecutionEnvironment.startEnvironment();
		setAbilities(abilities);

		startPeriodicThreads();

		LOG.info("<-- Initialization phase completed ! -->\n\n");
		startInitialService(initialService);

	}

	private void startPeriodicThreads() {
		int initialDelay = SelfletConfiguration.getSingleton().initialDelayAfterSelfletInitializationInMs;
		PeriodicThreadStarter periodicThreadsStarter = new PeriodicThreadStarter(initialDelay);

		periodicThreadsStarter.addPeriodicTask(injector.getInstance(UtilizationCheckerTimerTask.class));
		periodicThreadsStarter.addPeriodicTask(injector.getInstance(SelfLetAliveTimerTask.class));
		periodicThreadsStarter.addPeriodicTask(injector.getInstance(INeighborStateUpdaterTimerTask.class));
		periodicThreadsStarter.addPeriodicTask(injector.getInstance(IOptimizationManager.class));

		injector.getInstance(ISelfletShutdown.class).addPeriodicThreadStarter(periodicThreadsStarter);
		periodicThreadsStarter.start();
	}

	private void startInitialService(String initialService) {
		initialService = nullToEmpty(initialService);
		UtilitiesProvider.setInitialServiceName(initialService);

		if (initialService.isEmpty()) {
			LOG.info("No initial service set. Waiting for service requests");
			return;
		}

		LOG.info("Starting initial service " + initialService);
		// to start the initial service just generate a service execute event
		// with a null calling behavior
		dispatcher.dispatchEvent(new LocalReqLocalExeExecuteEvent(initialService, null));
	}

	private void setAbilities(List<AbstractAbility> abilities) {
		for (AbstractAbility ability : abilities) {
			abilityExecutionEnvironment.addAbility(ability);
		}
	}

	/**
	 * Stops and disconnects the SelfLet.
	 */
	public void stop() {
		LOG.debug("Stopping selfLet ");
		abilityExecutionEnvironment.stopEnvironment();
		messageHandler.disconnect();
	}

	private void registerSelfletComponents() {

		ImmutableSet<ISelfletComponent> componentsToRegister = ImmutableSet.of(messageHandler, abilityExecutionEnvironment, generalKnowledge,
				negotiationManager, autonomicManager, actionManager, runningServiceManager, negotiationEventReceiver, messageHandler, autonomicAttuator,
				performanceMonitor, neighborStateManager, serviceExecutor);

		for (ISelfletComponent component : componentsToRegister) {
			dispatcher.registerSelfLetComponent(component);
		}
	}

	public void setBaseProperty(String name, Object value) {
		generalKnowledge.setOrUpdateProperty(name, value);
	}

	public long getStartTime() {
		return startTime;
	}

}
