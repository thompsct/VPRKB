package semsimKB.model;

import java.net.URI;

import semsimKB.SemSimKBConstants;

/**
 * A SemSim model element. A {@link SemSimModel} extends this class as well.
 */
public class SemSimComponent {
	
	private String name;
	private String description;
	private String metadataID;
	private URI compuri;

	/**
	 * Get the component's free-text description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Get the component's name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Set the component's name
	 * 
	 * @param name The name to apply
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Set the component's free-text description
	 * 
	 * @param description The free-text description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Set the component's metadata ID. These ID's are often used
	 * by XML-based modeling languages such as SBML and CellML
	 * to link XML elements to RDF statements that further describe
	 * the elements.
	 * 
	 * @param metadataID The ID to apply
	 */
	public void setMetadataID(String metadataID) {
		this.metadataID = metadataID;
	}

	/**
	 * Get the component's metadata ID. These ID's are often used
	 * by XML-based modeling languages such as SBML and CellML
	 * to link XML elements to RDF statements that further describe
	 * the elements.
	 */
	public String getMetadataID() {
		return metadataID;
	}
	
	public URI getURI() {
		return compuri;
	}
	
	public void setURI(URI uri) {
		compuri = uri;
	}
	
	public  URI getClassURI() {
		return SemSimKBConstants.SEMSIM_COMPONENT_CLASS_URI;
	}
}
