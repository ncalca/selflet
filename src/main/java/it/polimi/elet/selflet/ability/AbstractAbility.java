package it.polimi.elet.selflet.ability;

import java.io.Serializable;

/**
 * An abstract class representing an Ability. Implementing classes need to
 * implement the <code>getAbility</code> and <code>CalculateHash</code> methods,
 * while getters for signature and descriptor are provided.
 * 
 * @author Davide Devescovi
 */
public abstract class AbstractAbility implements Serializable {

	private static final long serialVersionUID = 4242280289095064483L;

	private AbilitySignature signature;
	private IAbilityDescriptor descriptor;

	/**
	 * Constructs an Ability; must be called by implementing classes to
	 * initialize the <code>signature</code> and <code>descriptor</code> fields.
	 * 
	 * @param uniqueName
	 *            the unique name of the Ability
	 * @param version
	 *            the version of the Ability
	 * @param descriptor
	 *            the Ability's descriptor
	 * @param ability
	 *            the actual Ability
	 */
	protected AbstractAbility(String uniqueName, long version, IAbilityDescriptor descriptor, Serializable ability) {
		this.descriptor = descriptor;
		String hash = calculateHash(uniqueName, version, descriptor, ability);
		signature = new AbilitySignature(uniqueName, version, hash);
	}

	public AbilitySignature getSignature() {
		return signature;
	}

	public IAbilityDescriptor getDescriptor() {
		return descriptor;
	}

	public String toString() {
		return signature.toString();
	}

	/**
	 * Gets the actual Ability.
	 * 
	 * @return an Object (depending on the implementation) representing the
	 *         actual Ability
	 */
	public abstract Serializable getAbility();

	/**
	 * Calculates the hash of this Ability, based on all the information of the
	 * Ability and the Ability itself.
	 * 
	 * @param uniqueName
	 *            the unique name of the Ability
	 * @param version
	 *            the version of the Ability
	 * @param descriptor
	 *            the Ability's descriptor
	 * @param ability
	 *            the actual Ability
	 * 
	 * @return a String representing the calculated hash
	 */
	protected abstract String calculateHash(String uniqueName, long version, IAbilityDescriptor descriptor, Serializable ability);
}