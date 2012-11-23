package it.polimi.elet.selflet.exceptions;

public class NegotiationErrorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NegotiationErrorException(String message) {
		super(message);
	}

	public NegotiationErrorException(String message, Throwable cause) {
		super(message, cause);
	}
}
