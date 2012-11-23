package it.polimi.elet.selflet.message;

import java.io.Serializable;

import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.negotiation.ServicePack;
import it.polimi.elet.selflet.negotiation.NeededServiceParam;
import it.polimi.elet.selflet.negotiation.RemoteServiceCompleted;
import it.polimi.elet.selflet.negotiation.ServiceExecutionParameter;
import it.polimi.elet.selflet.negotiation.ServiceOfferModeEnum;
import it.polimi.elet.selflet.negotiation.ServiceProvider;
import it.polimi.elet.selflet.negotiation.nodeState.INodeState;

/**
 * Interface for selflet msg factory
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface ISelfLetMsgFactory {

	/**
	 * Creates an error message with a error string and the exception caused by
	 * the error
	 * 
	 * @param receiverId
	 *            the node receiving the msg
	 * @param message
	 *            a string representing the error
	 * @param exception
	 *            raised by the error, it can be null
	 */
	SelfLetMsg newNegotiationErrorMsg(ISelfLetID receiverId, String message, Throwable cause);

	/**
	 * Creates message asking the node state
	 * 
	 * @param receiverId
	 *            the node to which we are asking the state
	 * */
	SelfLetMsg newNodeStateMsg(ISelfLetID receiverId, INodeState nodeState);

	/**
	 * Creates message advertising an achievable service in broadcast
	 * 
	 * @param achievableServiceParam
	 *            the service to be advertised
	 * */
	SelfLetMsg newAdvertiseAchievableServiceMsg(String achievableService, ServiceOfferModeEnum mode);

	/**
	 * Creates message asking to a node the send the default implementation of a
	 * service
	 * 
	 * @param nodeId
	 *            node that need to execute the service
	 * @param serviceName
	 *            the service that is asked
	 * */
	SelfLetMsg newGetServiceMsg(ISelfLetID nodeId, String serviceName);

	/**
	 * Creates a message containing the servicePack of a service to be taught
	 * 
	 * TODO However right now it does not include all the abilities, actions and
	 * conditions related to this behavior
	 * 
	 * @param nodeId
	 *            the node receiving this request
	 * @param servicePack
	 *            the behavior to be sent
	 * */
	SelfLetMsg newGetServiceMsgReply(ISelfLetID nodeId, ServicePack servicePack);

	/**
	 * Creates message representing an ack for a previously asked service
	 * 
	 * @param nodeId
	 *            the node that executed the service
	 * @param serviceName
	 *            the service to be acknowledged
	 * */
	SelfLetMsg newAckServiceReceivedMsg(ISelfLetID nodeId, String serviceName);

	/**
	 * Creates message to ask the service execution in a node
	 * 
	 * @param receiverId
	 *            the remote node that will execute the service
	 * @param serviceParam
	 *            parameter for the service to be executed
	 * */
	SelfLetMsg newExecuteAchievableServiceMsg(ISelfLetID receiverId, ServiceExecutionParameter serviceParam);

	/**
	 * Creates a reply message containing the mode for an asked service
	 * 
	 * @param receiverId
	 *            the node receiving this message
	 * @param offerMode
	 *            offer mode for the asked service
	 * 
	 * */
	SelfLetMsg newAskNeededServiceReplyMsg(ISelfLetID receiverId, ServiceOfferModeEnum offerMode);

	/**
	 * Creates a reply message containing the providers offering a previously
	 * asked service
	 * 
	 * @param receiverId
	 *            the node receiving this message
	 * @param provider
	 *            offering the service
	 * */
	SelfLetMsg newGetServiceProviderIndirectlyReplyMsg(ISelfLetID receiverId, ServiceProvider serviceProvider);

	/**
	 * Creates a reply message after the execution of a service. The message
	 * contains the cost and the output
	 * 
	 * @param receiverId
	 *            the node receiving the state
	 * @param remoteServiceCompleted
	 *            details of the completed service
	 * 
	 * */
	SelfLetMsg newExecuteAchievableServiceReplyMsg(ISelfLetID receiverId, RemoteServiceCompleted remoteServiceCompleted);

	/**
	 * Creates a broadcast message asking for the execution of a service
	 * 
	 * @param neededService
	 *            the service needed
	 * */
	SelfLetMsg newAskNeededServiceMsg(NeededServiceParam neededService);

	/**
	 * Creates a message asking for the indirect providers of a given service
	 * 
	 * @param providerId
	 *            the receiver of this message
	 * @param neededServiceParam
	 *            details of the service
	 * */
	SelfLetMsg newGetIndirectlyServiceProviderMsg(ISelfLetID providerId, NeededServiceParam neededServiceParam);

	/**
	 * Creates an ALIVE message which is used by the request dispatcher to known
	 * active selflets
	 * */
	SelfLetMsg newAliveMessage();

	/**
	 * Creates a new message representing a redirect requeste
	 * */
	SelfLetMsg newRedirectRequestMsg(ServiceExecutionParameter serviceParam, ISelfLetID receiverProvider);

	/**
	 * Creates a new message containing a service pack that is used to install
	 * the new service
	 * */
	SelfLetMsg newServiceTeachMsg(ServicePack servicePack, ISelfLetID provider);

	SelfLetMsg newIstantiateNewSelfletMsg();

	SelfLetMsg newRedirectRequestReplyMsg(ISelfLetID receiverID, Serializable msgContent);

	/**
	 * Message used to notify the dispatcher that this selflet is removing
	 * */
	SelfLetMsg newRemovalSelfletMessage(ISelfLetID receiver);

}
