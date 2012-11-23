package it.polimi.elet.selflet.ability;

import it.polimi.elet.selflet.autonomic.IAutonomicManager;
import it.polimi.elet.selflet.behavior.CondiditionEvaluatorFactory;
import it.polimi.elet.selflet.behavior.IConditionEvaluator;
import it.polimi.elet.selflet.events.DispatchingUtility;
import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.IEventDispatcher;
import it.polimi.elet.selflet.events.ISelfletEvent;
import it.polimi.elet.selflet.events.SelfletComponent;
import it.polimi.elet.selflet.events.ability.AbilityInstalledEvent;
import it.polimi.elet.selflet.events.ability.AbilityUninstalledEvent;
import it.polimi.elet.selflet.exceptions.AbilityEnvironmentException;
import it.polimi.elet.selflet.exceptions.AlreadyPresentException;
import it.polimi.elet.selflet.exceptions.IncompatibleTypeException;
import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.knowledge.IGeneralKnowledge;
import it.polimi.elet.selflet.knowledge.IKnowledgesContainer;
import it.polimi.elet.selflet.knowledge.ITypeKnowledge;
import it.polimi.elet.selflet.message.IMessageHandler;
import it.polimi.elet.selflet.negotiation.INegotiationManager;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.knopflerfish.framework.Framework;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.util.tracker.ServiceTracker;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * An implementation of the IAbilityExecutionEnvironment interface, making use
 * of a Knopflerfish OSGi framework to handle abilities as OSGi Bundles.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * @author Davide Devescovi
 */
@Singleton
public class AbilityExecutionEnvironmentOsgi extends SelfletComponent implements IAbilityExecutionEnvironment {

	private static final Logger LOG = Logger.getLogger(AbilityExecutionEnvironmentOsgi.class);

	/**
	 * Arguments (classes) of the setup method for abilities
	 */
	private static final Class<?>[] SETUP_ARGS_TYPE = { IAbilityExecutionEnvironment.class, IAutonomicManager.class, IConditionEvaluator.class,
			IEventDispatcher.class, IGeneralKnowledge.class, IMessageHandler.class, INegotiationManager.class };

	private final ISelfLetID selfletID;
	private final IMessageHandler messageHandler;
	private final IKnowledgesContainer knowledges;

	private IAutonomicManager autonomicManager;
	private INegotiationManager negotiationManager;
	private ITypeKnowledge typeKnowledge;

	private String frameworkDir;

	private Framework framework;
	private BundleContext bundleContext;
	private boolean frameworkStarted = false;

	private ConcurrentHashMap<AbilitySignature, AbilityEntry> installedAbilities;
	private List<IEventDispatcher> dispatchers;

	@Inject
	public AbilityExecutionEnvironmentOsgi(ISelfLetID selfletID, IMessageHandler messageHandler, IKnowledgesContainer knowledges,
			IAutonomicManager autonomicManager) {
		this.selfletID = selfletID;
		this.autonomicManager = autonomicManager;
		this.messageHandler = messageHandler;
		this.knowledges = knowledges;
		this.installedAbilities = new ConcurrentHashMap<AbilitySignature, AbilityEntry>(6, 0.75f, 8);
		this.dispatchers = new CopyOnWriteArrayList<IEventDispatcher>();
		init();
	}

	private void init() {

		frameworkDir = "./SelfLetOsgiframeworks/fwdir_" + selfletID;

		// Set the path for the framework directory
		System.setProperty(FRAMEWORK_DIR_PROP, frameworkDir);
		// System.setProperty("java.security.manager", "");

		// Setup the framework
		try {
			framework = new Framework(this);
			bundleContext = framework.getSystemBundleContext();

			LOG.debug("Instantiated framework in " + frameworkDir);
		} catch (Exception e) {
			LOG.error("Error while instantiating OSGi framework. Quitting...", e);
			throw new AbilityEnvironmentException("Error while instantiating OSGi framework. Quitting...", e);
		}
	}

