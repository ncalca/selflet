package it.polimi.elet.selflet;

/**
 * Parses the input string and set the corresponding object attributes
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class InputParametersParser {

	private final String[] args;
	private boolean valid = true;

	private String workingDir;

	private boolean isBSet = false;
	private String brokerIpAddress;

	private String brokerPort;

	private boolean isISet = false;
	private String selfletId;

	public InputParametersParser(String[] args) {
		this.args = args.clone();
		parseInputParameters();
	}

	private void parseInputParameters() {
		if (args == null || args.length < 1) {
			invalidArguments();
			return;
		}
		workingDir = args[0];

		for (int i = 1; i < args.length; i = i + 2) {

			if (args.length < i + 1 || args[i].length() != 2) {
				invalidArguments();
				return;
			}

			setParameter(args[i], args[i + 1]);
		}
	}

	private void setParameter(String option, String value) {
		switch (option.charAt(1)) {

		case 'b':
			setBrokerAddress(value);
			break;

		case 'i':
			setSelfletID(value);
			break;

		default:
			invalidArguments();
			break;
		}
	}

	private void setSelfletID(String value) {
		isISet = true;
		selfletId = value;
	}

	private void setBrokerAddress(String value) {
		String[] splittedAddress = value.split(":");

		if (splittedAddress.length != 2) {
			invalidArguments();
			return;
		}
		isBSet = true;
		brokerIpAddress = splittedAddress[0];
		brokerPort = splittedAddress[1];
	}

	public boolean valid() {
		return valid;
	}

	public String getWorkingDir() {
		return workingDir;
	}

	public String getBrokerIpAddress() {
		if (!isBSet) {
			error();
		}
		return brokerIpAddress;
	}

	public String getBrokerPort() {
		if (!isBSet) {
			error();
		}
		return brokerPort;
	}

	public String getSelfletId() {
		if (!isISet) {
			error();
		}

		return selfletId;
	}

	private void invalidArguments() {
		valid = false;
	}

	private void error() {
		throw new IllegalStateException("Trying to get non set parameter");
	}
	
	public boolean isBrokerSet() {
		return isBSet;
	}

	public boolean isIDSet() {
		return isISet;
	}


}
