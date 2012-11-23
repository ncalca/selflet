package it.polimi.elet.selflet;

import it.polimi.elet.selflet.ability.AbstractAbility;
import it.polimi.elet.selflet.schema.SchemaLoader;
import it.polimi.elet.selflet.schema.XSDParserEnum;
import it.polimi.elet.selflet.utilities.parser.ObjectFactory;
import it.polimi.elet.selflet.utilities.parser.SelfletType;
import it.polimi.elet.selflet.utilities.parser.ServiceType;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;

import org.apache.log4j.Logger;

/**
 * This class parses a selflet project and returns all the necessary information
 * to initialize the SelfLet
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class SelfLetParser {

	private static final Logger LOG = Logger.getLogger(SelfLetParser.class);

	private static final String PARSER_PACKAGE = "it.polimi.elet.selflet.utilities.parser";

	private final String selfletWorkingDir;

	public SelfLetParser(String selfletWorkingDir) {
		this.selfletWorkingDir = selfletWorkingDir;
	}

	@SuppressWarnings("rawtypes")
	public SelfletType parseSelfLet() {
		String selfletConfigurationFilePath = selfletWorkingDir + "/" + SelfLetConstants.SELFLET_MAIN_FILE;
		return (SelfletType) ((JAXBElement) unmarshallFile(selfletConfigurationFilePath, XSDParserEnum.SelfLet)).getValue();

	}

	@SuppressWarnings("rawtypes")
	private ServiceType parseService(File file) {
		return (ServiceType) ((JAXBElement) unmarshallFile(file.getAbsolutePath(), XSDParserEnum.Service)).getValue();
	}

	public List<ServiceType> parseServices() {
		LOG.debug("Parsing services");
		List<ServiceType> services = new ArrayList<ServiceType>();
		List<File> files = getFilesOfSelfletFolderWithExtension(SelfLetConstants.SERVICE_FOLDER, SelfLetConstants.SERVICES_EXTENSION);

		for (File file : files) {
			LOG.debug("Parsing " + file);
			ServiceType service = parseService(file);
			services.add(service);
		}

		return services;
	}

	private Object unmarshallFile(String filePath, XSDParserEnum schemaType) {

		File configurationFile = new File(filePath);
		LOG.debug("Unmarshalling file " + configurationFile);

		try {

			ClassLoader classLoader = ObjectFactory.class.getClassLoader();
			JAXBContext jaxbContext = JAXBContext.newInstance(PARSER_PACKAGE, classLoader);
			Schema schema = SchemaLoader.getSchema(schemaType);

			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			unmarshaller.setSchema(schema);

			return unmarshaller.unmarshal(configurationFile);

		} catch (JAXBException e) {
			LOG.error("Error while loading JAXB subsystem", e);
			System.exit(0);
		}

		throw new IllegalStateException();
	}

	public List<String> parseRules() {
		LOG.debug("Parsing rules");
		List<String> rules = new ArrayList<String>();
		List<File> files = getFilesOfSelfletFolderWithExtension(SelfLetConstants.RULES_FOLDER, SelfLetConstants.RULES_EXTENSION);

		for (File file : files) {
			LOG.debug("Found rule " + file);
			rules.add(file.getAbsolutePath());
		}

		return rules;
	}

	public List<AbstractAbility> parseAbilities() {
		LOG.debug("Parsing abilities");
		List<AbstractAbility> abilities = new ArrayList<AbstractAbility>();
		List<File> files = getFilesOfSelfletFolderWithExtension(SelfLetConstants.ABILITIES_FOLDER, SelfLetConstants.ABILITIES_EXTENSION);

		for (File file : files) {
			LOG.debug("Found rule " + file);
			// TODO parse ability
		}
		return abilities;
	}

	public List<File> parseBehaviorOfService(String serviceName) {

		List<File> files = getFilesOfSelfletFolderWithExtension(SelfLetConstants.BEHAVIOR_FOLDER, SelfLetConstants.BEHAVIORS_EXTENSION);
		List<File> behaviorOfService = new ArrayList<File>();

		for (File file : files) {
			String fileName = file.getName();
			String[] parts = fileName.split("\\.");
			if (parts.length > 1 && parts[0].equals(serviceName)) {
				LOG.debug("Found behavior " + file.getAbsolutePath() + " for service " + serviceName);
				behaviorOfService.add(file);
			}
		}

		return behaviorOfService;
	}

	private List<File> getFilesOfSelfletFolderWithExtension(String subFolder, final String extension) {
		File folder = new File(selfletWorkingDir + "/" + subFolder);
		File[] files = folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String fileName) {
				return fileName.endsWith("." + extension);
			}
		});

		if (files == null) {
			return new ArrayList<File>();
		}

		return Arrays.asList(files);
	}

}
