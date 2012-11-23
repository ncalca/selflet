package it.polimi.elet.selflet.exceptions;

public class AbilityEnvironmentException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AbilityEnvironmentException(String message) {
		super(message);
	}

	public AbilityEnvironmentException(String message, Throwable cause) {
		super(message, cause);
	}

}
