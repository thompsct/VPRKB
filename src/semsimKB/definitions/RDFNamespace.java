package semsimKB.definitions;

public enum RDFNamespace  {
	VPR_NAMESPACE("http://www.virtualrat.edu/physkb/", "physkb"),
	SEMSIM_NAMESPACE("http://www.bhi.washington.edu/SemSim#", "semsim"),
	OPB_NAMESPACE("http://bhi.washington.edu/OPB#", "opb"),
	RO_NAMESPACE("http://www.obofoundry.org/ro/ro.owl#", "ro"),
	BQB_NAMESPACE("http://biomodels.net/biology-qualifiers/", "bqb"),
	BQM_NAMESPACE("http://biomodels.net/model-qualifiers/", "bqm"),
	RDF_NAMESPACE("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf"),
	DCTERMS_NAMESPACE("http://purl.org/dc/terms/", "dcterms");
	
	private String namespace;
	private String owlid;
	
	RDFNamespace(String namespace, String id) {
		this.namespace = namespace;
		owlid = id;
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public String getOWLid() {
		return owlid;
	}
}