	public void startEnvironment() {

		try {

			framework.launch(0);
			frameworkStarted = true;
			try {
				updateAbilityListFromFile();

			} catch (ClassNotFoundException e) {
				throw new AbilityEnvironmentException("Error while loading the list of installed abilities", e);
			}

			if (installedAbilities.size() == 0) {
				return;
			}

			Iterator<AbilitySignature> it = installedAbilities.keySet().iterator();
			LOG.debug("Bundles which are installed at startup: ");

			while (it.hasNext()) {

				AbilitySignature abilitySignature = it.next();
				AbilityEntry abilityEntry = installedAbilities.get(abilitySignature);

				if (abilityEntry == null) {
					continue;
				}

				// If a bundle is recorded on the file but is not installed,
				// remove it
				long bundleId = abilityEntry.getBundleID();
				if (bundleContext.getBundle(bundleId) == null) {
					it.remove();
					LOG.debug("The .bin file reported bundle " + abilitySignature.getUniqueName() + ", but it was not found in "
							+ "the framework and it was removed");
				} else {

					// We need to call the setup method of the ability
					// Get the installed service
					ServiceTracker tracker = new ServiceTracker(bundleContext, ((AbilityDescriptorOsgi) abilityEntry.getDescriptor()).getServiceName(), null);
					tracker.open();

					Object service = tracker.getService();

					if (service == null) {
						LOG.error("No setup method found for ability " + abilityEntry);
						continue;
					}

					Method method;
					try {
						// Get and invoke the setup method of the ability
						method = service.getClass().getMethod(SETUP_METHOD_NAME, SETUP_ARGS_TYPE);

						// set the correct parameters for the setup method
						method.invoke(service, this, autonomicManager, CondiditionEvaluatorFactory.create(knowledges.getGeneralKnowledge()), dispatcher,
								typeKnowledge, messageHandler, negotiationManager);

						LOG.debug("Setup method for ability " + abilitySignature.getUniqueName() + " was successfully invoked");
					}
					// TODO: when these exceptions happen, we should try to
					// uninstall the bundle
					catch (SecurityException e) {
						throw new AbilityEnvironmentException("Unexpected security error during Reflection lookup " + "for the setup method in ability "
								+ abilitySignature.getUniqueName(), e);
					} catch (NoSuchMethodException e) {
						throw new IncompatibleTypeException("Couldn't find the setup method in ability " + abilitySignature.getUniqueName(), e);
					} catch (IllegalArgumentException e) {
						throw new AbilityEnvironmentException("Unexpected Reflection error during setup method " + "invocation for ability "
								+ abilitySignature.getUniqueName(), e);
					} catch (IllegalAccessException e) {
						throw new AbilityEnvironmentException("Unexpected Reflection error during setup method " + "invocation for ability "
								+ abilitySignature.getUniqueName(), e);
					} catch (InvocationTargetException e) {
						throw new AbilityEnvironmentException("Unexpected Reflection error during setup method " + "invocation for ability "
								+ abilitySignature.getUniqueName(), e);
					}

					// We need to notify that we do have these abilities
					// installed, or the NegotiationManager won't know about
					// them
					fireAbilityInstalledEvent(abilitySignature);

					LOG.debug(abilityEntry.getBundleID() + ": " + abilitySignature.getUniqueName());
				}
			}

			LOG.debug("Successfully started framework");

		} catch (BundleException e) {
			throw new AbilityEnvironmentException("Unexpected bundle error during framework startup", e);
		}
	}

