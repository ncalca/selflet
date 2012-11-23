package it.polimi.elet.selflet.ability;

import it.polimi.elet.selflet.exceptions.AbilityEnvironmentException;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * An OSGi implementation of the <code>AbstractAbility</code> class, using the
 * Knopflerfish framework. The actual Ability is the array of bytes which makes
 * up the .jar file of the OSGi bundle.
 * 
 * @author Davide Devescovi
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 */
public class AbilityOsgi extends AbstractAbility {

	private static final long serialVersionUID = 1L;

	public static final String OSGI_TYPE = "OSGi";

	private final byte[] bundle;

	/**
	 * Constructs an OSGi Ability.
	 * 
	 * @param uniqueName
	 *            the unique name of the Ability
	 * @param version
	 *            the version of the Ability
	 * @param serviceName
	 *            the name of the OSGi service
	 * @param methods
	 *            the list of the methods which can be invoked on the service
	 * @param bundle
	 *            the .jar file of the OSGi bundle, as a byte array
	 */
	public AbilityOsgi(String uniqueName, long version, String serviceName, List<MethodEntry> methods, byte[] bundle) {
		super(uniqueName, version, new AbilityDescriptorOsgi(serviceName, methods), bundle);
		this.bundle = bundle.clone();
	}

	/* ****************************** */
	/* AbstractAbility implementation */
	/* ****************************** */

	@Override
	public Serializable getAbility() {
		return bundle;
	}

	@Override
	/**
	 * Calculates the hash on all the available information using MD5. The
	 * returned String is in hexadecimal format.
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
	protected String calculateHash(String uniqueName, long version, IAbilityDescriptor descriptor, Serializable ability) {

		if (!descriptor.getAbilityType().equals(OSGI_TYPE)) {
			throw new AbilityEnvironmentException("Unforseen incompatibility error!");
		}

		AbilityDescriptorOsgi descr = (AbilityDescriptorOsgi) descriptor;

		byte[] bytes = (byte[]) ability;

		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new AbilityEnvironmentException("Unforseen MD5 algorithm exception!", e);
		}

		messageDigest.update(uniqueName.getBytes());
		messageDigest.update(Long.toString(version).getBytes());
		messageDigest.update(descr.getServiceName().getBytes());

		for (MethodEntry methodEntry : descr.getMethods()) {

			messageDigest.update(methodEntry.getName().getBytes());

			for (Class<?> clazz : methodEntry.getArgsTypes()) {
				if (clazz == null) {
					continue;
				}
				messageDigest.update(clazz.toString().getBytes());
			}
		}

		messageDigest.update(bytes);

		return encodeHex(messageDigest.digest());
	}

	/* ********************** */
	/* Private helper methods */
	/* ********************** */

	/**
	 * Helper method to convert an array of bytes to a hexadecimal String.
	 * 
	 * @param data
	 *            the array to convert
	 * 
	 * @return the converted hexadecimal String
	 */
	private static String encodeHex(byte[] data) {

		char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

		int length = data.length;

		char[] out = new char[length << 1];

		// two characters form the hex value.
		for (int i = 0, j = 0; i < length; i++) {
			out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
			out[j++] = DIGITS[0x0F & data[i]];
		}

		return new String(out);
	}

	/**
	 * Helper method to put the content of a given file in a byte array.
	 * 
	 * @param filePath
	 *            the path to the file to be loaded
	 * 
	 * @return a byte array containing the loaded file
	 */
	public static byte[] readBytesFromFile(String filePath) throws FileNotFoundException, IOException {

		File file = new File(filePath);

		byte[] array = new byte[(int) file.length()];

		FileInputStream fileInputStream = null;

		fileInputStream = new FileInputStream(file);
		DataInputStream dataInputStream = new DataInputStream(fileInputStream);
		dataInputStream.readFully(array);
		dataInputStream.close();
		fileInputStream.close();

		return array;
	}
}