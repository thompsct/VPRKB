package semsimKB.webservices;

import semsimKB.definitions.SemSimRelation.KBRelations;

public class SPARQLConstants {
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
	
	public final String axproplist = "SELECT ?property WHERE { ?axiom " + KBRelations.BQM_IS.getSPARQLCode() + " %s." +
			"?axiom " + KBRelations.PHYSICAL_PROPERTY_OF.getSPARQLCode() + " ?property}";
	
	public final String axpropmodlist = "SELECT ?model WHERE { ?axiom " + KBRelations.BQM_IS.getSPARQLCode() + " %s." +
			"?axiom " + KBRelations.PHYSICAL_PROPERTY_OF.getSPARQLCode() + " %p. ?axiom " + KBRelations.BQM_IS_DESCRIBED_BY.getSPARQLCode() + " ?model}";
	
	public final String ocount = "SELECT (COUNT(?instance) AS ?count) WHERE { ?instance a %t}";
	
}
