package semsimKB.definitions;

import java.net.URI;

public enum SemSimTypes {
	KB_MODEL("Models", RDFNamespace.VPR_NAMESPACE.getNamespace()+"KBBioSimModel", "physkb:KBBioSimModel"),
	KB_DATASET("Dataset", RDFNamespace.VPR_NAMESPACE.getNamespace() + "KB_Data_Set", "physkb:KB_Data_Set"),
	PHYSICAL_PROPERTY("Singular Physical Property", RDFNamespace.SEMSIM_NAMESPACE.getNamespace() + "Physical_property", "SemSim:Physical_property"),
	PHYSICAL_PROPERTY_IN_COMPOSITE("Physical Property for a Composite", RDFNamespace.SEMSIM_NAMESPACE.getNamespace() + "Physical_property_in_composite", ""),
	REFERENCE_PHYSICAL_ENTITY("Reference Physical Entity", RDFNamespace.SEMSIM_NAMESPACE.getNamespace() + "Reference_physical_entity", "SemSim:Reference_physical_entity"),
	KB_COMPOSITE_ENTITY("Composite Physical Entity", RDFNamespace.VPR_NAMESPACE.getNamespace() + "KB_Physical_Entity", "physkb:KB_Physical_Entity"),
	KB_PHYSICAL_PROCESS("Physical Process", RDFNamespace.VPR_NAMESPACE.getNamespace() + "KB_Physical_Process", "physkb:KB_Physical_Process"),
	PHYSICAL_DEPENDENCY("Physical Dependency", RDFNamespace.SEMSIM_NAMESPACE.getNamespace() + "Physical_dependency", ""),
	CUSTOM_PHYSICAL_ENTITY("Custom Physical Entity", RDFNamespace.SEMSIM_NAMESPACE.getNamespace() + "Custom_physical_entity", ""),
	COMPOSITE_PHYSICAL_ENTITY("Composite Physical Entity", RDFNamespace.SEMSIM_NAMESPACE.getNamespace() + "Composite_physical_entity", ""),
	CUSTOM_PHYSICAL_PROCESS("Custom Physical Process", RDFNamespace.SEMSIM_NAMESPACE.getNamespace() + "Custom_physical_process", ""),
	REFERENCE_PHYSICAL_PROCESS("Reference Physical Process", RDFNamespace.SEMSIM_NAMESPACE.getNamespace() + "Reference_physical_process", ""),
	PROPERTYOF_INSTANCE("Instance of Property of an Entity or Process", RDFNamespace.VPR_NAMESPACE.getNamespace() + "PropertyOfInstance", "physkb:propertyOfInstance");
	
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
