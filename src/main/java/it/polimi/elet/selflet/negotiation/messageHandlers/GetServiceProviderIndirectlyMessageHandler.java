package it.polimi.elet.selflet.negotiation.messageHandlers;

import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.message.IMessageHandler;
import it.polimi.elet.selflet.message.ISelfLetMsgFactory;
import it.polimi.elet.selflet.message.SelfLetMessageTypeEnum;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.negotiation.NeededServiceParam;
import it.polimi.elet.selflet.negotiation.ServiceAskModeEnum;
import it.polimi.elet.selflet.negotiation.ServiceOfferModeEnum;
import it.polimi.elet.selflet.negotiation.ServiceProvider;
import it.polimi.elet.selflet.service.Service;

import org.apache.log4j.Logger;

/**
 * Implementation for GET_SERVICE_PROVIDER_INDIRECTLY selflet message handler
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class GetServiceProviderIndirectlyMessageHandler implements ISelfletMessageHandler {

	private static final Logger LOG = Logger.getLogger(GetServiceProviderIndirectlyMessageHandler.class);

	private final IServiceKnowledge serviceKnowledge;
	private final ISelfLetMsgFactory selfletMsgFactory;
	private final IMessageHandler messageHandler;

	public GetServiceProviderIndirectlyMessageHandler(IServiceKnowledge serviceKnowledge, ISelfLetMsgFactory selfletMsgFactory, IMessageHandler messageHandler) {
		this.serviceKnowledge = serviceKnowledge;
		this.selfletMsgFactory = selfletMsgFactory;
		this.messageHandler = messageHandler;
	}

	@Override
	public void handleMessage(SelfLetMsg message) {
		LOG.debug("Negotiation Manager: received event " + "GET SERVICE PROVIDER INDIRECTLY");

		NeededServiceParam neededServiceParam = (NeededServiceParam) message.getContent();

		String neededService = neededServiceParam.getNeededServiceName();
		ServiceAskModeEnum askMode = neededServiceParam.getMode();

		Service service = null;
		try {
			service = serviceKnowledge.getProperty(neededService);
		} catch (NotFoundException e) {
			LOG.error("Can't find Service", e);
			SelfLetMsg reply = selfletMsgFactory.newGetServiceProviderIndirectlyReplyMsg(message.getFrom(), null);
			messageHandler.reply(reply, message.getId());
			return;
		}

		ServiceProvider provider = service.getPreferredProvider(askMode);

		ServiceOfferModeEnum provOffer = provider.getOfferMode();

		if (provOffer == ServiceOfferModeEnum.KnowsWhoCanBoth || provOffer == ServiceOfferModeEnum.KnowsWhoCanDo
				|| provOffer == ServiceOfferModeEnum.KnowsWhoCanTeach || provOffer == ServiceOfferModeEnum.None) {

			// If we can't find a provider who can, we reply with
			// null
			SelfLetMsg reply = selfletMsgFactory.newGetServiceProviderIndirectlyReplyMsg(message.getFrom(), null);

			messageHandler.reply(reply, message.getId());
			return;
		} else {
			// Else we can reply with the provider
			SelfLetMsg reply = selfletMsgFactory.newGetServiceProviderIndirectlyReplyMsg(message.getFrom(), provider);
			messageHandler.reply(reply, message.getId());
			return;
		}

	}

	@Override
	public SelfLetMessageTypeEnum getTypeOfHandledMessage() {
		return SelfLetMessageTypeEnum.GET_SERVICE_PROVIDER_INDIRECTLY;
	}

}
