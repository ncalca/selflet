package it.polimi.elet.selflet.ability;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A simple "storage" class to hold together a method name and the array of its
 * parameters types.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * @author Davide Devescovi
 */
public class MethodEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String name;
	private final Class<?>[] argsTypes;

	/**
	 * Constructs a MethodEntry object with the given parameters.
	 * 
	 * @param name
	 *            the method name
	 * @param argsTypes
	 *            the array of the parameters types
	 * 
	 * @throws NullPointerException
	 *             if any of the parameters is null
	 */
	public MethodEntry(String name, Class<?>[] argsTypes) {

		if (name == null) {
			throw new IllegalArgumentException("The method name can't be null");
		}

		if (argsTypes == null) {
			throw new IllegalArgumentException("The list of argument types can't be null; " + "if the method has no arguments, use an empty array");
		}

		this.name = name;
		this.argsTypes = argsTypes.clone();
	}

	public String getName() {
		return name;
	}

	public Class<?>[] getArgsTypes() {
		return argsTypes;
	}

	public String toString() {
		StringBuffer msg = new StringBuffer();
		msg.append("Method name: " + name + ", args: ");
		for (int i = 0; i < argsTypes.length; i++) {
			msg.append(argsTypes[i].getName());
			msg.append(", ");
		}

		return msg.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(argsTypes);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MethodEntry)) {
			return false;
		}
		MethodEntry other = (MethodEntry) obj;
		if (!Arrays.equals(argsTypes, other.argsTypes)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	/* ************************************** */
	/* Overridden equals and hashCode methods */
	/* ************************************** */

}