package semsimKB;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

import semsimKB.annotation.SemSimRelation;

/**
 * A set of constants for working with SemSim models
 */
public class SemSimKBConstants {
	
	// Namespaces
	public static final String VPR_NAMESPACE = "http://www.virtualrat.edu/physkb/";
	public static final String SEMSIM_NAMESPACE = "http://www.bhi.washington.edu/SemSim#";
	public static final String OPB_NAMESPACE = "http://bhi.washington.edu/OPB#";
	public static final String RO_NAMESPACE = "http://www.obofoundry.org/ro/ro.owl#";
	public static final String BQB_NAMESPACE = "http://biomodels.net/biology-qualifiers/";
	public static final String BQM_NAMESPACE = "http://biomodels.net/model-qualifiers/";
	public static final String RDF_NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String DCTERMS_NAMESPACE = "http://purl.org/dc/terms/";
	
	// URIs
	public static final URI HAS_SOURCE_URI = URI.create(SEMSIM_NAMESPACE + "hasSource");
	public static final URI HAS_SINK_URI = URI.create(SEMSIM_NAMESPACE + "hasSink");
	public static final URI HAS_MEDIATOR_URI = URI.create(SEMSIM_NAMESPACE + "hasMediator");
	public static final URI HAS_SOURCE_PARTICIPANT_URI = URI.create(SEMSIM_NAMESPACE + "hasSourceParticipant");
	public static final URI HAS_SINK_PARTICIPANT_URI = URI.create(SEMSIM_NAMESPACE + "hasSinkParticipant");
	public static final URI HAS_MEDIATOR_PARTICIPANT_URI = URI.create(SEMSIM_NAMESPACE + "hasMediatorParticipant");
	
	public static final URI HREF_VALUE_OF_IMPORT_URI = URI.create(SEMSIM_NAMESPACE + "hrefValueOfImport");
	public static final URI METADATA_ID_URI = URI.create(SEMSIM_NAMESPACE + "metadataID");
	public static final URI HAS_PHYSICAL_DEFINITION_URI = URI.create(SEMSIM_NAMESPACE + "hasPhysicalDefinition");
	public static final URI SEMSIM_VERSION_URI = URI.create(SEMSIM_NAMESPACE + "SemSimVersion");
	public static final URI HAS_UNIT_URI = URI.create(SEMSIM_NAMESPACE + "hasUnit");
	public static final URI HAS_PHYSICAL_PROPERTY_URI = URI.create(SEMSIM_NAMESPACE + "hasPhysicalProperty");
	
	public static final URI BQB_HAS_PART_URI = URI.create(BQB_NAMESPACE + "hasPart");
	public static final URI BQB_IS_PART_OF_URI = URI.create(BQB_NAMESPACE + "isPartOf");
	public static final URI BQB_IS_URI = URI.create(BQB_NAMESPACE + "is");
	public static final URI BQB_IS_VERSION_OF_URI = URI.create(BQB_NAMESPACE + "isVersionOf");
	public static final URI BQB_OCCURS_IN_URI = URI.create(BQB_NAMESPACE + "occursIn");
	public static final URI BQM_IS_URI = URI.create(BQM_NAMESPACE + "is");
	public static final URI BQM_IS_DESCRIBED_BY_URI = URI.create(BQM_NAMESPACE + "isDescribedBy");
	public static final URI BQM_IS_DERIVED_FROM_URI = URI.create(BQM_NAMESPACE + "isDerivedFrom");

	
	public static final URI HAS_NAME_URI = URI.create(SEMSIM_NAMESPACE + "name");
	
	// Relations
	public static final SemSimRelation BQB_HAS_PART_RELATION = new SemSimRelation("The biological entity represented by the model element includes the subject of the referenced resource, either physically or logically", BQB_HAS_PART_URI);
	public static final SemSimRelation BQB_IS_RELATION = new SemSimRelation("The biological entity represented by the model element has identity with the subject of the referenced resource", BQB_IS_URI);
	public static final SemSimRelation BQB_IS_PART_OF_RELATION = new SemSimRelation("The biological entity represented by the model element is a physical or logical part of the subject of the referenced resource", BQB_IS_PART_OF_URI);
	public static final SemSimRelation BQB_IS_VERSION_OF_RELATION = new SemSimRelation("The biological entity represented by the model element is a version or an instance of the subject of the referenced resource", BQB_IS_VERSION_OF_URI);
	public static final SemSimRelation BQB_OCCURS_IN_RELATION = new SemSimRelation("Model processes occur in some taxon", BQB_OCCURS_IN_URI);
	public static final SemSimRelation BQM_IS_RELATION = new SemSimRelation("The modelling object represented by the model element is identical with the subject of the referenced resource", BQM_IS_URI);
	public static final SemSimRelation BQM_IS_DESCRIBED_BY_RELATION = new SemSimRelation("The modelling object represented by the model element is described by the subject of the referenced resource", BQM_IS_DESCRIBED_BY_URI);
	public static final SemSimRelation BQM_IS_DERIVED_FROM_RELATION = new SemSimRelation("The modelling object represented by the model element is derived from the modelling object represented by the referenced resource", BQM_IS_DERIVED_FROM_URI);
	
