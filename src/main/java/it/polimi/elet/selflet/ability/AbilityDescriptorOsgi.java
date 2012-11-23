package it.polimi.elet.selflet.ability;

import it.polimi.elet.selflet.exceptions.NotFoundException;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * An OSGi implementation of the <code>IAbilityDescriptor</code> interface. The
 * fields characterizing an OSGi Ability are the name of the service to use, the
 * name of the method to run on that service and the types of the arguments to
 * be passed to that method.
 * 
 * @author Davide Devescovi
 */
public class AbilityDescriptorOsgi implements IAbilityDescriptor {

	private static final long serialVersionUID = 1L;

	private String abilityType = AbilityOsgi.OSGI_TYPE;
	private String serviceName;
	private List<MethodEntry> methods;

	/**
	 * Constructs an AbilityDescriptorOSGi object.
	 * 
	 * @param serviceName
	 *            the name of the service to use
	 * @param methods
	 *            the methods which can be invoked on the service
	 * 
	 * @throws NullPointerException
	 *             if the service name or the list of methods are null, or if
	 *             the list of methods is empty
	 */
	public AbilityDescriptorOsgi(String serviceName, List<MethodEntry> methods) {

		if (serviceName == null || methods == null) {
			throw new IllegalArgumentException("Servicename or methods is null");
		}
		this.serviceName = serviceName;
		this.methods = Lists.newArrayList(methods);
	}

	public String getServiceName() {
		return serviceName;
	}

	public List<MethodEntry> getMethods() {
		return Lists.newArrayList(methods);
	}

	/**
	 * Gets the parameters types of the specified method.
	 * 
	 * @param methodName
	 *            the name of the method
	 * 
	 * @return an array of Class objects representing the parameters types of
	 *         the method
	 * 
	 * @throws NotFoundException
	 *             if the specified method is not present in this ability
	 */
	public Class<?>[] getMethodParams(String methodName) throws NotFoundException {

		for (MethodEntry methodEntry : methods) {

			if (methodEntry.getName().equals(methodName)) {
				return methodEntry.getArgsTypes();
			}
		}

		throw new NotFoundException("Can't find a method with name " + methodName);
	}

	/* ********************************* */
	/* IAbilityDescriptor implementation */
	/* ********************************* */

	public String getAbilityType() {
		return abilityType;
	}

	/* ************************************** */
	/* Overridden equals and hashCode methods */
	/* ************************************** */

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof AbilityDescriptorOsgi)) {
			return false;
		}

		AbilityDescriptorOsgi descriptor = (AbilityDescriptorOsgi) obj;

		return (descriptor.serviceName.equals(this.serviceName) && descriptor.methods.equals(this.methods));
	}

	@Override
	public int hashCode() {

		int hashCode = 1;
		hashCode = hashCode * 31 + serviceName.hashCode();
		hashCode = hashCode * 31 + methods.hashCode();

		return hashCode;
	}
}