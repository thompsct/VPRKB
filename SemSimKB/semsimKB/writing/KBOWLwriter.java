package semsimKB.writing;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import semsimKB.SemSimKBConstants;
import semsimKB.model.annotation.ReferenceOntologyAnnotation;
import semsimKB.model.physical.DBPhysicalComponent;
import semsimKB.model.physical.DBCompositeEntity;
import semsimKB.model.physical.PhysicalProperty;
import semsim.model.annotation.StructuralRelation;
import semsim.model.physical.PhysicalModelComponent;
import semsimKB.model.KnowledgeBase;
import semsimKB.model.SemSimComponent;
import semsimKB.owl.KBOWLFactory;

public class KBOWLwriter {
	
	protected KnowledgeBase kb;
	private File destination;
	public OWLOntologyManager manager;
	protected OWLOntology ont;
	public OWLDataFactory factory;
	public Hashtable<PhysicalProperty,URI> propertiesandprocessuris = new Hashtable<PhysicalProperty,URI>(); 
	public Map<DBPhysicalComponent,URI> compositeEntitiesAndIndexes = new HashMap<DBPhysicalComponent,URI>();
	public Set<String> singularphysentinds;
	public String vprbase = SemSimKBConstants.VPR_NAMESPACE;
	public String SemSimbase = SemSimKBConstants.SEMSIM_NAMESPACE;
	
	public KBOWLwriter(KnowledgeBase masterKB, File localkb) {kb = masterKB; destination = localkb;}
	
	public OWLOntology createOWLOntologyFromModel() throws OWLException{
		String namespace = vprbase;
		
		manager = OWLManager.createOWLOntologyManager();
		factory = manager.getOWLDataFactory();
				
		// Create a blank semsim ontology with just the base classes
		Set<OWLAxiom> allbaseaxioms = manager.loadOntologyFromOntologyDocument(new File("cfg/SemSimKBBase.owl")).getAxioms();
		
		
		IRI ontiri = IRI.create(namespace.substring(0, namespace.length()-1));  // Gets rid of '#' at end of namespace
		ont = manager.createOntology(allbaseaxioms, ontiri);

		//First add the models
		for (SemSimComponent cbm : kb.getSet(SemSimKBConstants.KB_COMPUTATIONAL_BIOMODEL_URI)) {
		
			String modeluri = cbm.getURI().toString();
			KBOWLFactory.createSemSimIndividual(ont, modeluri, 
					factory.getOWLClass(IRI.create(SemSimKBConstants.KB_COMPUTATIONAL_BIOMODEL_URI.toString())), "", manager);
			KBOWLFactory.setRDFLabel(ont, factory.getOWLNamedIndividual(IRI.create(modeluri)), cbm.getName(), manager);	
		}
		//Get information for reference physical entities
		for (SemSimComponent pre : kb.getSet(SemSimKBConstants.REFERENCE_PHYSICAL_ENTITY_CLASS_URI)) {
			String uri = pre.getURI().toString();
			createPhysicalModelIndividual(((PhysicalModelComponent)pre), uri);
		}
		
		//Write properties
		for (SemSimComponent pp : kb.getSet(SemSimKBConstants.PHYSICAL_PROPERTY_CLASS_URI)) {
			String uri = pp.getURI().toString();
			createPhysicalModelIndividual(((PhysicalModelComponent)pp), uri);
		}
		
		// Update the list of singular reference physical entities for URI naming purposes
		singularphysentinds = KBOWLFactory.getIndividualsInTreeAsStrings(ont, SemSimKBConstants.REFERENCE_PHYSICAL_ENTITY_CLASS_URI.toString());
		
		//Composites
		for (SemSimComponent pce : kb.getSet(SemSimKBConstants.KB_PHYSICAL_ENTITY_CLASS_URI)) {
			processCompositePhysicalEntity((DBCompositeEntity)pce);
		}
				
		String str = new String("Virtual Physiological Rat Knowledge Base");
		KBOWLFactory.addOntologyAnnotation(ont, SemSimKBConstants.MODEL_NAME_URI.toString(), str, "en", manager);
				
		return ont;
	}
	