	public void addAbility(AbstractAbility ability) {

		// If the ability isn't of the same type as this environment, throw an
		// Exception
		if (!ability.getDescriptor().getAbilityType().equals(AbilityOsgi.OSGI_TYPE)) {
			throw new IncompatibleTypeException("The ability type " + ability.getDescriptor().getAbilityType()
					+ " is not compatible with current environment type " + AbilityOsgi.OSGI_TYPE);
		}

		// If the framework hasn't started yet, do it now
		if (!frameworkStarted) {
			startEnvironment();
		}

		// If the ability is already installed throw an exception
		if (isPresent(ability.getSignature())) {
			throw new AlreadyPresentException("The ability " + ability.getSignature().getUniqueName() + "is already installed");
		}

		ByteArrayInputStream bundle = new ByteArrayInputStream((byte[]) ability.getAbility());

		long bundleId;

		try {
			// TODO: Actually the first parameter of installBundle is the
			// "location"... but which location would that be, as I'm passing an
			// array of byte? At the moment I just use the name of the ability
			// and all seems fine.
			bundleId = framework.installBundle(ability.getSignature().getUniqueName(), bundle);
			framework.startBundle(bundleId);
			LOG.debug("Started bundle " + bundleId + ": " + ability.getSignature().getUniqueName());
		} catch (BundleException e) {
			throw new AbilityEnvironmentException("Unexpected bundle error during bundle " + ability.getSignature().getUniqueName() + " installation", e);
		}
		// We could check the variable id, but if we get here everything worked
		// fine

		// Get the installed service

		String serviceName = ((AbilityDescriptorOsgi) ability.getDescriptor()).getServiceName();

		ServiceTracker tracker = new ServiceTracker(bundleContext, serviceName, null);

		tracker.open();

		Object service = tracker.getService();

		if (service == null) {
			throw new AbilityEnvironmentException("Service tracker returned a null service while looking for service "
					+ ((AbilityDescriptorOsgi) ability.getDescriptor()).getServiceName() + " for ability " + ability + " . I'm going to skip it");
		}

		Method method;
		try {
			// Get and invoke the setup method of the ability
			Class<?> clazz = service.getClass();

			method = clazz.getMethod(SETUP_METHOD_NAME, SETUP_ARGS_TYPE);
			method.invoke(service, this, autonomicManager, CondiditionEvaluatorFactory.create(knowledges.getGeneralKnowledge()), dispatcher, typeKnowledge,
					messageHandler, negotiationManager);

			LOG.debug("Setup method for ability " + ability.getSignature().getUniqueName() + " was successfully invoked");
		}
		// TODO: when these exceptions happen, we should try to uninstall
		// the bundle
		catch (SecurityException e) {
			throw new AbilityEnvironmentException("Unexpected security error during Reflection lookup " + "for the setup method in ability "
					+ ability.getSignature().getUniqueName(), e);
		} catch (NoSuchMethodException e) {
			throw new IncompatibleTypeException("Couldn't find the setup method in ability " + ability.getSignature().getUniqueName(), e);
		} catch (IllegalArgumentException e) {
			throw new AbilityEnvironmentException("Unexpected Reflection error during setup method " + "invocation for ability "
					+ ability.getSignature().getUniqueName(), e);
		} catch (IllegalAccessException e) {
			throw new AbilityEnvironmentException("Unexpected Reflection error during setup method " + "invocation for ability "
					+ ability.getSignature().getUniqueName(), e);
		} catch (InvocationTargetException e) {
			throw new AbilityEnvironmentException("Unexpected Reflection error during setup method " + "invocation for ability "
					+ ability.getSignature().getUniqueName(), e);
		}

		// The "already present" check was performed earlier with isPresent,
		// so here we're sure we're not overwriting anything
		// TODO: not true because of concurrency...
		installedAbilities.put(ability.getSignature(), new AbilityEntry((AbilityDescriptorOsgi) ability.getDescriptor(), bundleId));
		try {
			commitAbilityList();
		} catch (FileNotFoundException e) {
			throw new AbilityEnvironmentException("Error while updating the ability list on file", e);
		} catch (IOException e) {
			throw new AbilityEnvironmentException("Error while updating the ability list on file", e);
		}

		LOG.debug("Installed ability " + ability.getSignature().getUniqueName());

		fireAbilityInstalledEvent(ability.getSignature());
	}

	public void removeAbility(AbilitySignature abilitySignature) {

		AbilityEntry entry = installedAbilities.get(abilitySignature);

		// No abilities found, so we don't care and return
		if (entry == null) {
			LOG.debug("removeAbility was called on " + abilitySignature.getUniqueName() + ", but the ability was not found: ignored");
			return;
		}

		// Get the service registered by the ability
		ServiceTracker tracker = new ServiceTracker(bundleContext, entry.getDescriptor().getServiceName(), null);
		tracker.open();
		Object service = tracker.getService();

		Method method;
		try {
			// Get and invoke the teardown method of the ability
			method = service.getClass().getMethod(TEARDOWN_METHOD_NAME);
			method.invoke(service);

			LOG.debug("Teardown method for ability " + abilitySignature.getUniqueName() + " was successfully invoked");
		} catch (SecurityException e) {
			throw new AbilityEnvironmentException("Unexpected security error during Reflection lookup " + "for the teardown method in ability "
					+ abilitySignature.getUniqueName(), e);
		} catch (NoSuchMethodException e) {
			throw new IncompatibleTypeException("Couldn't find the teardown method in ability " + abilitySignature.getUniqueName(), e);
		} catch (IllegalArgumentException e) {
			throw new AbilityEnvironmentException("Unexpected Reflection error during teardown method " + "invocation for ability "
					+ abilitySignature.getUniqueName(), e);
		} catch (IllegalAccessException e) {
			throw new AbilityEnvironmentException("Unexpected Reflection error during teardown method " + "invocation for ability "
					+ abilitySignature.getUniqueName(), e);
		} catch (InvocationTargetException e) {
			throw new AbilityEnvironmentException("Unexpected Reflection error during teardown method " + "invocation for ability "
					+ abilitySignature.getUniqueName(), e);
		}

		try {
			// The actual uninstall
			framework.uninstallBundle(entry.getBundleID());

			LOG.debug("Stopped and uninstalled bundle " + entry.getBundleID() + ": " + abilitySignature.getUniqueName());
		} catch (BundleException e) {
			throw new AbilityEnvironmentException("Unexpected bundle error during framework shutdown", e);
		} finally {
			// In any case, remove the ability
			installedAbilities.remove(abilitySignature);
			try {
				commitAbilityList();
			} catch (FileNotFoundException e) {
				throw new AbilityEnvironmentException("Error while updating the ability list on file", e);
			} catch (IOException e) {
				throw new AbilityEnvironmentException("Error while updating the ability list on file", e);
			}
		}

		LOG.debug("Removed ability " + abilitySignature.getUniqueName());

		fireAbilityUninstalledEvent(abilitySignature);
	}

