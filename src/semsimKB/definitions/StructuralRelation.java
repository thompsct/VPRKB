package semsimKB.definitions;

import java.net.URI;

/** A type of SemSimRelation for establishing structural relationships between
 * SemSim Physical Entities. */

// A structural relationship between two physical entities
public enum StructuralRelation  {
	PART_OF_RELATION("part of", "physical entity is part of another physical entity", 
			RDFNamespace.RO_NAMESPACE.getNamespace() + "part_of", "ro:part_of"),
	HAS_PART_RELATION("has part", "physical entity has part other physical entity",
			RDFNamespace.RO_NAMESPACE.getNamespace() + "has_part", "ro:has_part"),
	CONTAINED_IN_RELATION("contained in", "physical entity is contained in another physical entity",
			RDFNamespace.RO_NAMESPACE.getNamespace() + "contained_in", "ro:contained_in"),
	CONTAINS_RELATION("contains", "physical entity contains another physical entity",
			RDFNamespace.RO_NAMESPACE.getNamespace() + "contains", "ro:contains"),
	ADJACENT_RELATION("adjacent to", "physical entity is adjacent to another physical entity",
			RDFNamespace.SEMSIM_NAMESPACE.getNamespace() + "adjacentTo", "semsim:adjacent_to"),
	INDEX_ENTITY_RELATION("index entity", "physical entity is index entity for another physical entity", 
			RDFNamespace.SEMSIM_NAMESPACE.getNamespace() + "hasIndexEntity", "semsim:hasIndexEntity");
	
	String description;
	String shortdesc;
	String sparqlcode;
	String uri;
	
	StructuralRelation(String sdesc, String desc, String u, String sparql) {
		shortdesc = sdesc;
		description = desc;
		uri = u;
		sparqlcode = sparql;
	}
	
	public String getShortDescription() {
		return shortdesc;
	}
	
	public String getDescription() {
		return description;
	}
	
	public URI getURI() {
		return URI.create(uri);
	}
	
	public String getURIasString() {
		return uri;
	}
	
	public String getSparqlCode() {
		return sparqlcode;
	}
}
