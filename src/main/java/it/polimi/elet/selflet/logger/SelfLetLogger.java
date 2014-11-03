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
	private static final String LOG_FOLDER = "./../selflets-log";

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
		
		Logger resultsLogger = Logger.getLogger("resultsLogger");
		if (resultsLogger == null) {
			System.err.println("Cannot find results logger");
		}
		
		Logger actionsLogger = Logger.getLogger("actionsLogger");
		if (actionsLogger == null) {
			System.err.println("Cannot find actions logger");
		}
		
		setConsoleAppender(rootLogger);
		setFileAppender(rootLogger);
		setResultsAppender(resultsLogger);
		setActionsAppender(actionsLogger);
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
	
	private void setResultsAppender(Logger resultsLogger) {
		FileAppender fileAppender = (FileAppender) resultsLogger.getAppender("resultsFileAppender");
		
		String logFileName = LOG_FOLDER + "/results_selflet" + selfLetID + "_" + System.currentTimeMillis() + ".log";

		fileAppender.setFile(logFileName);
		fileAppender.activateOptions();
	}
	
	private void setActionsAppender(Logger resultsLogger) {
		FileAppender fileAppender = (FileAppender) resultsLogger.getAppender("actionsFileAppender");
		
		String logFileName = LOG_FOLDER + "/actions_selflet" + selfLetID + "_" + System.currentTimeMillis() + ".log";

		fileAppender.setFile(logFileName);
		fileAppender.activateOptions();
	}

}
