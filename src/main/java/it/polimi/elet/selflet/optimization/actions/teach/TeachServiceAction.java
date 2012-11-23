package it.polimi.elet.selflet.optimization.actions.teach;

import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.optimization.actions.IOptimizationAction;
import it.polimi.elet.selflet.optimization.actions.OptimizationActionTypeEnum;
import it.polimi.elet.selflet.service.Service;

public class TeachServiceAction implements IOptimizationAction {

	private final ISelfLetID receiverSelflet;
	private final Service service;
	private final double weight;

	public TeachServiceAction(ISelfLetID receiverSelflet, Service service, double weight) {
		this.receiverSelflet = receiverSelflet;
		this.service = service;
		this.weight = Math.max(weight, MIN_WEIGHT);
	}

	@Override
	public OptimizationActionTypeEnum optimizationType() {
		return OptimizationActionTypeEnum.TEACH_SERVICE;
	}

	public ISelfLetID getReceiverSelflet() {
		return receiverSelflet;
	}

	public Service getService() {
		return service;
	}

	@Override
	public double weight() {
		return weight;
	}

	@Override
	public String toString() {
		return "Teach service " + service + " to selflet " + receiverSelflet;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((receiverSelflet == null) ? 0 : receiverSelflet.hashCode());
		result = prime * result + ((service == null) ? 0 : service.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TeachServiceAction)) {
			return false;
		}
		TeachServiceAction other = (TeachServiceAction) obj;
		if (receiverSelflet == null) {
			if (other.receiverSelflet != null) {
				return false;
			}
		} else if (!receiverSelflet.equals(other.receiverSelflet)) {
			return false;
		}
		if (service == null) {
			if (other.service != null) {
				return false;
			}
		} else if (!service.equals(other.service)) {
			return false;
		}
		return true;
	}

}
