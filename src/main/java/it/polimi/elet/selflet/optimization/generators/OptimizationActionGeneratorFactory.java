package it.polimi.elet.selflet.optimization.generators;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import it.polimi.elet.selflet.autonomic.IAutonomicActuator;
import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.knowledge.IKnowledgesContainer;
import it.polimi.elet.selflet.negotiation.nodeState.INeighborStateManager;
import it.polimi.elet.selflet.optimization.actions.OptimizationActionTypeEnum;
import it.polimi.elet.selflet.service.utilization.IPerformanceMonitor;
import it.polimi.elet.selflet.service.utilization.IRedirectMonitor;

/**
 * An implementation of optimization action generator factory
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class OptimizationActionGeneratorFactory implements IOptimizationActionGeneratorFactory {

	private final INeighborStateManager neighborStateManager;
	private final IPerformanceMonitor performanceMonitor;
	private final IKnowledgesContainer knowledges;
	private final IRedirectMonitor redirectMonitor;
	private final IAutonomicActuator autonomicAttuator;

	@Inject
	public OptimizationActionGeneratorFactory(INeighborStateManager neighborStateManager, IPerformanceMonitor performanceMonitor,
			IKnowledgesContainer knowledges, IRedirectMonitor redirectMonitor, IAutonomicActuator autonomicAttuator) {
		this.neighborStateManager = neighborStateManager;
		this.performanceMonitor = performanceMonitor;
		this.knowledges = knowledges;
		this.redirectMonitor = redirectMonitor;
		this.autonomicAttuator = autonomicAttuator;
	}

	@Override
	public IActionGenerator createActionGenerator(OptimizationActionTypeEnum actionType) {

		switch (actionType) {

		case REDIRECT_SERVICE:
			return new RedirectServiceActionGenerator(neighborStateManager, performanceMonitor, knowledges.getServiceKnowledge(), redirectMonitor);

		case ADD_SELFLET:
			return new IstantiateNewSelfletActionGenerator(neighborStateManager, performanceMonitor, autonomicAttuator);

		case REMOVE_SELFLET:
			return new RemoveSelfletActionGenerator(neighborStateManager, performanceMonitor, knowledges.getServiceKnowledge());

		case TEACH_SERVICE:
			return new TeachActionGenerator(neighborStateManager, knowledges.getServiceKnowledge(), performanceMonitor);

		case CHANGE_SERVICE_IMPLEMENTATION:
			return new ChangeServiceImplementationGenerator(neighborStateManager, performanceMonitor, knowledges.getServiceKnowledge());

		default:
			throw new NotFoundException("Trying to instantiate a non existent type of action generator " + actionType);
		}

	}

}
