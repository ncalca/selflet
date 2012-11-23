package it.polimi.elet.selflet.message;

import java.io.Serializable;

import it.polimi.elet.selflet.id.BroadcastSelfLetID;
import it.polimi.elet.selflet.id.DispatcherID;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.negotiation.AchievableServiceParam;
import it.polimi.elet.selflet.negotiation.ServicePack;
import it.polimi.elet.selflet.negotiation.NeededServiceParam;
import it.polimi.elet.selflet.negotiation.NegotiationErrorParam;
import it.polimi.elet.selflet.negotiation.RemoteServiceCompleted;
import it.polimi.elet.selflet.negotiation.ServiceExecutionParameter;
import it.polimi.elet.selflet.negotiation.ServiceOfferModeEnum;
import it.polimi.elet.selflet.negotiation.ServiceProvider;
import it.polimi.elet.selflet.negotiation.nodeState.INodeState;

import static it.polimi.elet.selflet.message.SelfLetMessageTypeEnum.*;

/**
 * This class contains methods generating instances of <code>SelfLetMsg</code>
 * class with the correct input parameters for each type of msg
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class SelfLetMsgFactory implements ISelfLetMsgFactory {

	private final ISelfLetID thisSelfLetID;

	public SelfLetMsgFactory(ISelfLetID selfLetId) {
		this.thisSelfLetID = selfLetId;
	}

	public SelfLetMsg newNegotiationErrorMsg(ISelfLetID receiverId, String message, Throwable cause) {
		return new SelfLetMsg(thisSelfLetID, receiverId, NEGOTIATION_ERROR, new NegotiationErrorParam(message, cause));
	}

	public SelfLetMsg newNodeStateMsg(ISelfLetID receiverId, INodeState nodeState) {
		return new SelfLetMsg(thisSelfLetID, receiverId, NODE_STATE, nodeState);
	}

	public SelfLetMsg newAdvertiseAchievableServiceMsg(String achievableService, ServiceOfferModeEnum mode) {
		ISelfLetID broadCastSelfLetId = new BroadcastSelfLetID();
		AchievableServiceParam achievableServiceParam = new AchievableServiceParam(achievableService, mode, thisSelfLetID);
		return new SelfLetMsg(thisSelfLetID, broadCastSelfLetId, ADVERTISE_ACHIEVABLE_SERVICE, achievableServiceParam);
	}

	public SelfLetMsg newGetServiceMsg(ISelfLetID nodeId, String serviceName) {
		return new SelfLetMsg(thisSelfLetID, nodeId, DOWNLOAD_ACHIEVABLE_SERVICE, serviceName);
	}

	public SelfLetMsg newGetServiceMsgReply(ISelfLetID nodeId, ServicePack servicePack) {
		return new SelfLetMsg(thisSelfLetID, nodeId, DOWNLOAD_ACHIEVABLE_SERVICE_REPLY, servicePack);
	}

	public SelfLetMsg newAckServiceReceivedMsg(ISelfLetID nodeId, String serviceName) {
		return new SelfLetMsg(thisSelfLetID, nodeId, ACKNOWLEDGE_RECEIVED_SERVICE, serviceName);
	}

	public SelfLetMsg newExecuteAchievableServiceMsg(ISelfLetID receiverId, ServiceExecutionParameter serviceParam) {
		return new SelfLetMsg(thisSelfLetID, receiverId, EXECUTE_ACHIEVABLE_SERVICE, serviceParam);
	}

	public SelfLetMsg newAskNeededServiceReplyMsg(ISelfLetID receiverId, ServiceOfferModeEnum offerMode) {
		return new SelfLetMsg(thisSelfLetID, receiverId, ASK_NEEDED_SERVICE_REPLY, offerMode);
	}

	public SelfLetMsg newGetServiceProviderIndirectlyReplyMsg(ISelfLetID receiverId, ServiceProvider serviceProvider) {
		return new SelfLetMsg(thisSelfLetID, receiverId, GET_SERVICE_PROVIDER_INDIRECTLY_REPLY, serviceProvider);
	}

	public SelfLetMsg newExecuteAchievableServiceReplyMsg(ISelfLetID receiverId, RemoteServiceCompleted remoteServiceCompleted) {
		return new SelfLetMsg(thisSelfLetID, receiverId, EXECUTE_ACHIEVABLE_SERVICE_REPLY, remoteServiceCompleted);
	}

	public SelfLetMsg newAskNeededServiceMsg(NeededServiceParam neededService) {
		ISelfLetID broadCastSelfLetId = new BroadcastSelfLetID();
		return new SelfLetMsg(thisSelfLetID, broadCastSelfLetId, ASK_NEEDED_SERVICE, neededService);
	}

	public SelfLetMsg newGetIndirectlyServiceProviderMsg(ISelfLetID providerId, NeededServiceParam neededServiceParam) {
		return new SelfLetMsg(thisSelfLetID, providerId, GET_SERVICE_PROVIDER_INDIRECTLY, neededServiceParam);
	}

	public SelfLetMsg newAliveMessage() {
		ISelfLetID dispatcherID = new DispatcherID();
		return new SelfLetMsg(thisSelfLetID, dispatcherID, ALIVE_SELFLET, "");
	}

	@Override
	public SelfLetMsg newRedirectRequestReplyMsg(ISelfLetID receiverID, Serializable msgContent) {
		return new SelfLetMsg(thisSelfLetID, receiverID, REDIRECT_REQUEST_REPLY, msgContent);
	}

	@Override
	public SelfLetMsg newRedirectRequestMsg(ServiceExecutionParameter serviceParam, ISelfLetID receiverProvider) {
		return new SelfLetMsg(thisSelfLetID, receiverProvider, REDIRECT_REQUEST, serviceParam);
	}

	@Override
	public SelfLetMsg newServiceTeachMsg(ServicePack servicePack, ISelfLetID provider) {
		return new SelfLetMsg(thisSelfLetID, provider, SERVICE_TEACH, servicePack);
	}

	@Override
	public SelfLetMsg newIstantiateNewSelfletMsg() {
		return new SelfLetMsg(thisSelfLetID, new DispatcherID(), ISTANTIATE_NEW_SELFLET, "");
	}

	@Override
	public SelfLetMsg newRemovalSelfletMessage(ISelfLetID receiver) {
		return new SelfLetMsg(thisSelfLetID, receiver, REMOVE_SELFLET, "");
	}

}
