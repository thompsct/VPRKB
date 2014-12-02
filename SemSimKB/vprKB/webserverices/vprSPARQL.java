package vprKB.webserverices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.util.LinkedList;
import java.util.Scanner;

import vprExplorer.Globals;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;


public class vprSPARQL {
	 String host;
	 String server = "http://localhost:3030/VPRKB/";
	 PrefixMappingImpl pmap = new PrefixMappingImpl();
	 LinkedList<String> prefixes = new LinkedList<String>();
	 protected Globals globals;
	 protected QueryExecution qef;
	 protected Model base, model;
	 protected static SPARQLConstants cons = new SPARQLConstants();
	 protected ResultSet results;

	 public vprSPARQL(Globals global) {
		 globals = global;
		 host = server + "query";
		 if (TestServerConnection()==0) {
			 loadPrefixes();
		 }
	 }
	 
	 public int TestServerConnection() {
		
		try (Socket s = new Socket("localhost", 3030)) {
			System.out.println("Server Connection Verified");
			return 0;
		}
		catch (IOException ex) {
			System.out.println("Server Connection failed");
	    }
		
	 	return -1;
	}
	
	protected int loadPrefixes() {
		File prefixtxt = new File("cfg/SPARQLHeader.txt");
		Scanner fis;
		try {
			fis = new Scanner(prefixtxt);
			while (fis.hasNextLine()) {
				prefixes.add(fis.nextLine());
			}
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		}
		for (String s : prefixes) {
			int dIdx = s.indexOf(" "); 
			String prefix = s.substring(0, dIdx);
			String uri = s.substring(dIdx+1, s.length());
			pmap.setNsPrefix(prefix, uri);
		}
		
		return 0;
	}
	//Does database contain specified individual
	public boolean isIndividual(URI subject) {
		host = server + "query";
		String qs = makePrefixString("owl") + cons.ask;
		qs = qs.replace("%o", "owl:NamedIndividual");
		qs = qs.replace("%p", "a");
		qs = qs.replace("%s", "<" + subject.toString()+ ">");
		
		Query query = QueryFactory.create(qs);
		query.setPrefixMapping(pmap);
		
		qef = QueryExecutionFactory.sparqlService(host, query);
		
		if (qef.execAsk())	return true;
		return false;
	}
	
	public int selectDistinct(String predicate, String object) {
		host = server + "query";
		String qs = hasPrefix(predicate) + cons.query;
		qs = qs.replace("%d", "DISTINCT");
		qs = qs.replace("%t", "?s");
		qs = qs.replace("%o", " <" + object + ">");
		qs = qs.replace("%p", predicate);
		qs = qs.replace("%s", "?s");
		
		Query query = QueryFactory.create(qs);
		query.setPrefixMapping(pmap);
		
		qef = QueryExecutionFactory.sparqlService(host, query);
		
		results = qef.execSelect();
		return 0;
	}

	public int selectDistinct(String predicate, URI object) {
		return selectDistinct(predicate, object.toString());
	}
	
	public String selectObjectbyProperty(String predicate, String property) {
		host = server + "query";
		String qs = hasPrefix(predicate) + cons.selfil;
		qs = qs.replace("%f", property);
		qs = qs.replace("%p", predicate);
		
		Query query = QueryFactory.create(qs);
		query.setPrefixMapping(pmap);
		qef = QueryExecutionFactory.sparqlService(host, query);

		ResultSet result = qef.execSelect();
		
		return parseResource("?s", result);
	}
	
	public int describeGraph(URI graph) {
		host = server + "query";
		String qs = cons.describe;
		qs = qs.replace("%t", graph.toString());
		
		Query query = QueryFactory.create(qs);
		query.setPrefixMapping(pmap);
		
		qef = QueryExecutionFactory.sparqlService(host, query);
		
		model = qef.execDescribe();
		return 0;
	}
	
	//Query server for property of node
	public String getSingProperty(String predicate, URI cmpturi,boolean literal) {
		host = server + "query";
		String qs = hasPrefix(predicate) + cons.query;
		qs = qs.replace("%d", "");
		qs = qs.replace("%t", "?o");
		qs = qs.replace("%o", "?o");
		qs = qs.replace("%p", predicate);
		qs = qs.replace("%s", "<" + cmpturi.toString()+ ">");
		
		Query query = QueryFactory.create(qs);
		query.setPrefixMapping(pmap);
		
		qef = QueryExecutionFactory.sparqlService(host, query);
		
		ResultSet result = qef.execSelect();
		String request;
		if (literal) request = parseLiteral("?o", result);
			else request = parseResource("?o", result);
		return request;
	}
	
	public String getSingModelProperty(String predicate, URI cmpturi,boolean literal) {
		String qs = hasPrefix(predicate) + cons.query;
		qs = qs.replace("%d", "");
		qs = qs.replace("%t", "?o");
		qs = qs.replace("%o", "?o");
		qs = qs.replace("%p", predicate);
		qs = qs.replace("%s", "<" + cmpturi.toString()+ ">");
		
		Query query = QueryFactory.create(qs);
		query.setPrefixMapping(pmap);
		
		qef = QueryExecutionFactory.create(query, model);
		
		ResultSet result = qef.execSelect();
		String request;
		if (literal) request = parseLiteral("?o", result);
			else request = parseResource("?o", result);
		return request;
	}
	
