package it.polimi.elet.selflet.exceptions;

public class DeadStateException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DeadStateException(String message, Throwable cause) {

		super(message, cause);
	}

	public DeadStateException(String message) {

		super(message);
	}

}
