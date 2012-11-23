package it.polimi.elet.selflet.knowledge;

/**
 * Simple storage class for all the knowledges within the SelfLet
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IKnowledgesContainer {

	IGeneralKnowledge getGeneralKnowledge();

	IServiceKnowledge getServiceKnowledge();

	ITypeKnowledge getTypeKnowledge();

}
