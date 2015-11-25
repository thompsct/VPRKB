package semsimKB.definitions;

import java.net.URI;

import org.semanticweb.owlapi.model.IRI;

/** SemSimRelations describe the relationship that an annotated SemSimComponent
 * has with an annotation value. Examples include SemSimConstants.REFERS_TO_RELATION
 * and SemSimConstants.PART_OF_RELATION */
public class SemSimRelation {
	/**
	 * Look up a URI's corresponding {@link SemSimRelation}
	 * 
	 * @param uri The URI key
	 * @return The SemSimRelation value for the URI key or else null if not found
	 */
	public static KBRelations getRelationFromURI(URI uri){
		for (KBRelations rel : KBRelations.values()) {
			if (rel.getURI().equals(uri)) return rel;
		}
		return KBRelations.UNKNOWN;
	}
	
	public enum KBRelations {
		HAS_SOURCE("hasSource", RDFNamespace.SEMSIM_NAMESPACE, "physical process has thermodynamic source entity"),
		HAS_SINK("hasSink", RDFNamespace.SEMSIM_NAMESPACE, "physical process has thermodynamic sink entity"),
		HAS_MEDIATOR("hasMediator", RDFNamespace.SEMSIM_NAMESPACE, "physical process has thermodynamic mediator entity"),
		HAS_SOURCE_PARTICIPANT("hasSourceParticipant", RDFNamespace.SEMSIM_NAMESPACE, ""),
		HAS_SINK_PARTICIPANT("hasSinkParticipant", RDFNamespace.SEMSIM_NAMESPACE, ""),
		HAS_MEDIATOR_PARTICIPANT("hasMediatorParticipant", RDFNamespace.SEMSIM_NAMESPACE, ""),
		
		HREF_VALUE_OF_IMPORT("hrefValueOfImport", RDFNamespace.SEMSIM_NAMESPACE, ""),
		METADATA_ID("metadataID", RDFNamespace.SEMSIM_NAMESPACE, "a semsim model component has some metadata id (to support SBML and CellML metadata IDind)"),
		HAS_PHYSICAL_DEFINITION("hasPhysicalDefinition", RDFNamespace.SEMSIM_NAMESPACE, "Refers to ontology term"),

		HAS_UNIT("hasUnit", RDFNamespace.SEMSIM_NAMESPACE, "physical property has physical units"),
		HAS_PHYSICAL_PROPERTY("hasPhysicalProperty", RDFNamespace.SEMSIM_NAMESPACE, "physical property of an entity or process"),
		PHYSICAL_PROPERTY_OF("physicalPropertyOf", RDFNamespace.SEMSIM_NAMESPACE, "physical entity or process associated with a property"),
		BQB_HAS_PART("hasPart", RDFNamespace.BQB_NAMESPACE, 
				"The biological entity represented by the model element includes the subject of the referenced resource, either physically or logically"),
		BQB_IS_PART_OF("isPartOf", RDFNamespace.BQB_NAMESPACE, "The biological entity represented by the model element is a physical or logical part of the subject of the referenced resource"),
		BQB_IS("is", RDFNamespace.BQB_NAMESPACE, 
				"The biological entity represented by the model element has identity with the subject of the referenced resource"),
		BQB_IS_VERSION_OF("isVersionOf", RDFNamespace.BQB_NAMESPACE, "The biological entity represented by the model element is a version or an instance of the subject of the referenced resource"),
		BQB_OCCURS_IN("occursIn", RDFNamespace.BQB_NAMESPACE, "Model processes occur in some taxon"),
		BQM_IS("is", RDFNamespace.BQM_NAMESPACE, "The modelling object represented by the model element is identical with the subject of the referenced resource"),
		BQM_IS_DESCRIBED_BY("isDescribedBy", RDFNamespace.BQM_NAMESPACE, "The modelling object represented by the model element is described by the subject of the referenced resource"),
		BQM_IS_DERIVED_FROM("isDerivedFrom", RDFNamespace.BQM_NAMESPACE, "The modelling object represented by the model element is derived from the modelling object represented by the referenced resource"),

		HAS_NAME("name", RDFNamespace.SEMSIM_NAMESPACE, "semsim component has name"),
		UNKNOWN("unknown", RDFNamespace.VPR_NAMESPACE, "Unrecognized Relation Type");

		private String name;
		private URI uri;
		private String description;
		private String sparqlcode;
	
	/** Class constructor (generally you'd want to use the relations in SemSimConstants,
	 * rather than construct a new SemSimRelation de novo)
	 * @param description A free-text description of the relation
	 * @param relationURI A URI for the relation */
		KBRelations(String name, RDFNamespace namespace, String desc) {
			this.name = name;
			this.uri = URI.create(namespace.getNamespace() + name);
			description = desc;
			sparqlcode = namespace.getOWLid() + ":" + name;
		}

		public String getName() {
			return name;
		}
		
		/** @return The URI of the relation */
		public URI getURI() {
			return uri;
		}
		
		public String getURIasString() {
			return uri.toString();
		}
		
		public String getDescription() {
			return description;
		}
		
		public String getSPARQLCode() {
			return sparqlcode;
		}
		
		public IRI getIRI() {
			return IRI.create(uri);
		}
	}
}
