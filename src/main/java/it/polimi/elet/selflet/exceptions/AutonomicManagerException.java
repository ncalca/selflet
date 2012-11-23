package it.polimi.elet.selflet.exceptions;

public class AutonomicManagerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AutonomicManagerException(String message) {
		super(message);
	}

	public AutonomicManagerException(String message, Throwable cause) {
		super(message, cause);
	}

}
