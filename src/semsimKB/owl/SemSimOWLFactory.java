package semsimKB.owl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Hashtable;
import java.util.HashSet;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.util.Map;
import java.util.Set;

public class SemSimOWLFactory {

	public static OWLDataFactory factory = OWLManager.createOWLOntologyManager().getOWLDataFactory();

	public SemSimOWLFactory() {}

	public static OWLOntology getOntologyIfPreviouslyLoaded(IRI iri,OWLOntologyManager manager) {
		OWLOntologyManager tempmanager = OWLManager.createOWLOntologyManager();
		OWLOntology newont = null;

		try {
			newont = tempmanager.loadOntologyFromOntologyDocument(iri);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}

		OWLOntology returnont = null;
		for (OWLOntology ont : manager.getOntologies()) {
			if (ont.getOntologyID().toString().equals(newont.getOntologyID().toString())) {
				returnont = ont;
				tempmanager.removeOntology(newont);
			}
		}
		return returnont;
	}

	// REMOVE AN OBJECT PROPERTY VALUE FOR ONE CLASS (hasValue)
	public static void removeClsDataValueRestriction(OWLOntology ont, String clsname, String propname, OWLOntologyManager manager)
			throws OWLException {
		OWLClass cls = factory.getOWLClass(IRI.create(clsname));
		OWLDataPropertyExpression prop = factory.getOWLDataProperty(IRI.create(propname));
		Set<OWLClassExpression> descs = cls.getSuperClasses(ont);
		for (OWLClassExpression desc : descs) {
			if (desc instanceof OWLDataHasValue) {
				OWLDataHasValue allrest = (OWLDataHasValue) desc;
				if (allrest.getProperty().asOWLDataProperty().getIRI().toString().equals(prop.asOWLDataProperty().getIRI().toString())) {
					OWLSubClassOfAxiom axiom = factory.getOWLSubClassOfAxiom(cls, desc);
					RemoveAxiom removeax = new RemoveAxiom(ont, axiom);
					manager.applyChange(removeax);
				}
			}
		}
	}
	// SET A DATATYPE PROPERTY FOR A SET OF INDIVIDUALS
	public static void setIndDatatypeProperty(OWLOntology ont, Hashtable<String, Object[]> table,
			String rel, OWLOntologyManager manager) throws OWLException {
		String[] keys = (String[]) table.keySet().toArray(new String[] {});
		for (int i = 0; i < keys.length; i++) {
			Object[] val = (Object[]) table.get(keys[i]);
			for (int y = 0; y < val.length; y++) {
				if(!val[y].equals("") && val[y]!=null){
					setIndDatatypeProperty(ont, keys[i], rel, val[y], manager);
				}
			}
		}
	}
	
	public static OWLAxiom createIndDatatypePropertyAxiom(OWLOntology ont, String subject, String rel,
			Object val, Set<OWLAnnotation> anns, OWLOntologyManager manager) throws OWLException {
		OWLDataProperty prop = factory.getOWLDataProperty(IRI.create(rel));
		OWLLiteral valueconstant;

		Set<String> rangeset = new HashSet<String>();
		for (OWLDataRange x : prop.getRanges(ont)) {
			rangeset.add(x.toString());
		}
		if (rangeset.contains("xsd:boolean"))
			valueconstant = factory.getOWLLiteral((Boolean) val);
		else valueconstant = factory.getOWLLiteral(val.toString());
		
		OWLIndividual ind = factory.getOWLNamedIndividual(IRI.create(subject));
		OWLAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(prop, ind, valueconstant);
		
		if(anns!=null)
			axiom = axiom.getAnnotatedAxiom(anns);
		return axiom;
	}

	
	public static void setIndDatatypeProperty(OWLOntology ont, String induri, String rel, Object val, OWLOntologyManager manager) throws OWLException {
		if(val!=null && !val.equals("")){
			OWLAxiom axiom = createIndDatatypePropertyAxiom(ont, induri, rel, val, null, manager);
			AddAxiom addAxiom = new AddAxiom(ont, axiom);
			manager.applyChange(addAxiom);
		}
	}

