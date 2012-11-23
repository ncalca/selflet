package it.polimi.elet.selflet.exceptions;

public class OptimizationActionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OptimizationActionException(String message) {
		super(message);
	}

	public OptimizationActionException(String message, Throwable cause) {
		super(message, cause);
	}

}