	public static final SemSimRelation HAS_PHYSICAL_DEFINITION_RELATION = new SemSimRelation("Refers to ontology term", HAS_PHYSICAL_DEFINITION_URI);
	public static final SemSimRelation SEMSIM_VERSION_RELATION = new SemSimRelation("Version of SemSim used to create model", SEMSIM_VERSION_URI);
	public static final SemSimRelation HAS_UNIT_RELATION = new SemSimRelation("physical property has physical units", HAS_UNIT_URI);
	public static final SemSimRelation METADATA_ID_RELATION = new SemSimRelation("a semsim model component has some metadata id (to support SBML and CellML metadata IDind)", METADATA_ID_URI);
	public static final SemSimRelation HAS_PHYSICAL_PROPERTY_RELATION = new SemSimRelation("physical property of an entity or process", HAS_PHYSICAL_PROPERTY_URI);
	public static final SemSimRelation HAS_SOURCE_RELATION = new SemSimRelation("physical process has thermodynamic source entity", HAS_SOURCE_URI);
	public static final SemSimRelation HAS_SINK_RELATION = new SemSimRelation("physical process has thermodynamic sink entity", HAS_SINK_URI);
	public static final SemSimRelation HAS_MEDIATOR_RELATION = new SemSimRelation("physical process has thermodynamic mediator entity", HAS_MEDIATOR_URI);

	public static final SemSimRelation HAS_NAME_RELATION = new SemSimRelation("semsim component has name", HAS_NAME_URI);
	
	public static final String BIOPORTAL_API_KEY = "c4192e4b-88a8-4002-ad08-b4636c88df1a";
	
	public static final double SEMSIM_VERSION = 0.1;
	
	public static final Map<URI, SemSimRelation> URIS_AND_SEMSIM_RELATIONS;
	
	public static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();
	
	static{       
        // URIsAndSemSimRelations Map
        Map<URI,SemSimRelation> aMap0 = new HashMap<URI,SemSimRelation>();
		aMap0.put(HAS_PHYSICAL_DEFINITION_URI, HAS_PHYSICAL_DEFINITION_RELATION);
		aMap0.put(SEMSIM_VERSION_URI, SEMSIM_VERSION_RELATION);
		aMap0.put(HAS_UNIT_URI, HAS_UNIT_RELATION);
		aMap0.put(HAS_NAME_URI, HAS_NAME_RELATION);
		aMap0.put(METADATA_ID_URI, METADATA_ID_RELATION);
		aMap0.put(HAS_PHYSICAL_PROPERTY_URI, HAS_PHYSICAL_PROPERTY_RELATION);
		aMap0.put(HAS_SOURCE_URI, HAS_SOURCE_RELATION);
		aMap0.put(HAS_SINK_URI, HAS_SINK_RELATION);
		aMap0.put(HAS_MEDIATOR_URI, HAS_MEDIATOR_RELATION);
		// Model-level stuff
		aMap0.put(BQB_HAS_PART_URI, BQB_HAS_PART_RELATION);
		aMap0.put(BQB_IS_PART_OF_URI, BQB_IS_PART_OF_RELATION);
		aMap0.put(BQB_IS_URI, BQB_IS_RELATION);
		aMap0.put(BQB_IS_VERSION_OF_URI, BQB_IS_VERSION_OF_RELATION);
		aMap0.put(BQB_OCCURS_IN_URI, BQB_OCCURS_IN_RELATION);
		aMap0.put(BQM_IS_URI, BQM_IS_RELATION);
		aMap0.put(BQM_IS_DESCRIBED_BY_URI, BQM_IS_DESCRIBED_BY_RELATION);
		aMap0.put(BQM_IS_DERIVED_FROM_URI, BQM_IS_DERIVED_FROM_RELATION);
		URIS_AND_SEMSIM_RELATIONS = Collections.unmodifiableMap(aMap0);
		
	}
	
	/**
	 * Look up a URI's corresponding {@link SemSimRelation}
	 * 
	 * @param uri The URI key
	 * @return The SemSimRelation value for the URI key or else null if not found
	 */
	public static SemSimRelation getRelationFromURI(URI uri){
			return URIS_AND_SEMSIM_RELATIONS.get(uri);
	}
}