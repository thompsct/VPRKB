package semsimKB.webservices;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import semsimKB.SemSimKBConstants;

public class SPARQLConstants extends SemSimKBConstants {
	//local server
	public static final String local = "http://localhost:3030/cfg/SemSimKBBase.owl/";
	public final String triple = "%s %p %o . ";
	public final String dbl = "%p %o ";
	public final String ask = "ASK WHERE {%s %p %o}";
	public final String describe = "DESCRIBE <%t>";
	
	public final String query = "SELECT %d %t WHERE {%s %p %o}";
	public final String multselect = "SELECT %d %t WHERE {?s %q}";
	public final String selfil = "SELECT ?s WHERE {?s %p ?o FILTER regex(?o, '^%f')}";
	public final String sdescribe = "DESCRIBE %s WHERE {<%s> %p %o}";
	
	public final String axproplist = "SELECT ?property WHERE { ?axiom owl:annotatedSource %s." +
			"?axiom owl:annotatedTarget ?property}";
	
	public final String axpropmodlist = "SELECT ?model WHERE { ?axiom owl:annotatedSource %s." +
			"?axiom owl:annotatedTarget %p. ?axiom model-qualifiers:isDescribedBy ?model}";
	
	public final String ocount = "SELECT (COUNT(?instance) AS ?count) WHERE { ?instance a %t}";
	
	public static final String kbpp = "SemSim:Physical_property";
	public static final String kbrpe = "SemSim:Reference_physical_entity";
	public static final String kbcpe = "VPRKB:KB_Physical_Entity";
	public static final String kbcbm = "VPRKB:KB_Bio_Model";
	
	public static final Map<URI, String> classmap;
	static {
		Map<URI, String> aMap = new HashMap<URI, String>();
		aMap.put(PHYSICAL_PROPERTY_CLASS_URI, kbpp);
		aMap.put(REFERENCE_PHYSICAL_ENTITY_CLASS_URI,kbrpe);
		aMap.put(KB_COMPOSITE_CLASS_URI,kbcpe);
		aMap.put(KB_MODEL_URI,kbcbm);
		aMap.put(KB_DATASET_CLASS_URI,"VPRKB:KB_Data_Set");
		aMap.put(KB_PROCESS_CLASS_URI,"VPRKB:KB_Physical_Process");
		classmap = Collections.unmodifiableMap(aMap);
	}
	
}
