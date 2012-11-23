package it.polimi.elet.selflet.injectorModules;

import it.polimi.elet.selflet.id.ISelfLetID;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Factory for Guice modules
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class GuiceModuleFactory {

	private GuiceModuleFactory() {
		// private constructor
	}

	public static Injector buildProductionModule(ISelfLetID selfletID) {
		return buildModule(new SelfLetProductionModule(selfletID));
	}

	public static Injector buildModule(AbstractModule module) {
		return Guice.createInjector(module);
	}

}
