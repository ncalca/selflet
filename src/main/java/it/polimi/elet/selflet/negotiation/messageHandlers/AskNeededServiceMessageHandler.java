package it.polimi.elet.selflet.negotiation.messageHandlers;

import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.message.IMessageHandler;
import it.polimi.elet.selflet.message.ISelfLetMsgFactory;
import it.polimi.elet.selflet.message.SelfLetMessageTypeEnum;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.negotiation.NeededServiceParam;
import it.polimi.elet.selflet.negotiation.ServiceAskModeEnum;
import it.polimi.elet.selflet.negotiation.ServiceOfferModeEnum;
import it.polimi.elet.selflet.service.Service;

import org.apache.log4j.Logger;

/**
 * Implementation for ASK_NEEDED_SERVICE selflet message handler. This message
 * is sent from a selflet to another to ask the offer mode of a given service
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class AskNeededServiceMessageHandler implements ISelfletMessageHandler {

	private static final Logger LOG = Logger.getLogger(AskNeededServiceMessageHandler.class);

	private final ISelfLetMsgFactory selfletMsgFactory;
	private final IMessageHandler messageHandler;
	private final IServiceKnowledge serviceKnowledge;

	public AskNeededServiceMessageHandler(IServiceKnowledge serviceKnowledge, ISelfLetMsgFactory selfletMsgFactory, IMessageHandler messageHandler) {
		this.serviceKnowledge = serviceKnowledge;
		this.selfletMsgFactory = selfletMsgFactory;
		this.messageHandler = messageHandler;
	}

	@Override
	public void handleMessage(SelfLetMsg message) {

		NeededServiceParam neededServiceParam = (NeededServiceParam) message.getContent();

		LOG.debug("Received event " + message.getType() + " " + neededServiceParam);

		String neededServiceName = neededServiceParam.getNeededServiceName();
		ServiceAskModeEnum askMode = neededServiceParam.getMode();

		SelfLetMsg reply = null;

		if (serviceKnowledge.isPropertyPresent(neededServiceName)) {
			reply = generateServiceReply(neededServiceName, askMode, message.getFrom());
		} else {
			reply = selfletMsgFactory.newAskNeededServiceReplyMsg(message.getFrom(), ServiceOfferModeEnum.None);
		}

		messageHandler.reply(reply, message.getId());
	}

	private SelfLetMsg generateServiceReply(String neededService, ServiceAskModeEnum askMode, ISelfLetID from) {
		Service service = serviceKnowledge.getProperty(neededService);
		ServiceOfferModeEnum replyMode = selectReplyMode(service, askMode);
		return selfletMsgFactory.newAskNeededServiceReplyMsg(from, replyMode);
	}

	private ServiceOfferModeEnum selectReplyMode(Service service, ServiceAskModeEnum askMode) {

		switch (askMode) {

		// Here the priorities are chosen...
		// priority is given to the "Do" mode (direct or undirect)s
		case Any:

			if (service.hasLocalOfferMode(ServiceOfferModeEnum.CanDo)) {
				return ServiceOfferModeEnum.CanDo;
			} else if (service.hasLocalOfferMode(ServiceOfferModeEnum.KnowsWhoCanDo)) {
				return ServiceOfferModeEnum.KnowsWhoCanDo;
			} else if (service.hasLocalOfferMode(ServiceOfferModeEnum.CanTeach)) {
				return ServiceOfferModeEnum.CanTeach;
			} else if (service.hasLocalOfferMode(ServiceOfferModeEnum.KnowsWhoCanTeach)) {
				return ServiceOfferModeEnum.KnowsWhoCanTeach;
			}
			break;

		case Do:

			if (service.hasLocalOfferMode(ServiceOfferModeEnum.CanDo)) {
				return ServiceOfferModeEnum.CanDo;
			} else if (service.hasLocalOfferMode(ServiceOfferModeEnum.KnowsWhoCanDo)) {
				return ServiceOfferModeEnum.KnowsWhoCanDo;
			}
			break;

		case Teach:

			if (service.hasLocalOfferMode(ServiceOfferModeEnum.CanTeach)) {
				return ServiceOfferModeEnum.CanTeach;
			} else if (service.hasLocalOfferMode(ServiceOfferModeEnum.KnowsWhoCanTeach)) {
				return ServiceOfferModeEnum.KnowsWhoCanTeach;
			}

			break;

		}

		throw new IllegalArgumentException("Ask not recognized: " + askMode);
	}

	@Override
	public SelfLetMessageTypeEnum getTypeOfHandledMessage() {
		return SelfLetMessageTypeEnum.ASK_NEEDED_SERVICE;
	}

}
