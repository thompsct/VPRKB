package semsimKB.writing;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
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
import semsimKB.annotation.StructuralRelation;
import semsimKB.model.kbbuffer.KnowledgeBase;
import semsimKB.model.physical.DBCompositeEntity;
import semsimKB.model.physical.PhysicalModelComponent;
import semsimKB.model.physical.PhysicalProperty;
import semsimKB.model.physical.ReferencePhysicalEntity;
import semsimKB.model.CompBioModel;
import semsimKB.model.SemSimTypes;
import semsimKB.owl.KBOWLFactory;

public class KBOWLwriter {
	
	protected KnowledgeBase kb;
	private File destination;
	private OWLOntologyManager manager;
	protected OWLOntology ont;
	public OWLDataFactory factory;
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
					factory.getOWLClass(IRI.create(SemSimTypes.KB_MODEL.getURI())), "", manager);
			KBOWLFactory.setRDFLabel(ont, factory.getOWLNamedIndividual(IRI.create(modeluri)), cbm.getName(), manager);	
		}
		//Get information for reference physical entities
		for (ReferencePhysicalEntity pre : kb.getReferenceEntities()) {
			createPhysicalModelIndividual(pre);
		}
		
		//Write properties
		for (PhysicalProperty pp : kb.getProperties()) {
			createPhysicalModelIndividual(pp);
		}
		
		//Composites
		for (DBCompositeEntity pce : kb.getComposites()) {
			processCompositePhysicalEntity(pce);
		}
				
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

		KBOWLFactory.createSemSimIndividual(ont, compuri.toString(), 
				factory.getOWLClass(IRI.create(SemSimTypes.KB_COMPOSITE_ENTITY.getURI())), "", manager);		

		KBOWLFactory.setRDFLabel(ont, factory.getOWLNamedIndividual(IRI.create(compuri)), label, manager); 
		StructuralRelation rel = cpe.getRelation();
		
		// Truncate the composite by one entity
		Pair<URI, URI> ents = cpe.getComponentURIs();
		
		KBOWLFactory.setIndObjectProperty(ont, compuri.toString(), ents.getLeft().toString(),
				StructuralRelation.INDEX_ENTITY_RELATION.getURIasString(), manager);
		// Establish structural relationship between parts of composite annotation
		if (ents.getRight()!=null) {
			KBOWLFactory.setIndObjectProperty(ont, compuri.toString(), ents.getRight().toString(),
					rel.getURI().toString(), manager);
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

	private void createPhysicalModelIndividual(PhysicalModelComponent pmc) throws OWLException{
		KBOWLFactory.createSemSimIndividual(ont, pmc.getURI().toString(), 
				factory.getOWLClass(IRI.create(pmc.getClassURI())), "", manager);
		
		KBOWLFactory.setRDFLabel(ont, factory.getOWLNamedIndividual(IRI.create(pmc.getURI())), pmc.getName(), manager); 
	} 
}
