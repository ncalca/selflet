package it.polimi.elet.selflet.exceptions;

public class ConnectionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ConnectionException(String message) {

		super(message);
	}

	public ConnectionException(String message, Throwable cause) {

		super(message, cause);
	}
}
