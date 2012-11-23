package it.polimi.elet.selflet.negotiation.nodeState;

import it.polimi.elet.selflet.threadUtilities.IPeriodicTask;

/**
 * Interface for periodic state updater. It periodically activates and send the
 * state to the dispatcher to the neighbors
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface INeighborStateUpdaterTimerTask extends IPeriodicTask {

}
