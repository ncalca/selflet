package it.polimi.elet.selflet.negotiation;

import java.io.Serializable;

/**
 * Class representing a negotiation error
 * 
 * @author silvia
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 */
public class NegotiationErrorParam implements Serializable {

	private static final long serialVersionUID = 1L;

	private String message;
	private Throwable cause;

	public NegotiationErrorParam(String message, Throwable cause) {
		this.message = message;
		this.cause = cause;
	}

	public String getMessage() {
		return message;
	}

	public Throwable getCause() {
		return cause;
	}

	@Override
	public String toString() {
		return message;
	}
}
