package semsimKB.webservices;

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
	public final String selfilmult = "SELECT ?s WHERE {%x. ?s %p ?o FILTER regex(?o, '%f', %g)}";
	public final String sdescribe = "DESCRIBE %s WHERE {<%s> %p %o}";
	
	public final String axproplist = "SELECT ?property WHERE { ?axiom owl:annotatedSource %s." +
			"?axiom owl:annotatedTarget ?property}";
	
	public final String axpropmodlist = "SELECT ?model WHERE { ?axiom owl:annotatedSource %s." +
			"?axiom owl:annotatedTarget %p. ?axiom model-qualifiers:isDescribedBy ?model}";
	
	public final String ocount = "SELECT (COUNT(?instance) AS ?count) WHERE { ?instance a %t}";
	
}