	public LinkedList<String> getModelProperty(String predicate, URI cmpturi) {
		host = server + "query";
		String qs = hasPrefix(predicate) + cons.query;
		qs = qs.replace("%d", "");
		qs = qs.replace("%t", "?o");
		qs = qs.replace("%o", "?o");
		qs = qs.replace("%p", predicate);
		qs = qs.replace("%s", "<" + cmpturi.toString()+ ">");
		
		Query query = QueryFactory.create(qs);
		query.setPrefixMapping(pmap);
		
		qef = QueryExecutionFactory.sparqlService(host, query);
		
		ResultSet result = qef.execSelect();
		LinkedList<String> request;
		
		request = getResultsasStrings("?o", result);
		return request;
	}
	
	public LinkedList<String> getMultModelProperty(String predicate, URI cmpturi) {
		String qs = hasPrefix(predicate) + cons.query;
		qs = qs.replace("%d", "");
		qs = qs.replace("%t", "?o");
		qs = qs.replace("%o", "?o");
		qs = qs.replace("%p", predicate);
		qs = qs.replace("%s", "<" + cmpturi.toString()+ ">");
		
		Query query = QueryFactory.create(qs);
		query.setPrefixMapping(pmap);
		
		qef = QueryExecutionFactory.create(query, model);
		
		ResultSet result = qef.execSelect();
		LinkedList<String> request;
		
		request = getResultsasStrings("?o", result);
		return request;
	}
	
	//Count number of instance
	public int countObj(String type) {
		host = server + "query";
		String qs = makePrefixString("owl") + cons.ocount;
		qs = qs.replace("%t", type);
		
		Query query = QueryFactory.create(qs);
		query.setPrefixMapping(pmap);
		
		qef = QueryExecutionFactory.sparqlService(host, query);
		
		ResultSet result = qef.execSelect();
		String cnt = parseLiteral("?count", result);
		return Integer.parseInt(cnt);	
	}
	
	//Get all physical properties associated with composite
	public LinkedList<String> getComponentPropertyList(URI cmpturi) {
		host = server + "query";
		String qs = makePrefixString("owl") + makePrefixString("VPRKB") + cons.axproplist;
		qs = qs.replace("%s", "<" + cmpturi.toString() + ">");
		
		Query query = QueryFactory.create(qs);
		query.setPrefixMapping(pmap);
		
		qef = QueryExecutionFactory.sparqlService(host, query);
		
		ResultSet result = qef.execSelect();
		return getResultsasStrings("?property", result);
	}
	
	//Get all property/model relations associated with composite
	public LinkedList<String> getComponentPropModelMap(URI cmpturi, URI property) {
		host = server + "query";
		String qs = makePrefixString("owl") + makePrefixString("model-qualifiers") + cons.axpropmodlist;
		qs = qs.replace("%s", "<" + cmpturi.toString() + ">");
		qs = qs.replace("%p", "<" + property.toString() + ">");
		
		Query query = QueryFactory.create(qs);
		query.setPrefixMapping(pmap);
		
		qef = QueryExecutionFactory.sparqlService(host, query);
		
		ResultSet result = qef.execSelect();
		return getResultsasStrings("?model", result);
	}
		
	public LinkedList<String> getResultLabels() {
		LinkedList<String> rslts = new LinkedList<String>();
		
		while (results.hasNext()) {
			QuerySolution sol = results.next();
			RDFNode node = sol.get("?s");
			String label = getSingProperty("rdfs:label", URI.create(node.toString()), true);
			
			rslts.add(label);
		}
		return rslts;
	}
	
	//Translate list of resources into strings
	public LinkedList<String> getResultsasStrings(String var, ResultSet result) {
		LinkedList<String> rslts = new LinkedList<String>();
		String request = null;
		
		while (result.hasNext()) {
			QuerySolution sol = result.next();
		    Resource thing= sol.getResource(var);
		    if (thing==null) continue;
		    request = thing.toString();
			rslts.add(request);
		}
		return rslts;
	}
		
	protected String makePrefixString(String pre) {
		String prefix = "PREFIX " + pre + ": <" + pmap.getNsPrefixURI(pre) + ">";
		return prefix;
	}
	
	//Check if query is using a prefix
	private String hasPrefix(String predicate) {
		if (predicate.contains(":")) {
			int dIdx = predicate.indexOf(":"); 
			String prefix = predicate.substring(0, dIdx);
			return makePrefixString(prefix);
		}
		return "";
	}
	//Return resource URI as string
	public String parseResource(String var,ResultSet result) {
		String request = null;
		while (result.hasNext()) {
		    QuerySolution row= result.next();
		    Resource thing= row.getResource(var);
		    if (thing==null) continue;
		    request = thing.toString();
		    if ((request.matches("owl:NamedIndividual")) || (request.matches("http://www.w3.org/2002/07/owl#NamedIndividual"))) {
		    	request=null;
		    	continue;
		    }	
		    else break;
		}
		return request;
	}
	//Return literal as string
	public String parseLiteral(String predicate,ResultSet result) {
		String request = null;
		while (result.hasNext()) {
		    QuerySolution row= result.next();
		    Literal thing= row.getLiteral(predicate);
		    if (thing==null) continue;
		    request = thing.getString();
		    break;
		}
		return request;
	}
}