	// RETRIEVE THE DATATYPE PROPERTY VALUES FOR ONE INDIVIDUAL (RETURNS A SET)
	public static Set<String> getIndDatatypeProperty(OWLOntology ont, String indname, String propname) throws OWLException {
		Set<String> values = new HashSet<String>();
		OWLIndividual ind = factory.getOWLNamedIndividual(IRI.create(indname));
		OWLDataPropertyExpression prop = factory.getOWLDataProperty(IRI.create(propname));
		Map<OWLDataPropertyExpression, Set<OWLLiteral>> dataprops = ind.getDataPropertyValues(ont);
		Set<OWLDataPropertyExpression> datapropskeyset = dataprops.keySet();
		for (OWLDataPropertyExpression expression : datapropskeyset) {
			if (expression.equals(prop)) {
				if (dataprops.get(expression) != null) {
					for (OWLLiteral referstovalue : dataprops.get(expression)) {
						values.add(referstovalue.getLiteral());
					}
				}
			}
		}
		return values;
	}

	// // RETRIEVE A FUNCTIONAL DATATYPE PROPERTY VALUE FOR ONE INDIVIDUAL
	// (RETURNS A SINGLE STRING)
	public static String getFunctionalIndDatatypeProperty(OWLOntology ont, String indname, String propname) throws OWLException {
		// The hashtable keys are individual IRIs, values are relations (as IRIs)
		Set<String> values = SemSimOWLFactory.getIndDatatypeProperty(ont, indname, propname);
		String[] valuearray = values.toArray(new String[] {});
		String value = "";
		if (valuearray.length == 1) {
			value = valuearray[0];
		} 
		return value;
	}

	// RETRIEVE THE VALUES OF AN OBJECT PROPERTY FOR ONE INDIVIDUAL
	public static Set<String> getIndObjectProperty(OWLOntology ont, String indname, String propname) throws OWLException {
		Set<String> values = new HashSet<String>();
		OWLIndividual ind = factory.getOWLNamedIndividual(IRI.create(indname));
		OWLObjectProperty prop = factory.getOWLObjectProperty(IRI.create(propname));
		Map<OWLObjectPropertyExpression, Set<OWLIndividual>> objprops = ind.getObjectPropertyValues(ont);
		for (OWLObjectPropertyExpression expression : objprops.keySet()) {
			if (expression.equals(prop)) {
				if (objprops.get(expression) != null) {
					for (OWLIndividual referstovalue : objprops.get(expression)) {
						values.add(referstovalue.asOWLNamedIndividual().getIRI().toString());
					}
				}
			}
		}
		return values;
	}
	
	// RETRIEVE THE VALUE OF AN OBJECT PROPERTY FOR ONE INDIVIDUAL
	public static String getFunctionalIndObjectProperty(OWLOntology ont,
			String indname, String propname) throws OWLException {
		String value = "";
		Set<String> values = getIndObjectProperty(ont, indname, propname);
		if(!values.isEmpty()){
			value = values.toArray(new String[]{})[0];
		}
		return value;
	}

	
	
	public static Set<String> getIndividualsAsStrings(OWLOntology ont, String parentclass) {
		Set<String> indstrings = new HashSet<String>();
		Set<OWLIndividual> existinginds = factory.getOWLClass(IRI.create(parentclass)).getIndividuals(ont);
		for (OWLIndividual ind : existinginds) {
			indstrings.add(ind.asOWLNamedIndividual().getIRI().toString());
		}
		return indstrings;
	}
	
	
	public static Set<String> getIndividualsInTreeAsStrings(OWLOntology ont, String parentclass) throws OWLException {
		Set<String> indstrings = new HashSet<String>();
		for(String cls : SemSimOWLFactory.getAllSubclasses(ont, parentclass, true)){
			Set<OWLIndividual> existinginds = factory.getOWLClass(IRI.create(cls)).getIndividuals(ont);
			for (OWLIndividual ind : existinginds) {
				indstrings.add(ind.asOWLNamedIndividual().getIRI().toString());
			}
		}
		return indstrings;
	}

