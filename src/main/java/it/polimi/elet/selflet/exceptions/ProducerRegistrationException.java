package it.polimi.elet.selflet.exceptions;

public class ProducerRegistrationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProducerRegistrationException(String message) {
		super(message);
	}

	public ProducerRegistrationException(String message, Throwable cause) {
		super(message, cause);
	}
}