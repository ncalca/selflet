package it.polimi.elet.selflet.message;

import polimi.reds.Filter;
import polimi.reds.Message;

import static it.polimi.elet.selflet.message.SelfLetMessageTypeEnum.*;

public class RedsServiceRequestsFilter implements Filter {
	private static final long serialVersionUID = 8342045511834404655L;

	public boolean matches(Message msg) {

		if (!(msg instanceof RedsMessage)) {
			return false;
		}

		RedsMessage redsMessage = (RedsMessage) msg;
		SelfLetMsg selfLetMessage = redsMessage.getMessage();
		SelfLetMessageTypeEnum msgType = selfLetMessage.getType();

		return msgType.equals(EXECUTE_ACHIEVABLE_SERVICE) || msgType.equals(EXECUTE_ACHIEVABLE_SERVICE_REPLY);
	}
}
