package it.polimi.elet.selflet.optimization.actions.changeImplementation;

import it.polimi.elet.selflet.optimization.actions.IOptimizationAction;
import it.polimi.elet.selflet.optimization.actions.OptimizationActionTypeEnum;
import it.polimi.elet.selflet.service.Service;

/**
 * Action chaning service implementation
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ChangeServiceImplementationAction implements IOptimizationAction {

	private Service service;
	private int behaviorQuality;
	private double weight;
	
	@Override
	public OptimizationActionTypeEnum optimizationType() {
		return OptimizationActionTypeEnum.CHANGE_SERVICE_IMPLEMENTATION;
	}
	
	public ChangeServiceImplementationAction(Service service, int behaviorQuality, double weight){
		this.service =  service;
		this.behaviorQuality = behaviorQuality;
		this.weight = weight;
	}
	
	public Service getService(){
		return this.service;
	}
	
	public int getBehaviorQuality(){
		return this.behaviorQuality;
	}

	@Override
	public double weight() {
		return this.weight;
	}

}