	public Serializable execute(AbilitySignature abilitySignature, String methodName, Object... params) {

		AbilityEntry entry = installedAbilities.get(abilitySignature);

		// The ability is not installed, so throw an exception
		if (entry == null) {
			throw new NotFoundException("Couldn't find ability " + abilitySignature.getUniqueName());
		}

		Class<?>[] argsTypes = entry.getDescriptor().getMethodParams(methodName);

		// Get the service
		ServiceTracker tracker = new ServiceTracker(bundleContext, entry.getDescriptor().getServiceName(), null);
		tracker.open();
		Object service = tracker.getService();

		Method method;

		try {

			// Try to get and execute the specified methodp
			method = service.getClass().getMethod(methodName, argsTypes);

			// FIXME tryal
			Serializable output = (Serializable) method.invoke(service, params);

			LOG.debug(methodName + " method for ability " + abilitySignature.getUniqueName() + " was successfully executed");
			LOG.debug("Output was " + output);
			return output;

		} catch (SecurityException e) {
			throw new AbilityEnvironmentException("Unexpected security error during Reflection lookup for the " + methodName + " method in ability "
					+ abilitySignature.getUniqueName(), e);
		} catch (NoSuchMethodException e) {
			// TODO: use a more specific Exception for this situaiton?
			throw new AbilityEnvironmentException("Couldn't find the " + methodName + " method in ability " + abilitySignature.getUniqueName(), e);
		} catch (IllegalArgumentException e) {
			throw new AbilityEnvironmentException("Unexpected Reflection error during " + methodName + " method "
					+ "invocation for ability. IllegalArgumentException " + abilitySignature.getUniqueName(), e);
		} catch (IllegalAccessException e) {
			throw new AbilityEnvironmentException("Unexpected Reflection error during " + methodName + " method "
					+ "invocation for ability. IllegalAccessException " + abilitySignature.getUniqueName(), e);
		} catch (InvocationTargetException e) {
			throw new AbilityEnvironmentException("Unexpected Reflection error during " + methodName + " method "
					+ "invocation for ability. InvocationTargetException " + abilitySignature.getUniqueName(), e);
		}
	}

	public boolean isPresent(AbilitySignature abilitySignature) {

		AbilityEntry entry = installedAbilities.get(abilitySignature);

		return (entry != null);
	}

	public AbstractAbility getAbility(AbilitySignature abilitySignature) {

		AbilityEntry entry = installedAbilities.get(abilitySignature);

		// Ability not found
		if (entry == null) {
			throw new NotFoundException("Couldn't find ability " + abilitySignature.getUniqueName());
		}

		// Here we "brutally" get the .jar file from the path where we know it
		// gets copied. Unfortuately Knopflerfish doesn't seem to offer a method
		// to do this safely and automatically.
		String path = frameworkDir + "/bs/" + entry.getBundleID() + "/jar0";

		LOG.debug("Trying to read .jar from \"" + path + "\"");

		// Convert the file to a byte array
		byte[] jar = null;
		try {
			jar = AbilityOsgi.readBytesFromFile(path);
		} catch (FileNotFoundException e) {
			throw new NotFoundException("Coudln't find ability " + abilitySignature.getUniqueName(), e);
		} catch (IOException e) {
			throw new NotFoundException("Coudln't find ability " + abilitySignature.getUniqueName(), e);
		}

		// Create the ability
		AbstractAbility ability = new AbilityOsgi(abilitySignature.getUniqueName(), abilitySignature.getVersion(), entry.getDescriptor().getServiceName(),
				entry.getDescriptor().getMethods(), jar);

		LOG.debug("Created ability " + ability.getSignature().getUniqueName());

		return ability;
	}

