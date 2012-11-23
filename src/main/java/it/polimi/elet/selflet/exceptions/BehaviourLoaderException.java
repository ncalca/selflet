package it.polimi.elet.selflet.exceptions;

public class BehaviourLoaderException extends Exception {

	private static final long serialVersionUID = 1L;

	public BehaviourLoaderException(String message) {

		super(message);
	}

	public BehaviourLoaderException(String message, Throwable cause) {

		super(message, cause);
	}
}
