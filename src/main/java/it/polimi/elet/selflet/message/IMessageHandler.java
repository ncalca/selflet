package it.polimi.elet.selflet.message;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.IEventProducer;
import it.polimi.elet.selflet.events.ISelfletComponent;
import it.polimi.elet.selflet.exceptions.ConnectionException;

import java.util.List;

import polimi.reds.MessageID;

/**
 * This is the interface of the Message Handler, which handles communication
 * between the Selflet and the other Selflet which are present in the network.
 * 
 * @author Daniel Dubois
 * @author Davide Devescovi
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 */
public interface IMessageHandler extends IEventProducer, ISelfletComponent {

	/**
	 * Connects this communicator to the broker
	 * 
	 * @param address
	 *            the address to connect to
	 * @param port
	 *            the port to connect to
	 * 
	 * @throws ConnectionException
	 *             if an error occurs during connection
	 */
	void connect(String address, Integer port) throws ConnectionException;

	/**
	 * Disconnects the Message Handler.
	 */
	void disconnect();

	/**
	 * Sends a message; the message itself contains information about sender,
	 * destination, commands and parameters. The method returns an Object which,
	 * depending on the implementation, represents the unique ID of the message
	 * which was sent.
	 * 
	 * @param message
	 *            the message to send
	 * 
	 * @return the ID of the message which was sent
	 */
	MessageID send(SelfLetMsg message);

	/**
	 * Sends a reply to a previously received message. The method returns an
	 * Object which, depending on the implementation, represents the unique ID
	 * of the message which was sent.
	 * 
	 * @param reply
	 *            the message to be sent as a reply
	 * @param originalID
	 *            the ID of the message to reply to
	 * 
	 * @return the ID of the message which was sent
	 */
	Object reply(SelfLetMsg reply, Object originalID);

	/**
	 * Tries to receive a message for the specified amount of time.
	 * 
	 * @param timeout
	 *            wait time in milliseconds
	 * 
	 * @return the received message
	 * 
	 * @throws NotFoundException
	 *             if no message is received before the timeout
	 */
	SelfLetMsg retrieveMessage(SelfLetMessageTypeEnum messageType, int timeout);

	/**
	 * Tries to receive a reply to the specified message for the specified
	 * amount of time.
	 * 
	 * @param originalID
	 *            the ID of the message which is being replied to
	 * 
	 * @param timeout
	 *            wait time in milliseconds
	 * 
	 * @return the received reply
	 * 
	 * @throws NotFoundException
	 *             if no reply is received before the timeout
	 */
	SelfLetMsg retrieveReply(Object originalID, int timeout);

	/**
	 * Tries to receive a list of replies to the specified message for a given
	 * amount of time.
	 * 
	 * @param originalID
	 *            the ID of the message which is being replied to
	 * @param timeout
	 *            wait time in milliseconds
	 * 
	 * @return a List of received replies
	 * 
	 * @throws NotFoundException
	 *             if no reply is received before the timeout
	 */
	List<SelfLetMsg> retrieveReplies(Object originalID, int timeout);

	/**
	 * Binds a specific message type to an event type. This way, when a message
	 * of the specified type is received, the Message Handler will produce an
	 * event of the specified type.
	 * 
	 * @param messageType
	 *            the message type to bind
	 * @param eventType
	 *            the event subtype to be associated to the message type (the
	 *            main type will always be the standard message event type)
	 * 
	 * @throws AlreadyBoundException
	 *             if the type is already bound
	 * @throws ProducerRegistrationException
	 *             if an error occurs while registering the event type
	 */
	void bindMessagesToEvents(SelfLetMessageTypeEnum messageType, EventTypeEnum eventType);

	/**
	 * Unbinds a message type. No more events will be sent when a message with
	 * that message type is received.
	 * 
	 * @param messageType
	 *            The message command to unbind
	 * 
	 * @throws ProducerRegistrationException
	 *             if an error occurs while unregistering the event type
	 */
	void unbindMessagesToEvent(String messageType);

	/**
	 * Gets the address to which the message handler is currently connected.
	 * 
	 * @return a String representing the address
	 */
	String getAddress();

	/**
	 * Gets the port to which the message handler is currently connected.
	 * 
	 * @return an Integer representing the port
	 */
	Integer getPort();

}