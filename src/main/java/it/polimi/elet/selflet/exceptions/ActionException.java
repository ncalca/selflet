package it.polimi.elet.selflet.exceptions;

/**
 * Genering runtime exception used within the context of actions
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ActionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ActionException(String message) {
		super(message);
	}

	public ActionException(String message, Throwable cause) {
		super(message, cause);
	}
}
