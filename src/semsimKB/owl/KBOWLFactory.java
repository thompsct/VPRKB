package semsimKB.owl;

import java.util.HashSet;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.util.Map;
import java.util.Set;

public class KBOWLFactory {

	public static OWLDataFactory factory = OWLManager.createOWLOntologyManager().getOWLDataFactory();

	public KBOWLFactory() {}

	// METHODS ON INDIVIDUALS
	public static void createSemSimIndividual(OWLOntology ont, String indname, OWLClass parent, String suffix, OWLOntologyManager manager){
		OWLIndividual ind = factory.getOWLNamedIndividual(IRI.create(indname + suffix));
		OWLIndividualAxiom axiom = factory.getOWLClassAssertionAxiom(parent, ind);
		if(!ont.getAxioms().contains(axiom)){
			AddAxiom addAxiom = new AddAxiom(ont, axiom);
			manager.applyChange(addAxiom);
		}
	}

	// SET AN OBJECT PROPERTY FOR ONE INDIVIDUAL
	public static void setIndObjectProperty(OWLOntology ont, String subject, String object, String rel, OWLOntologyManager manager)
			throws OWLException {

		AddAxiom addAxiom = new AddAxiom(ont, createIndObjectPropertyAxiom(ont, subject, object, rel, manager));
		manager.applyChange(addAxiom);
	}
	
	public static OWLAxiom createIndObjectPropertyAxiom(OWLOntology ont, String subject,
			String object, String rel,OWLOntologyManager manager) throws OWLException {
		OWLIndividual ind = factory.getOWLNamedIndividual(IRI.create(subject));
		OWLIndividual value = factory.getOWLNamedIndividual(IRI.create(object));
		OWLObjectProperty prop = factory.getOWLObjectProperty(IRI.create(rel));
		OWLAxiom axiom = factory.getOWLObjectPropertyAssertionAxiom(prop, ind,value);
		return axiom;
	}
	
	public static OWLAxiom addAnnotationstoAxiom(OWLOntology ont, OWLAxiom axiom, 
			Set<OWLAnnotation> anns,OWLOntologyManager manager) throws OWLException {
		if(anns!=null) axiom = axiom.getAnnotatedAxiom(anns);
		return axiom;
	}
	
	public static void setIndObjectPropertyWithAnnotations(OWLOntology ont, String subject,
			String object, String rel, Set<OWLAnnotation> annsforrel, OWLOntologyManager manager)
			throws OWLException{
				
			OWLAxiom axiom = createIndObjectPropertyAxiom(ont, subject, object, rel, manager);
			axiom = addAnnotationstoAxiom(ont, axiom, annsforrel, manager);

			AddAxiom addAxiom = new AddAxiom(ont, axiom);
			manager.applyChange(addAxiom);

	}
	
	public static OWLAxiom createIndDatatypePropertyAxiom(OWLOntology ont, String subject, String rel,
			Object val, Set<OWLAnnotation> anns, OWLOntologyManager manager)
		throws OWLException {
		OWLIndividual ind = factory.getOWLNamedIndividual(IRI.create(subject));
		OWLDataProperty prop = factory.getOWLDataProperty(IRI.create(rel));
		OWLLiteral valueconstant;

		Set<String> rangeset = new HashSet<String>();
		for (OWLDataRange x : prop.getRanges(ont)) {
			rangeset.add(x.toString());
		}
		if (rangeset.contains("xsd:boolean"))
			valueconstant = factory.getOWLLiteral((Boolean) val);
		else valueconstant = factory.getOWLLiteral(val.toString());

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

	public static Set<String> getIndividualsInTreeAsStrings(OWLOntology ont, String parentclass) throws OWLException {
		Set<String> indstrings = new HashSet<String>();
		for(String cls : KBOWLFactory.getAllSubclasses(ont, parentclass, true)){
			Set<OWLIndividual> existinginds = factory.getOWLClass(IRI.create(cls)).getIndividuals(ont);
			for (OWLIndividual ind : existinginds) {
				indstrings.add(ind.asOWLNamedIndividual().getIRI().toString());
			}
		}
		return indstrings;
	}

	public static String[] getRDFLabels(OWLOntology ont, OWLEntity ent) {
		OWLLiteral val = null;
		OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		OWLAnnotation[] annarray = ent.getAnnotations(ont, label).toArray(new OWLAnnotation[] {});
		if (annarray.length == 0) {
			return new String[] { "" };
		} else {
			String[] labeltexts = new String[annarray.length];
			for (int x = 0; x < annarray.length; x++) {
				val = (OWLLiteral) annarray[x].getValue();
				labeltexts[x] = val.getLiteral();
			}
			return labeltexts;
		}
	}

	public static void setRDFLabel(OWLOntology ontology, OWLNamedIndividual annind, String value, OWLOntologyManager manager) {
		if(value!=null && !value.equals("")){
			OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
			Set<OWLAnnotation> anns = annind.getAnnotations(ontology, label);
			for (OWLAnnotation ann : anns) {
				OWLAnnotationSubject annsub = annind.getIRI();
				OWLAxiom removeax = factory.getOWLAnnotationAssertionAxiom(annsub,ann);
				manager.applyChange(new RemoveAxiom(ontology, removeax));
			}
			OWLAnnotation newann = factory.getOWLAnnotation(
					factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()),
					factory.getOWLLiteral(value, "en"));
			OWLAxiom ax = factory.getOWLAnnotationAssertionAxiom(annind.getIRI(),newann);
			manager.applyChange(new AddAxiom(ontology, ax));
		}
	}

	public static void addOntologyAnnotation(OWLOntology ont, String property, String val, String lang, OWLOntologyManager manager){
		OWLLiteral lit = factory.getOWLLiteral(val,lang);
		OWLAnnotation anno = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(IRI.create(property)), lit);
		manager.applyChange(new AddOntologyAnnotation(ont, anno));
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
			set.addAll(KBOWLFactory.getIndObjectProperty(ontology, parenturi, relation));
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
	
	public static String getNamespaceFromIRI(String uri) {
		if (uri.contains("#")) {
			return uri.substring(0, uri.indexOf("#") + 1);
		} 
		else {
			if (uri.startsWith("http://purl.obolibrary.org/obo/")) { // To deal with CHEBI and Mouse Adult Gross Anatomy Ontology
				return uri.substring(0, uri.lastIndexOf("_"));
			} 
			else if(uri.startsWith("urn:miriam:")){
				return uri.substring(0, uri.lastIndexOf(":") + 1);
			}
			else {
				return uri.substring(0, uri.lastIndexOf("/") + 1);
			}
		}
	}
}