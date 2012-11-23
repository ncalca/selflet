package it.polimi.elet.selflet.logger;

import org.apache.log4j.Appender;
import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

/**
 * This class initializes and manages the logger for the SelfLet using the Log4J
 * libraries
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class SelfLetLogger {

	private String selfLetID;
	private static final String LOG_FOLDER = "./logs";

	public SelfLetLogger(String selfLetID) {
		this.selfLetID = selfLetID;
	}

	/**
	 * Initializes the logger
	 * */
	public void init() {
		Logger rootLogger = Logger.getRootLogger();
		if (rootLogger == null) {
			System.err.println("Cannot find root logger");
			return;
		}

		setConsoleAppender(rootLogger);
		setFileAppender(rootLogger);
		setSelfletAppender(rootLogger);
	}

	private void setSelfletAppender(Logger rootLogger) {
		SelfletAppender selfletAppender = new SelfletAppender(selfLetID);
		selfletAppender.setName("SelfLetAppender");
		String pattern = "%-4r %properties{selfletId} %-5p [%c{1}]:%L - %m%n";
		selfletAppender.setLayout(new EnhancedPatternLayout(pattern));
		selfletAppender.activateOptions();
		rootLogger.addAppender(selfletAppender);
	}

	private void setConsoleAppender(Logger rootLogger) {
		Appender consoleAppender = rootLogger.getAppender("myConsoleAppender");

		if (consoleAppender == null) {
			System.err.println("Cannot find console logger");
			return;
		}

	}

	private void setFileAppender(Logger rootLogger) {
		FileAppender fileAppender = (FileAppender) rootLogger.getAppender("myFileAppender");

		String logFileName = LOG_FOLDER + "/selflet" + selfLetID + "_" + System.currentTimeMillis() + ".log";

		fileAppender.setFile(logFileName);
		fileAppender.activateOptions();
	}

}
