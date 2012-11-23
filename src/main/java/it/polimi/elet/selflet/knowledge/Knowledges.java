package it.polimi.elet.selflet.knowledge;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Simple storage class to store all the knowledges of the SelfLet. This is
 * typically used to initialized all the components within the SelfLet
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class Knowledges implements IKnowledgesContainer {

	private IGeneralKnowledge generalKnowledge;
	private IServiceKnowledge serviceKnowledge;
	private ITypeKnowledge typeKnowledge;

	@Inject
	public Knowledges(IGeneralKnowledge generalKnowledge, IServiceKnowledge serviceKnowledge, ITypeKnowledge typeKnowledge) {
		this.generalKnowledge = generalKnowledge;
		this.serviceKnowledge = serviceKnowledge;
		this.typeKnowledge = typeKnowledge;
	}

	public IGeneralKnowledge getGeneralKnowledge() {
		return generalKnowledge;
	}

	public IServiceKnowledge getServiceKnowledge() {
		return serviceKnowledge;
	}

	public ITypeKnowledge getTypeKnowledge() {
		return typeKnowledge;
	}

}
