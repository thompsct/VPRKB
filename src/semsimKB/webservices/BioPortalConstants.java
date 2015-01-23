package semsimKB.webservices;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import semsimKB.SemSimKBConstants;

public class BioPortalConstants {
	
	public static final Map<String, String> ONTOLOGY_FULL_NAMES_AND_BIOPORTAL_IDS;
	    static {
	        Map<String, String> aMap = new HashMap<String,String>();
			aMap.put(SemSimKBConstants.ECG_ONTOLOGY_FULLNAME, "1146");
			aMap.put(SemSimKBConstants.PHENOTYPE_AND_TRAIT_ONTOLOGY_FULLNAME, "1107");
			aMap.put(SemSimKBConstants.CHEMICAL_ENTITIES_OF_BIOLOGICAL_INTEREST_FULLNAME, "1007");
			aMap.put(SemSimKBConstants.ONTOLOGY_OF_PHYSICS_FOR_BIOLOGY_FULLNAME, "1141");
			aMap.put(SemSimKBConstants.FOUNDATIONAL_MODEL_OF_ANATOMY_FULLNAME, "1053");
			aMap.put(SemSimKBConstants.GENE_ONTOLOGY_FULLNAME, "1070");
			aMap.put(SemSimKBConstants.SYSTEMS_BIOLOGY_ONTOLOGY_FULLNAME, "1046");
			aMap.put(SemSimKBConstants.SNOMEDCT_FULLNAME, "1353");
			aMap.put(SemSimKBConstants.PROTEIN_ONTOLOGY_FULLNAME, "1062");
			aMap.put(SemSimKBConstants.CELL_TYPE_ONTOLOGY_FULLNAME, "1006");
			aMap.put(SemSimKBConstants.BRENDA_TISSUE_ONTOLOGY_FULLNAME, "1005");
			aMap.put(SemSimKBConstants.UBERON_FULLNAME, "1404");
			aMap.put(SemSimKBConstants.CLINICAL_MEASUREMENT_ONTOLOGY_FULLNAME, "1583");
			aMap.put(SemSimKBConstants.HUMAN_DISEASE_ONTOLOGY_FULLNAME, "1009");
			aMap.put(SemSimKBConstants.MOUSE_ADULT_GROSS_ANATOMY_ONTOLOGY_FULLNAME, "1000");

	        ONTOLOGY_FULL_NAMES_AND_BIOPORTAL_IDS = Collections.unmodifiableMap(aMap);
	    }
}
