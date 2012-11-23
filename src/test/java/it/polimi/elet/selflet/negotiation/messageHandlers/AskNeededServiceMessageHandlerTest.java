package it.polimi.elet.selflet.negotiation.messageHandlers;

import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.id.SelfLetID;
import it.polimi.elet.selflet.injectorModules.GuiceModuleFactory;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.message.IMessageHandler;
import it.polimi.elet.selflet.message.ISelfLetMsgFactory;
import it.polimi.elet.selflet.message.SelfLetMessageTypeEnum;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.negotiation.NeededServiceParam;
import it.polimi.elet.selflet.negotiation.ServiceAskModeEnum;
import it.polimi.elet.selflet.negotiation.ServiceOfferModeEnum;
import it.polimi.elet.selflet.test.modules.SelfletMessageHandlerModule;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Injector;
import static org.mockito.Mockito.*;

public class AskNeededServiceMessageHandlerTest {

	private static IServiceKnowledge serviceKnowledge = null;
	private static ISelfLetMsgFactory selfletMsgFactory = null;
	private static IMessageHandler messageHandler = null;

	private static ISelfLetID selfletId = new SelfLetID(1);
	private ISelfletMessageHandler handler = null;

	@BeforeClass
	public static void setUpClass() {
		Injector injector = GuiceModuleFactory.buildModule(new SelfletMessageHandlerModule(selfletId));
		serviceKnowledge = injector.getInstance(IServiceKnowledge.class);

		messageHandler = mock(IMessageHandler.class);
		// selfletMsgFactory = injector.getInstance(ISelfLetMsgFactory.class);
		selfletMsgFactory = mock(ISelfLetMsgFactory.class);
	}

	@Before
	public void setUpTest() {
		handler = new AskNeededServiceMessageHandler(serviceKnowledge, selfletMsgFactory, messageHandler);
	}

	@Test
	public void testAnswerAfterMessage() {
		ISelfLetID receiverSelflet = new SelfLetID(2);
		NeededServiceParam neededServiceParam = new NeededServiceParam("service1", ServiceAskModeEnum.Any);
		SelfLetMsg selfletMsg = new SelfLetMsg(selfletId, receiverSelflet, SelfLetMessageTypeEnum.ASK_NEEDED_SERVICE, neededServiceParam);
		handler.handleMessage(selfletMsg);
		SelfLetMsg reply = selfletMsgFactory.newAskNeededServiceReplyMsg(receiverSelflet, ServiceOfferModeEnum.None);
		verify(messageHandler).reply(reply, selfletMsg.getId());
	}

}
