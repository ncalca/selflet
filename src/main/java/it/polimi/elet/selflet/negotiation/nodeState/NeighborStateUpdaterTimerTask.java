package it.polimi.elet.selflet.negotiation.nodeState;

import it.polimi.elet.selflet.configuration.SelfletConfiguration;
import it.polimi.elet.selflet.id.DispatcherID;
import it.polimi.elet.selflet.id.NeighborsID;
import it.polimi.elet.selflet.message.IMessageHandler;
import it.polimi.elet.selflet.message.ISelfLetMsgFactory;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.negotiation.nodeState.INodeState;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * This class extends a thread class and it is in charge of updating the state
 * of neighbors.
 * 
 * This class is periodically activated and will ask to known neighbors their
 * internal states.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class NeighborStateUpdaterTimerTask extends TimerTask implements INeighborStateUpdaterTimerTask {

	private static final Logger LOG = Logger.getLogger(NeighborStateUpdaterTimerTask.class);

	private static final int SLEEP_TIME_MSEC = SelfletConfiguration.getSingleton().neighborUpdateManagerIntervalInSec * 1000;;

	private final IMessageHandler messageHandler;
	private final ISelfLetMsgFactory selfLetMsgFactory;
	private final INodeStateFactory nodeStateFactory;

	@Inject
	public NeighborStateUpdaterTimerTask(IMessageHandler messageHandler, ISelfLetMsgFactory selfLetMsgFactory, INodeStateFactory nodeStateFactory) {
		this.messageHandler = messageHandler;
		this.selfLetMsgFactory = selfLetMsgFactory;
		this.nodeStateFactory = nodeStateFactory;
	}

	public void run() {
		try {
			LOG.debug("Sending node state");
			INodeState nodeState = nodeStateFactory.createNodeState();

			SelfLetMsg dispatcherSelfletMsg = selfLetMsgFactory.newNodeStateMsg(new DispatcherID(), nodeState);
			messageHandler.send(dispatcherSelfletMsg);
			SelfLetMsg neighborSelfletMsg = selfLetMsgFactory.newNodeStateMsg(new NeighborsID(), nodeState);
			try {
				messageHandler.send(neighborSelfletMsg);
			} catch (RuntimeException ex) {
				LOG.error("Cannot send state message to selflet neighbors", ex.getCause());
				return;
			}
		} catch (Throwable e) {
			LOG.error("Error in neighbor state updater", e);
		}
	}

	@Override
	public int periodInMsec() {
		return SLEEP_TIME_MSEC;
	}
}
