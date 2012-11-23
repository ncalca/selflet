package it.polimi.elet.selflet.ability;

import java.io.Serializable;

/**
 * A class representing the unique signature of an Ability.
 * 
 * @author Davide Devescovi
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 */
public class AbilitySignature implements Serializable {

	private static final long serialVersionUID = 1L;

	private String uniqueName;
	private long version;
	private String hash;

	/**
	 * Constructs an AbilitySignature object.
	 * 
	 * @param uniqueName
	 *            the unique name of the Ability
	 * @param version
	 *            the version of the Ability
	 * @param hash
	 *            the hash of the Ability
	 * 
	 * @throws NullPointerException
	 *             if the name or hash are null
	 */
	public AbilitySignature(String uniqueName, long version, String hash) {

		if (uniqueName == null || hash == null) {
			throw new IllegalArgumentException("Unique name or hash is null");
		}
		this.uniqueName = uniqueName;
		this.version = version;
		this.hash = hash;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public long getVersion() {
		return version;
	}

	public String getHash() {
		return hash;
	}

	public String toString() {
		return getUniqueName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
		result = prime * result + ((uniqueName == null) ? 0 : uniqueName.hashCode());
		result = prime * result + (int) (version ^ (version >>> 32));
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
		if (!(obj instanceof AbilitySignature)) {
			return false;
		}
		AbilitySignature other = (AbilitySignature) obj;
		if (hash == null) {
			if (other.hash != null) {
				return false;
			}
		} else if (!hash.equals(other.hash)) {
			return false;
		}
		if (uniqueName == null) {
			if (other.uniqueName != null) {
				return false;
			}
		} else if (!uniqueName.equals(other.uniqueName)) {
			return false;
		}
		if (version != other.version) {
			return false;
		}
		return true;
	}

}