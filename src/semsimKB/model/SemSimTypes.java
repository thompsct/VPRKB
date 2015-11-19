package semsimKB.model;

import java.net.URI;

import semsimKB.SemSimKBConstants;

public enum SemSimTypes {
	KB_MODEL("Models", SemSimKBConstants.VPR_NAMESPACE+"KB_Bio_Model", "physkb:KB_Bio_Model"),
	KB_DATASET("Dataset", SemSimKBConstants.VPR_NAMESPACE + "KB_Data_Set", "physkb:KB_Data_Set"),
	PHYSICAL_PROPERTY("Singular Physical Property", SemSimKBConstants.SEMSIM_NAMESPACE + "Physical_property", "SemSim:Physical_property"),
	PHYSICAL_PROPERTY_IN_COMPOSITE("Physical Property for a Composite", SemSimKBConstants.SEMSIM_NAMESPACE + "Physical_property_in_composite", ""),
	REFERENCE_PHYSICAL_ENTITY("Reference Physical Entity", SemSimKBConstants.SEMSIM_NAMESPACE + "Reference_physical_entity", "SemSim:Reference_physical_entity"),
	KB_COMPOSITE_ENTITY("Composite Physical Entity", SemSimKBConstants.VPR_NAMESPACE + "KB_Physical_Entity", "physkb:KB_Physical_Entity"),
	KB_PHYSICAL_PROCESS("Physical Process", SemSimKBConstants.VPR_NAMESPACE + "KB_Physical_Process", "physkb:KB_Physical_Process"),
	PHYSICAL_DEPENDENCY("Physical Dependency", SemSimKBConstants.SEMSIM_NAMESPACE + "Physical_dependency", ""),
	CUSTOM_PHYSICAL_ENTITY("Custom Physical Entity", SemSimKBConstants.SEMSIM_NAMESPACE + "Custom_physical_entity", ""),
	COMPOSITE_PHYSICAL_ENTITY("Composite Physical Entity", SemSimKBConstants.SEMSIM_NAMESPACE + "Composite_physical_entity", ""),
	CUSTOM_PHYSICAL_PROCESS("Custom Physical Process", SemSimKBConstants.SEMSIM_NAMESPACE + "Custom_physical_process", ""),
	REFERENCE_PHYSICAL_PROCESS("Reference Physical Process", SemSimKBConstants.SEMSIM_NAMESPACE + "Reference_physical_process", ""),
	SEMSIM_RELATION("Relation", SemSimKBConstants.SEMSIM_NAMESPACE + "SemSim_relation", "");
	
	private String name;
	private String uri;
	private String sparqlcode;
	
	SemSimTypes(String name, String uri, String sparqlcode) {
		this.name = name;
		this.uri = uri;
		this.sparqlcode = sparqlcode;
	}
	
	public String getName() {
		return name;
	}
	
	public String getURIasString() {
		return uri;
	}
	
	public URI getURI() {
		return URI.create(uri);
	}
	
	public String getSparqlCode() {
		return sparqlcode;
	}
}
