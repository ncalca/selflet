package it.polimi.elet.selflet.message;

import it.polimi.elet.selflet.configuration.SelfletConfiguration;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.threadUtilities.IPeriodicTask;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 * This class periodically sends an ALIVE message (heartbeat) which is read by
 * the request dispatcher
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class SelfLetAliveTimerTask extends TimerTask implements IPeriodicTask {

	private static final Logger LOG = Logger.getLogger(SelfLetAliveTimerTask.class);

	private static final Integer SLEEP_TIME_MSEC = SelfletConfiguration.getSingleton().aliveMessagePeriodInSec * 1000;

	private final ISelfLetMsgFactory selfletMsgFactory;
	private final IMessageHandler messageHandler;

	@Inject
	public SelfLetAliveTimerTask(ISelfLetMsgFactory selfletMsgFactory, IMessageHandler messageHandler) {
		this.selfletMsgFactory = selfletMsgFactory;
		this.messageHandler = messageHandler;
	}

	@Override
	public void run() {
		LOG.debug("Sending alive message");
		SelfLetMsg aliveMessage = selfletMsgFactory.newAliveMessage();
		messageHandler.send(aliveMessage);
	}

	@Override
	public int periodInMsec() {
		return SLEEP_TIME_MSEC;
	}

}
