package semsimKB.writing;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
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
import semsimKB.annotation.ReferenceOntologyAnnotation;
import semsimKB.annotation.StructuralRelation;
import semsimKB.model.kbbuffer.KnowledgeBase;
import semsimKB.model.physical.DBPhysicalComponent;
import semsimKB.model.physical.DBCompositeEntity;
import semsimKB.model.physical.PhysicalModelComponent;
import semsimKB.model.physical.PhysicalProperty;
import semsimKB.model.physical.ReferencePhysicalEntity;
import semsimKB.model.CompBioModel;
import semsimKB.owl.KBOWLFactory;

public class KBOWLwriter {
	
	protected KnowledgeBase kb;
	private File destination;
	private OWLOntologyManager manager;
	protected OWLOntology ont;
	public OWLDataFactory factory;
	public Hashtable<PhysicalProperty,URI> propertiesandprocessuris = new Hashtable<PhysicalProperty,URI>(); 
	public Map<DBPhysicalComponent,URI> compositeEntitiesAndIndexes = new HashMap<DBPhysicalComponent,URI>();
	public Set<String> singularphysentinds;
	public String vprbase = SemSimKBConstants.VPR_NAMESPACE;
	public String SemSimbase = SemSimKBConstants.SEMSIM_NAMESPACE;
	
	public KBOWLwriter(KnowledgeBase masterKB, File localkb) {
		kb = masterKB; 
		destination = localkb;}
	
	protected OWLOntology createOWLOntologyFromModel() throws OWLException{
		manager = OWLManager.createOWLOntologyManager();
		factory = manager.getOWLDataFactory();
				
		// Create a blank semsim ontology with just the base classes
		Set<OWLAxiom> allbaseaxioms = manager.loadOntologyFromOntologyDocument(new File("cfg/SemSimKBBase.owl")).getAxioms();
		
		
		IRI ontiri = IRI.create(vprbase.substring(0, vprbase.length()-1));  // Gets rid of '#' at end of namespace
		ont = manager.createOntology(allbaseaxioms, ontiri);

		//First add the models
		for (CompBioModel cbm : kb.getModels()) {
			String modeluri = cbm.getURI().toString();
			KBOWLFactory.createSemSimIndividual(ont, modeluri, 
					factory.getOWLClass(IRI.create(SemSimKBConstants.KB_MODEL_URI.toString())), "", manager);
			KBOWLFactory.setRDFLabel(ont, factory.getOWLNamedIndividual(IRI.create(modeluri)), cbm.getName(), manager);	
		}
		//Get information for reference physical entities
		for (ReferencePhysicalEntity pre : kb.getReferenceEntities()) {
			String uri = pre.getURI().toString();
			createPhysicalModelIndividual(((PhysicalModelComponent)pre), uri);
		}
		
		//Write properties
		for (PhysicalProperty pp : kb.getProperties()) {
			String uri = pp.getURI().toString();
			createPhysicalModelIndividual(((PhysicalModelComponent)pp), uri);
		}
		
		// Update the list of singular reference physical entities for URI naming purposes
		singularphysentinds = KBOWLFactory.getIndividualsInTreeAsStrings(ont, SemSimKBConstants.REFERENCE_PHYSICAL_ENTITY_CLASS_URI.toString());
		
		//Composites
		for (DBCompositeEntity pce : kb.getComposites()) {
			processCompositePhysicalEntity(pce);
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
					factory.getOWLClass(IRI.create(SemSimKBConstants.KB_COMPOSITE_CLASS_URI)), "", manager);
						
			compositeEntitiesAndIndexes.put(cpe, compuri);
			KBOWLFactory.setRDFLabel(ont, factory.getOWLNamedIndividual(IRI.create(compuri)), label, manager); 
		}
		StructuralRelation rel = cpe.getRelation();
		
		// Truncate the composite by one entity
		Pair<URI, URI> ents = cpe.getComponentURIs();
		
		KBOWLFactory.setIndObjectProperty(ont, compuri.toString(), ents.getLeft().toString(),
				SemSimKBConstants.KB_HAS_SUBCOMPONENT_URI.toString(), manager);
		// Establish structural relationship between parts of composite annotation
		KBOWLFactory.setIndObjectProperty(ont, compuri.toString(), ents.getRight().toString(),
				rel.getURI().toString(), manager);
		
		//Add models
		for (CompBioModel cpebm : cpe.getBioCompModels()) {
			String cpebmuri = cpebm.getURI().toString();
			KBOWLFactory.setIndObjectProperty(ont, compuri.toString(),cpebmuri, SemSimKBConstants.BQM_IS_DERIVED_FROM_URI.toString(), manager);
		}
		
		//Add properties and model associations
		OWLAnnotation ann;	
		Set<OWLAnnotation> anns = new HashSet<OWLAnnotation>();
		for (PhysicalProperty cpepp : cpe.getPropertyList()) {
			anns.clear();
			for (CompBioModel mod :cpe.getPropertyModelList(cpepp)) {
				String moduri =	mod.getURI().toString();		
				ann = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(IRI.create(SemSimKBConstants.BQM_IS_DESCRIBED_BY_URI)), IRI.create(moduri));
				anns.add(ann);
			}
			KBOWLFactory.setIndObjectPropertyWithAnnotations(ont, compuri.toString(), cpepp.getURI().toString(), SemSimKBConstants.HAS_PHYSICAL_PROPERTY_URI.toString(), anns, manager);
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