	public List<AbilitySignature> listAbilities() {
		List<AbilitySignature> ret = new ArrayList<AbilitySignature>();

		for (AbilitySignature sig : installedAbilities.keySet()) {
			ret.add(sig);
		}
		return ret;
	}

	/* ******************************* */
	/* IEventDispatcher implementation */
	/* ******************************* */

	public void setEventDispatcher(IEventDispatcher dispatcher) {

		// TODO: it's possible that two threads calling this at the same time
		// will result in a duplicate element, so this should be externally
		// synchronized; but it's extremely unlikely, so ignore it for now
		if (!dispatchers.contains(dispatcher)) {
			dispatchers.add(dispatcher);
		}
	}

	public void removeEventDispatcher(IEventDispatcher dispatcher) {
		dispatchers.remove(dispatcher);
	}

	public void stopEnvironment() {
		framework.shutdown();
		frameworkStarted = false;
	}

	/* *************** */
	/* Private methods */
	/* *************** */

	private void commitAbilityList() throws FileNotFoundException, IOException {

		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(frameworkDir + "/abilityList.bin"));

		oos.writeObject(installedAbilities);

		if (oos != null) {
			oos.close();
		}
	}

	@SuppressWarnings("unchecked")
	private void updateAbilityListFromFile() throws ClassNotFoundException {

		String filePath = frameworkDir + "/abilityList.bin";
		ObjectInputStream ois = null;
		LOG.debug("Reading existing abilities from " + filePath);

		try {
			ois = new ObjectInputStream(new FileInputStream(filePath));
			installedAbilities = (ConcurrentHashMap<AbilitySignature, AbilityEntry>) ois.readObject();
			if (ois != null) {
				ois.close();
			}
		} catch (FileNotFoundException e) {
			LOG.debug("The ability list file " + filePath + " file was not present ", e);
			return;
		} catch (IOException e) {
			LOG.debug("The ability list file " + filePath + " file was not present ", e);
			return;
		}

		LOG.debug("Retrieved " + installedAbilities.size() + " abilities from file");
	}

	public void setAutonomicManager(IAutonomicManager autonomicManager) {
		this.autonomicManager = autonomicManager;
	}

	private void fireAbilityInstalledEvent(AbilitySignature signature) {
		DispatchingUtility.dispatchEvent(dispatchers, AbilityInstalledEvent.class, signature);
	}

	private void fireAbilityUninstalledEvent(AbilitySignature signature) {
		DispatchingUtility.dispatchEvent(dispatchers, AbilityUninstalledEvent.class, signature);
	}

	@Override
	public void installAbility(String inputFileName, String abilityName, String serviceName, String methodName) {

		// String fileName = System.getProperty("user.dir") + "/" + selfletID +
		// "/abilities/" + inputFileName;
		//
		// byte[] bundle = null;
		// try {
		// bundle = AbilityOsgi.readBytesFromFile(fileName);
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		//
		// Class<?>[] arguments = {};
		// MethodEntry me = new MethodEntry(methodName, arguments);
		//
		// ArrayList<MethodEntry> methods = new ArrayList<MethodEntry>();
		// methods.add(me);
		//
		// long version = 100;
		//
		// AbilityOsgi abilityOsgi = new AbilityOsgi(abilityName, version,
		// serviceName, methods, bundle);
		// // FIXME shouldn't this add the ability to a list of abilities?
		// String hash = abilityOsgi.getSignature().getHash();
	}

	@Override
	public ImmutableSet<EventTypeEnum> getReceivedEvents() {
		return ImmutableSet.of(EventTypeEnum.ABILITY_INSTALLED, EventTypeEnum.ABILITY_UNINSTALLED);
	}

	@Override
	public void eventReceived(ISelfletEvent event) {
	}

}