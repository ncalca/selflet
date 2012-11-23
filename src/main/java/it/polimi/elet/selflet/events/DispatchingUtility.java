package it.polimi.elet.selflet.events;

import it.polimi.elet.selflet.exceptions.NotFoundException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

/**
 * Class implementing some events dispatching utilities
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class DispatchingUtility {

	private static final Logger LOG = Logger.getLogger(DispatchingUtility.class);

	private DispatchingUtility() {
		// private constructor
	}

	/**
	 * Dispatches an event to a single dispatcher. The event is instantiated
	 * internally using reflection
	 * 
	 * @param dispatcher
	 *            a <code>IEventDispatcher</code> to which the event is
	 *            dispatched.
	 * 
	 * @param clazz
	 *            the class of the event to be dispatched
	 * 
	 * @param constructorArguments
	 *            the list of arguments needed in the constructor of the event
	 * */

	public static void dispatchEvent(IEventDispatcher dispatcher, Class<?> clazz, Object... constructorArguments) {
		dispatchEvent(Lists.newArrayList(dispatcher), clazz, constructorArguments);
	}

	/**
	 * Dispatches an event across all dispatchers. The event is instantiated
	 * internally using reflection
	 * 
	 * @param dispatchers
	 *            a list of <code>IEventDispatcher</code> to which the event is
	 *            dispatched.
	 * 
	 * @param clazz
	 *            the class of the event to be dispatched
	 * 
	 * @param constructorInput
	 *            the list of arguments needed in the constructor of the event
	 * */
	public static void dispatchEvent(List<IEventDispatcher> dispatchers, Class<?> clazz, Object... constructorInput) {
		if (dispatchers == null || dispatchers.isEmpty()) {
			LOG.warn("Trying to dispatch an event without dispatchers");
			return;
		}

		Constructor<?> constructor = getConstructor(clazz, constructorInput);
		for (IEventDispatcher dispatcher : dispatchers) {
			ISelfletEvent event = createNewEvent(constructor, constructorInput);
			dispatcher.dispatchEvent(event);
			LOG.debug("Dispatching event " + event + " to dispatcher " + dispatcher);
		}

	}

	private static ISelfletEvent createNewEvent(Constructor<?> constructor, Object[] constructorInput) {
		try {
			return (ISelfletEvent) constructor.newInstance(constructorInput);
		} catch (IllegalArgumentException e) {
			LOG.error(getErrorMessage(constructor.getDeclaringClass()), e);
		} catch (InstantiationException e) {
			LOG.error(getErrorMessage(constructor.getDeclaringClass()), e);
		} catch (IllegalAccessException e) {
			LOG.error(getErrorMessage(constructor.getDeclaringClass()), e);
		} catch (InvocationTargetException e) {
			LOG.error(getErrorMessage(constructor.getDeclaringClass()), e);
		}

		throw new IllegalStateException(getErrorMessage(constructor.getDeclaringClass()));
	}

	private static String getErrorMessage(Class<?> clazz) {
		return "Error while dispatching event " + clazz;
	}

	/**
	 * Returns the appropriate constructor for the given arguments
	 * */
	private static Constructor<?> getConstructor(Class<?> clazz, Object[] constructorInput) {

		Constructor<?>[] constructors = clazz.getConstructors();

		// iterate over all available constructor
		for (Constructor<?> constructor : constructors) {
			Class<?>[] parameterTypes = constructor.getParameterTypes();

			if (parameterTypes.length != constructorInput.length) {
				continue;
			}

			boolean isGoodConstructor = true;

			for (int i = 0; i < parameterTypes.length; i++) {

				if (constructorInput[i] == null) {
					continue;
				}

				Class<? extends Object> argumentClass = constructorInput[i].getClass();
				if (!parameterTypes[i].isAssignableFrom(argumentClass)) {
					isGoodConstructor = false;
				}
			}

			if (isGoodConstructor) {
				return constructor;
			}

		}

		throw new NotFoundException("Cannot find constructor for class " + clazz + " with parameters " + Arrays.toString(constructorInput));
	}
}