	// CHECK WHETHER AN INDIVIDUAL WITH A GIVEN IRI IS IN AN ONTOLOGY
	public static boolean indExistsInTree(String indtocheck, String parent, OWLOntology ontology) throws OWLException {
		boolean indpresent = false;
		Set<String> allsubclasses = SemSimOWLFactory.getAllSubclasses(ontology, parent, true);
		for (String oneclass : allsubclasses) {
			OWLClass oneowlclass = factory.getOWLClass(IRI.create(oneclass));
			Set<OWLIndividual> inds = oneowlclass.getIndividuals(ontology);
			for (OWLIndividual ind : inds) {
				if (ind.asOWLNamedIndividual().getIRI().toString().equals(indtocheck)) {
					indpresent = true;
				}
			}
		}
		return indpresent;
	}

	public static String[] getRDFLabels(OWLOntology ont, OWLEntity ent) {
		OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		OWLAnnotation[] annarray = ent.getAnnotations(ont, label).toArray(new OWLAnnotation[] {});
		if (annarray.length == 0) {
			return new String[] { "" };
		}		
		String[] labeltexts = new String[annarray.length];
		OWLLiteral val = null;
		for (int x = 0; x < annarray.length; x++) {
			val = (OWLLiteral) annarray[x].getValue();
			labeltexts[x] = val.getLiteral();
		}
		return labeltexts;
		
	}
	
	public static Set<String> getAllSubclasses(OWLOntology ont, String parent, Boolean includeparent) throws OWLException{
		// traverse all nodes that belong to the parent
		Set<String> nodes  = new HashSet<String>();
		if(includeparent) nodes.add(parent);
		OWLClass parentclass = factory.getOWLClass(IRI.create(parent));
		Set<OWLClassExpression> set = parentclass.getSubClasses(ont);
	    for(OWLClassExpression node : set){
		    // store node information
	    	String nodestring = node.asOWLClass().getIRI().toString();
		    nodes.add(nodestring);
		    // traverse children recursively
		    nodes.addAll(getAllSubclasses(ont, nodestring, false));
	    }
	    return nodes;
	}
	
	// Overloaded method for traversing tree - this one allows traversal over multiple relation types
	public static Set<String> traverseRelationTree(OWLOntology ontology, String parenturi, String[] relations) throws OWLException {
	    // traverse all nodes that belong to the parent
		Set<String> nodes  = new HashSet<String>();
		Set<String> set = new HashSet<String>();
		for(String relation : relations){
			set.addAll(SemSimOWLFactory.getIndObjectProperty(ontology, parenturi, relation));
		}
	    for(String node : set){
		    // store node information
		    nodes.add(node);
		    // traverse children recursively
		    traverseRelationTree(ontology, node, relations);
	    }
	    return nodes;
	}
	
	

	public static String getIRIfragment(String uri) {
		String result = "";
		if (!uri.equals("")) {
			if (uri.contains("#")) 
				result = uri.substring(uri.lastIndexOf("#") + 1, uri.length());
			else if (uri.startsWith("http://identifiers.org"))
				result = uri.substring(uri.lastIndexOf("/") + 1, uri.length());
			else if (uri.startsWith("urn:miriam:"))
				result = uri.substring(uri.lastIndexOf(":") + 1, uri.length());
			else if (uri.contains("/"))
				result = uri.substring(uri.lastIndexOf("/") + 1, uri.length());
			else
				result = uri;
		}
		return result;
	}
	
	
	public static String getURIdecodedFragmentFromIRI(String uri){
		return URIdecoding(getIRIfragment(uri));
	}

	public static String URIdecoding(String uri) {
		String result = uri;
		try {
			result = URLDecoder.decode(result, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		result = result.replace("%2A", "*");
		return result;
	}

	
public static String[] getRDFComments(OWLOntology ont, String indiri) {
		Set<String> commentset = new HashSet<String>();
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create(indiri));
		OWLLiteral val = null;
		OWLAnnotationProperty comment = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());
		Set<OWLAnnotation> anns = ind.getAnnotations(ont, comment);
		for (OWLAnnotation annotation : anns) {
			val = (OWLLiteral) annotation.getValue();
			commentset.add(val.getLiteral());
		}
		if(commentset.size()>0) return commentset.toArray(new String[] {});
		else return null;
	}
}