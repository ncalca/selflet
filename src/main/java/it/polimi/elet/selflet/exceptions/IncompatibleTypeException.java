package it.polimi.elet.selflet.exceptions;

public class IncompatibleTypeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IncompatibleTypeException(String message) {

		super(message);
	}

	public IncompatibleTypeException(String message, Throwable cause) {

		super(message, cause);
	}
}
