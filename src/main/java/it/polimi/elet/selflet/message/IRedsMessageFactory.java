package it.polimi.elet.selflet.message;

public interface IRedsMessageFactory {

	/**
	 * Returns a <code>RedsMessage</code> encapsulating a
	 * <code>SelfLetMsg</code> message.
	 * 
	 * @param message
	 *            the <code>SelfLetMsg</code> to encapsulate
	 * */

	RedsMessage createNewRedsMessage(SelfLetMsg message);

}