package it.polimi.elet.selflet.injectorModules;

import it.polimi.elet.selflet.ability.AbilityExecutionEnvironmentOsgi;
import it.polimi.elet.selflet.ability.IAbilityExecutionEnvironment;
import it.polimi.elet.selflet.action.ActionAPIFactory;
import it.polimi.elet.selflet.action.ActionExecutorFactory;
import it.polimi.elet.selflet.action.ActionFactory;
import it.polimi.elet.selflet.action.ActionManager;
import it.polimi.elet.selflet.action.IActionAPIFactory;
import it.polimi.elet.selflet.action.IActionExecutorFactory;
import it.polimi.elet.selflet.action.IActionFactory;
import it.polimi.elet.selflet.action.IActionManager;
import it.polimi.elet.selflet.autonomic.AutonomicActuator;
import it.polimi.elet.selflet.autonomic.AutonomicManagerDrools;
import it.polimi.elet.selflet.autonomic.IAutonomicActuator;
import it.polimi.elet.selflet.autonomic.IAutonomicManager;
import it.polimi.elet.selflet.behavior.IConditionEvaluator;
import it.polimi.elet.selflet.behavior.JEXL2ConditionEvaluator;
import it.polimi.elet.selflet.events.EventDispatcher;
import it.polimi.elet.selflet.events.IEventDispatcher;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.knowledge.IGeneralKnowledge;
import it.polimi.elet.selflet.knowledge.IKnowledgesContainer;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.knowledge.ITypeKnowledge;
import it.polimi.elet.selflet.knowledge.KnowledgeBase;
import it.polimi.elet.selflet.knowledge.Knowledges;
import it.polimi.elet.selflet.knowledge.ServiceKnowledge;
import it.polimi.elet.selflet.knowledge.TypeKnowledge;
import it.polimi.elet.selflet.lifecycle.ISelfletShutdown;
import it.polimi.elet.selflet.lifecycle.SelfletShutdown;
import it.polimi.elet.selflet.message.IMessageHandler;
import it.polimi.elet.selflet.message.IRedsMessageFactory;
import it.polimi.elet.selflet.message.ISelfLetMsgFactory;
import it.polimi.elet.selflet.message.RedsMessageFactory;
import it.polimi.elet.selflet.message.RedsMessageHandler;
import it.polimi.elet.selflet.message.SelfLetMsgFactory;
import it.polimi.elet.selflet.negotiation.INegotiationEventReceiver;
import it.polimi.elet.selflet.negotiation.INegotiationManager;
import it.polimi.elet.selflet.negotiation.INeighborManager;
import it.polimi.elet.selflet.negotiation.IServicePackInstallerFactory;
import it.polimi.elet.selflet.negotiation.NegotiationEventReceiver;
import it.polimi.elet.selflet.negotiation.NegotiationManager;
import it.polimi.elet.selflet.negotiation.NeighborManager;
import it.polimi.elet.selflet.negotiation.messageHandlers.ISelfletMessageHandlerFactory;
import it.polimi.elet.selflet.negotiation.messageHandlers.SelfletMessageHandlerFactory;
import it.polimi.elet.selflet.negotiation.nodeState.INeighborStateManager;
import it.polimi.elet.selflet.negotiation.nodeState.INeighborStateUpdaterTimerTask;
import it.polimi.elet.selflet.negotiation.nodeState.INodeStateFactory;
import it.polimi.elet.selflet.negotiation.nodeState.NeighborStateManager;
import it.polimi.elet.selflet.negotiation.nodeState.NeighborStateUpdaterTimerTask;
import it.polimi.elet.selflet.negotiation.nodeState.NodeStateFactory;
import it.polimi.elet.selflet.negotiation.servicepack.ServicePackInstallerFactory;
import it.polimi.elet.selflet.optimization.IOptimizationManager;
import it.polimi.elet.selflet.optimization.OptimizationManager;
import it.polimi.elet.selflet.optimization.actions.IOptimizationActionActuator;
import it.polimi.elet.selflet.optimization.actions.IOptimizationActionSelector;
import it.polimi.elet.selflet.optimization.actions.OptimizationActionActuator;
import it.polimi.elet.selflet.optimization.actions.OptimizationActionSelector;
import it.polimi.elet.selflet.optimization.actions.redirect.IRedirectCalculatorFactory;
import it.polimi.elet.selflet.optimization.actions.redirect.RedirectCalculatorFactory;
import it.polimi.elet.selflet.optimization.generators.IOptimizationActionGeneratorFactory;
import it.polimi.elet.selflet.optimization.generators.IOptimizationActionGeneratorManager;
import it.polimi.elet.selflet.optimization.generators.OptimizationActionGeneratorFactory;
import it.polimi.elet.selflet.optimization.generators.OptimizationActionGeneratorManager;
import it.polimi.elet.selflet.service.IRunningServiceFactory;
import it.polimi.elet.selflet.service.IRunningServiceManager;
import it.polimi.elet.selflet.service.IServiceExecutor;
import it.polimi.elet.selflet.service.IServiceTeacher;
import it.polimi.elet.selflet.service.RunningServiceFactory;
import it.polimi.elet.selflet.service.RunningServiceManager;
import it.polimi.elet.selflet.service.ServiceExecutor;
import it.polimi.elet.selflet.service.ServiceTeacher;
import it.polimi.elet.selflet.service.serviceEventHandlers.IServiceEventHandlerFactory;
import it.polimi.elet.selflet.service.serviceEventHandlers.ServiceEventHandlerFactory;
import it.polimi.elet.selflet.service.utilization.IPerformanceMonitor;
import it.polimi.elet.selflet.service.utilization.IRedirectMonitor;
import it.polimi.elet.selflet.service.utilization.PerformanceMonitor;
import it.polimi.elet.selflet.service.utilization.RedirectMonitor;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Creates bindings between interfaces and concrete classes which are used by
 * google juice framework.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class SelfLetProductionModule extends AbstractModule {

	private final ISelfLetID selfletID;

	public SelfLetProductionModule(ISelfLetID selfletID) {
		this.selfletID = selfletID;
	}

	/**
	 * This methods maps each interface to a specific class implementation
	 * */
	@Override
	protected void configure() {

		bind(ISelfLetID.class).toInstance(selfletID);

		bindKnowledges();
		bindActionSubSystem();
		bindServiceSubSystem();
		bindPredictionSubSystem();
		bindCommunicationSubSystem();
		bindOptimizationSubSystem();
		bindAutonomicSubSystem();
		bindLifeCycleSubSystem();

		bind(IEventDispatcher.class).to(EventDispatcher.class);
		bind(IAbilityExecutionEnvironment.class).to(AbilityExecutionEnvironmentOsgi.class);
	}

	private void bindLifeCycleSubSystem() {
		bind(ISelfletShutdown.class).to(SelfletShutdown.class);
	}

	private void bindAutonomicSubSystem() {
		bind(IAutonomicManager.class).to(AutonomicManagerDrools.class);
		bind(IAutonomicActuator.class).to(AutonomicActuator.class);
	}

	private void bindOptimizationSubSystem() {
		bind(IOptimizationManager.class).to(OptimizationManager.class);
		bind(IPerformanceMonitor.class).to(PerformanceMonitor.class);
		bind(IOptimizationActionGeneratorManager.class).to(OptimizationActionGeneratorManager.class);
		bind(IOptimizationActionGeneratorFactory.class).to(OptimizationActionGeneratorFactory.class);
		bind(IOptimizationActionActuator.class).to(OptimizationActionActuator.class);
		bind(IOptimizationActionSelector.class).to(OptimizationActionSelector.class);
		bind(IRedirectCalculatorFactory.class).to(RedirectCalculatorFactory.class);
	}

	private void bindCommunicationSubSystem() {
		bind(IMessageHandler.class).to(RedsMessageHandler.class);
		bind(INodeStateFactory.class).to(NodeStateFactory.class);
		bind(INegotiationManager.class).to(NegotiationManager.class);
		bind(INeighborStateUpdaterTimerTask.class).to(NeighborStateUpdaterTimerTask.class);
		bind(ISelfletMessageHandlerFactory.class).to(SelfletMessageHandlerFactory.class);
		bind(INeighborStateManager.class).to(NeighborStateManager.class);
		bind(IRedsMessageFactory.class).to(RedsMessageFactory.class);
		bind(INegotiationEventReceiver.class).to(NegotiationEventReceiver.class);
		bind(INeighborManager.class).to(NeighborManager.class);
	}

	private void bindPredictionSubSystem() {
	}

	private void bindServiceSubSystem() {
		bind(IServicePackInstallerFactory.class).to(ServicePackInstallerFactory.class);
		bind(IRunningServiceManager.class).to(RunningServiceManager.class);
		bind(IRunningServiceFactory.class).to(RunningServiceFactory.class);
		bind(IServiceTeacher.class).to(ServiceTeacher.class);
		bind(IServiceExecutor.class).to(ServiceExecutor.class);
		bind(IServiceEventHandlerFactory.class).to(ServiceEventHandlerFactory.class);
		bind(IRedirectMonitor.class).to(RedirectMonitor.class);
	}

	private void bindActionSubSystem() {
		bind(IActionFactory.class).to(ActionFactory.class);
		bind(IActionManager.class).to(ActionManager.class);
		bind(IActionAPIFactory.class).to(ActionAPIFactory.class);
		bind(IActionExecutorFactory.class).to(ActionExecutorFactory.class);
		bind(IConditionEvaluator.class).to(JEXL2ConditionEvaluator.class);
	}

	private void bindKnowledges() {
		bind(IGeneralKnowledge.class).to(KnowledgeBase.class);
		bind(IServiceKnowledge.class).to(ServiceKnowledge.class);
		bind(ITypeKnowledge.class).to(TypeKnowledge.class);
		bind(IKnowledgesContainer.class).to(Knowledges.class);
	}

	@Provides
	@Singleton
	public ISelfLetMsgFactory getSelfLetMsgFactory() {
		return new SelfLetMsgFactory(selfletID);
	}

}
