package it.polimi.elet.selflet;

import it.polimi.elet.selflet.ability.AbstractAbility;
import it.polimi.elet.selflet.configuration.ConfigurationException;
import it.polimi.elet.selflet.configuration.SelfletConfiguration;
import it.polimi.elet.selflet.knowledge.KnowledgeProperty;
import it.polimi.elet.selflet.knowledge.KnowledgePropertyFactory;
import it.polimi.elet.selflet.logger.SelfLetLogger;
import it.polimi.elet.selflet.utilities.parser.GeneralKnowledgeType;
import it.polimi.elet.selflet.utilities.parser.SelfLetProperty;
import it.polimi.elet.selflet.utilities.parser.SelfletProperties;
import it.polimi.elet.selflet.utilities.parser.SelfletType;
import it.polimi.elet.selflet.utilities.parser.ServiceType;
import it.polimi.elet.selflet.utilities.parser.TypeKnowledgeType;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * This class reads the configuration information from the XML file and launches
 * the actual SelfLet.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */

public class SelfLetInitializer {

	private static final Logger LOG = Logger.getLogger(SelfLetInitializer.class);

	private final InputParametersParser inputParameters;

	private SelfletInstance selfLetInstance;

	private String selfLetID;
	private String REDSAddress;
	private int REDSPort;
	private int LIMEPort;
	private String initialService;
	private List<String> ruleFiles;
	private List<AbstractAbility> abilities;
	private String workingDirectory;

	private SelfletProperties properties;
	private SelfLetParser selfLetParser;
	private SelfletType selfletElement;

	public SelfLetInitializer(InputParametersParser inputParameters) {
		this.inputParameters = inputParameters;
		this.workingDirectory = inputParameters.getWorkingDir();
		if (inputParameters.isIDSet()) {
			this.selfLetID = inputParameters.getSelfletId();
		}
		initConfigurationParameters();
		startParser(workingDirectory);
		configure();
	}

	private void startParser(String selfletConfigurationFilePath) {
		this.selfLetParser = new SelfLetParser(selfletConfigurationFilePath);
		this.selfletElement = selfLetParser.parseSelfLet();
	}

	private void configure() {

		SelfLetLogger selfLetLogger = new SelfLetLogger(selfLetID);
		selfLetLogger.init();

		properties = selfletElement.getSelfletProperties();

		List<ServiceType> services = selfLetParser.parseServices();
		ruleFiles = selfLetParser.parseRules();

		setKnowledgeBaseParameters(properties.getGeneralknowledge());
		extractTypeKnowledgeProperties(properties.getTypeKnowledge());

		selfLetInstance = new SelfletInstance(selfLetID, selfLetID, services, workingDirectory, ruleFiles);

		abilities = selfLetParser.parseAbilities();

		selfLetInstance.setBaseProperty("selfletType", selfletElement);

		if (inputParameters.isBrokerSet()) {
			this.REDSAddress = inputParameters.getBrokerIpAddress();
			this.REDSPort = Integer.valueOf(inputParameters.getBrokerPort());
		} else {
			this.REDSAddress = properties.getReds().getIpAddress();
			this.REDSPort = properties.getReds().getPort();
		}

		if (properties.getActive() != null) {
			this.initialService = properties.getActive().getMainService();
		}
	}

	public void startSelfLet() {
		selfLetInstance.startup(REDSAddress, REDSPort, LIMEPort, abilities, initialService);
	}

	/** Set the knowledge type parameters */
	private List<KnowledgeProperty> extractTypeKnowledgeProperties(TypeKnowledgeType typeKnowledgeType) {

		List<KnowledgeProperty> properties = new ArrayList<KnowledgeProperty>();
		if (typeKnowledgeType == null) {
			return properties;
		}

		//
		// List<SelfLetProperty> typeProperties =
		// typeKnowledgeType.getSelfLetProperty();
		//
		// for (SelfLetProperty selfLetProperty : typeProperties) {
		//
		// String name = selfLetProperty.getName();
		// String value = selfLetProperty.getValue();
		//
		// LOG.debug("Setting type property " + name + " to value " + value);
		//
		// typeKnowledge.setOrUpdateProperty(name, property)
		//
		// selfLetInstance.setTypeProperty(name,
		// KnowledgePropertyFactory.create(selfLetProperty));
		// }
		return properties;
	}

	/** Set the knowledge base parameters */
	private void setKnowledgeBaseParameters(GeneralKnowledgeType generalKnowledgeType) {

		if (generalKnowledgeType == null) {
			return;
		}

		List<SelfLetProperty> selfLetProperties = generalKnowledgeType.getSelfLetProperty();

		for (SelfLetProperty property : selfLetProperties) {

			String name = property.getName();
			String value = property.getValue();

			LOG.debug("Setting general knowledge property " + name + " to value " + value);

			selfLetInstance.setBaseProperty(name, KnowledgePropertyFactory.create(property));
		}
	}

	private void initConfigurationParameters() {
		String configurationFilePath = workingDirectory + "/" + SelfLetConstants.CONFIGURATION_FILE;
		try {
			SelfletConfiguration.getSingleton().loadFromFile(configurationFilePath);
		} catch (ConfigurationException e) {
			LOG.error("Cannot load configuration file " + configurationFilePath, e);

			String oneLevelUp = removeProjectPath(configurationFilePath);
			try {
				SelfletConfiguration.getSingleton().loadFromFile(oneLevelUp);
			} catch (ConfigurationException e1) {
				LOG.error("Cannot load configuration file " + oneLevelUp, e1);
				System.out.println("Cannot load " + oneLevelUp);
				e1.printStackTrace();
				System.exit(0);
			}

		}
	}

	private String removeProjectPath(String configurationFilePath) {
		String[] split = configurationFilePath.split("/");
		String newPath = "";
		for (int i = 0; i < split.length - 2; i++) {
			newPath += split[i] + "/";
		}
		newPath += split[split.length - 1];
		return newPath;
	}

}
