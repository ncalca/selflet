package it.polimi.elet.selflet.events.service;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;

public class EvaluateOptimizationPolicyEvent extends SelfletEvent {

	private final Double currentTotalNodeUtilization;

	public EvaluateOptimizationPolicyEvent(Double currentTotalNodeUtilization) {
		this.currentTotalNodeUtilization = currentTotalNodeUtilization;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.EVALUATE_OPTIMIZATION_POLICY;
	}

	@Override
	public String toString() {
		return "Evaluate optimization policy. Total utilization: " + currentTotalNodeUtilization;
	}
}
