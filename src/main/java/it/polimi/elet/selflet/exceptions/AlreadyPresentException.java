package it.polimi.elet.selflet.exceptions;

public class AlreadyPresentException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AlreadyPresentException(String message) {

		super(message);
	}

}
