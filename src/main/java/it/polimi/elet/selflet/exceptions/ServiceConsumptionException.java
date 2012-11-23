package it.polimi.elet.selflet.exceptions;

public class ServiceConsumptionException extends RuntimeException {

	private static final long serialVersionUID = -2526221032173143527L;

	public ServiceConsumptionException(String msg) {
		super(msg);
	}
}
