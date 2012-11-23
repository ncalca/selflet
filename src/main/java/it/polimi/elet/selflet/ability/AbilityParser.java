package it.polimi.elet.selflet.ability;

/**
 * This code has been factored out from the SelfLetInitializer because it was
 * not referenced anymore. However it can be useful for reference
 * 
 * */
public class AbilityParser {

	// private AbstractAbility prepareAbility(String workingDir, String
	// abilityName, String serviceName, List<MethodEntry> methodsArray) {
	//
	// // System.out.println("WorkingDir: " + workingDir);
	// // System.out.println("AbilityName: " + abilityName);
	// // System.out.print("ServiceName: " + serviceName);
	// // System.out.println("MethodName: " + methodName);
	//
	// String fileName = workingDir + "/abilities/" + abilityName;
	// // String fileName = workingDir + abilityName;
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
	// // MethodEntry me = new MethodEntry(methodName, arguments);
	// // List<MethodEntry> methodsArray = new ArrayList<MethodEntry>();
	// // methodsArray.add(me);
	//
	// long version = 100;
	//
	// AbilityOsgi a = new AbilityOsgi(abilityName, version, serviceName,
	// methodsArray, bundle);
	//
	// // String hash = a.getSignature().getHash();
	// // System.out.println(hash);
	//
	// return a;
	// }
	//
	// private List<AbstractAbility> createAbstractAbilities() {
	//
	// Class<?>[] methodArguments;
	// List<AbstractAbility> abstractAbilities = new
	// ArrayList<AbstractAbility>();
	// /*
	// * // Iterates over abilities for (AbilityType abilityType :
	// * abilitiesList) {
	// *
	// * List<Method> methods = abilityType.getMethods().getMethod();
	// * List<MethodEntry> methodsArray = new ArrayList<MethodEntry>();
	// *
	// * // Iterates over methods within the ability for (Method method :
	// * methods) {
	// *
	// * if (method != null) { // Creates the argument array for the
	// * prepareAbility method List<String> parameters =
	// * method.getParamType();
	// *
	// * methodArguments = new Class[parameters.size()];
	// *
	// * // Iterates over the parameters of the method for (int i = 0; i <
	// * parameters.size(); i++) { String type = parameters.get(i);
	// * methodArguments[i] = StringToClassConversion .convert(type); } } else
	// * { methodArguments = new Class[] {}; }
	// *
	// * MethodEntry me = new MethodEntry(method.getName(), methodArguments);
	// * methodsArray.add(me); }
	// *
	// * String fileName = abilityType.getFile();
	// *
	// * // remove the "/abilities/" prefix if
	// * (fileName.startsWith("/abilities/")) fileName =
	// * fileName.substring("/abilities/".length());
	// *
	// * AbstractAbility ability = prepareAbility(workingDirectory, fileName,
	// * abilityType.getService(), methodsArray);
	// *
	// * abstractAbilities.add(ability);
	// *
	// * // Set the ability signature type property
	// * selfLetInstance.setTypeProperty(fileName + "Signature",
	// * ability.getSignature());
	// *
	// * }
	// */
	// return abstractAbilities;
	// }

}
