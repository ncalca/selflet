package it.polimi.elet.selflet;

/**
 * Main class to instantiate the SelfLet
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class SelfLet {

	private static final String USAGE = "Usage:\n\t" + SelfLet.class.getName() + " workingDir [-b ADDRESS:PORT] [-i IDENTIFIER]";

	private SelfLet() {
		// private constructor
	}

	/**
	 * Input arguments:
	 * 
	 * Selflet.main workingdir [-b ADDRESS:PORT] [-i IDENTIFIER]
	 * */
	public static void main(String[] args) {

		InputParametersParser inputParameters = new InputParametersParser(args);

		if (!inputParameters.valid()) {
			System.out.println(USAGE);
			System.exit(-1);
		}

		SelfLetInitializer initializer = new SelfLetInitializer(inputParameters);
		initializer.startSelfLet();
	}

}
