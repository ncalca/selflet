package it.polimi.elet.selflet.action;

import it.polimi.elet.selflet.exceptions.ActionException;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import com.google.common.collect.ImmutableSet;

/**
 * This class is in charge of loading action classes from files
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class JavassistActionCreator {

	private static final String ACTION_EXECUTE_METHOD = "executeAction";

	private static final ImmutableSet<String> PACKAGES = ImmutableSet.of("it.polimi.elet.selflet.ability", "it.polimi.elet.selflet.action",
			"it.polimi.elet.selflet.behavior", "it.polimi.elet.selflet.message", "it.polimi.elet.selflet.negotiation", "it.polimi.elet.selflet.utilities",
			"it.polimi.elet.selflet.utilities.factories", "it.polimi.elet.selflet.knowledge", "it.polimi.elet.selflet.exceptions", "java.util",
			"it.polimi.elet.selflet.load");

	private ClassPool classPool;

	private Action action;

	public JavassistActionCreator(Action action) {
		this.action = action;
		init();
	}

	public IJavassistAction createAction() throws ActionException {

		if (action.getActionExpression().isEmpty()) {
			return new EmptyJavassistAction();
		}

		return loadAction();
	}

	private IJavassistAction loadAction() {

		// String path = UtilitiesProvider.getWorkingDir() + "/actions/" +
		// actionName;
		String methodCode = action.getActionExpression();

		// compile time objects
		CtClass myClass = null;
		CtMethod myMethod = null;

		IJavassistAction javassistAction = null;
		Exception exception = null;

		try {
			myClass = classPool.getAndRename("it.polimi.elet.selflet.action.EmptyJavassistAction",
					"it.polimi.elet.selflet.action." + action.getName().replace('.', '_'));
			defrostIfNecessary(myClass);
			myMethod = myClass.getDeclaredMethod(ACTION_EXECUTE_METHOD);
			myMethod.setBody(methodCode);
			javassistAction = (IJavassistAction) myClass.toClass().newInstance();
		} catch (NotFoundException e) {
			exception = e;
		} catch (CannotCompileException e) {
			exception = e;
		} catch (InstantiationException e) {
			exception = e;
		} catch (IllegalAccessException e) {
			exception = e;
		} finally {

			if (exception != null) {
				throw new ActionException("Javassist error: cannot load action " + action.getName(), exception);
			}
		}

		return javassistAction;
	}

	private void defrostIfNecessary(CtClass ctClass) {
		if (ctClass.isFrozen()) {
			ctClass.defrost();
		}
	}

	private void init() {
		classPool = ClassPool.getDefault();
		// add the class path used to load this class
		classPool.insertClassPath(new ClassClassPath(this.getClass()));
		for (String packageName : PACKAGES) {
			classPool.importPackage(packageName);
		}
	}

}
