package it.polimi.elet.selflet.ability;

import java.io.Serializable;

/**
 * A simple "storage" class to hold together an AbilityDescriptorOsgi and the
 * corresponding bundle ID when the ability is installed in the OSGi framework.
 * 
 * @author Davide Devescovi
 */
public class AbilityEntry implements Serializable {

	private static final long serialVersionUID = 1L;
	private AbilityDescriptorOsgi descriptor;
	private long bundleID;

	/**
	 * Constructs an <code>AbilityEntry</code> object with the specified
	 * parameters.
	 * 
	 * @param descriptor
	 *            the ability descriptor
	 * @param ID
	 *            the bundle ID
	 */
	AbilityEntry(AbilityDescriptorOsgi descriptor, long ID) {
		this.descriptor = descriptor;
		this.bundleID = ID;
	}

	public AbilityDescriptorOsgi getDescriptor() {
		return descriptor;
	}

	public long getBundleID() {
		return bundleID;
	}
}