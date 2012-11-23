package it.polimi.elet.selflet.logger;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * An extension of ConsoleAppender that add the selfletId property to the
 * LoggingEvent. In this way it can be retrieved from the layout pattern using
 * the %property{...} syntax.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class SelfletAppender extends ConsoleAppender {

	private static final String SELFLET_ID_PROPERTY = "selfletId";

	private final String selfletID;

	public SelfletAppender(String selfLetID) {
		this.selfletID = selfLetID;
	}

	@Override
	public void append(LoggingEvent event) {
		event.setProperty(SELFLET_ID_PROPERTY, "[S_" + selfletID + "] ");
		super.append(event);
	}

}