	public void writeToFile() throws OWLException{
		OWLOntology ont = createOWLOntologyFromModel();
		IRI iri = IRI.create(destination);
		manager.saveOntology(
				ont,
				new RDFXMLOntologyFormat(), 
				iri);
	}
	
	public void writeToFile(URI uri) throws OWLException{
		OWLOntology ont = createOWLOntologyFromModel();
		manager.saveOntology(ont, new RDFXMLOntologyFormat(), IRI.create(uri));
	}
	
	
	private URI processCompositePhysicalEntity(DBCompositeEntity cpe) throws OWLException{
		
		URI compuri = cpe.getURI();
		String label = cpe.getName();
		// If we haven't added this composite entity yet
		if(!compositeEntitiesAndIndexes.containsKey(cpe)){

			KBOWLFactory.createSemSimIndividual(ont, compuri.toString(), 
					factory.getOWLClass(IRI.create(SemSimKBConstants.KB_PHYSICAL_ENTITY_CLASS_URI)), "", manager);
						
			compositeEntitiesAndIndexes.put(cpe, compuri);
			KBOWLFactory.setRDFLabel(ont, factory.getOWLNamedIndividual(IRI.create(compuri)), label, manager); 
		}
		StructuralRelation rel = cpe.getRelation(0);
		
		// Truncate the composite by one entity
		ArrayList<URI> ents = new ArrayList<URI>();
		
		for(int u=0; u<cpe.getComponents().size(); u++){
			ents.add(cpe.getComponents().get(u));
		}
		KBOWLFactory.setIndObjectProperty(ont, compuri.toString(), ents.get(0).toString(),
				SemSimKBConstants.KB_HAS_SUBCOMPONENT_URI.toString(), manager);
		// Establish structural relationship between parts of composite annotation
		KBOWLFactory.setIndObjectProperty(ont, compuri.toString(), ents.get(1).toString(),
				rel.getURI().toString(), manager);
		//Record complexity of entity/composite
		KBOWLFactory.setIndDatatypeProperty(ont, compuri.toString(), SemSimKBConstants.KB_ENTITY_COMPLEXITY_URI.toString(), cpe.getType().toString(),
				manager);
		
		//Add models
		for (URI cpebm : cpe.getBioCompModels()) {
			String cpebmuri = cpebm.toString();
			KBOWLFactory.setIndObjectProperty(ont, compuri.toString(),cpebmuri, SemSimKBConstants.BQM_IS_DERIVED_FROM_URI.toString(), manager);
		}
		
		//Add properties and model associations
		OWLAnnotation ann;	
		Set<OWLAnnotation> anns = new HashSet<OWLAnnotation>();
		for (URI cpepp : cpe.getPropertyList()) {
			anns.clear();
			for (URI moduri :cpe.getPropertyMap().get(cpepp)) {
							
				ann = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(IRI.create(SemSimKBConstants.BQM_IS_DESCRIBED_BY_URI)), IRI.create(moduri));
				anns.add(ann);
			}
			KBOWLFactory.setIndObjectPropertyWithAnnotations(ont, compuri.toString(), cpepp.toString(), SemSimKBConstants.HAS_PHYSICAL_PROPERTY_URI.toString(), anns, manager);
		}
				
		return compuri;
	}

	private void createPhysicalModelIndividual(PhysicalModelComponent pmc, String uriforind) throws OWLException{
		String parenturi = pmc.getClassURI().toString();
				
		KBOWLFactory.createSemSimIndividual(ont, pmc.getURI().toString(), 
				factory.getOWLClass(IRI.create(parenturi)), "", manager);
		
		ReferenceOntologyAnnotation firstann = pmc.getFirstRefersToReferenceOntologyAnnotation();
		KBOWLFactory.setIndDatatypeProperty(ont, uriforind, SemSimbase + "refersTo", 
				firstann.getReferenceURI().toString(), manager);
		
		KBOWLFactory.setRDFLabel(ont, factory.getOWLNamedIndividual(IRI.create(uriforind)), pmc.getName(), manager); 
	} 
}
