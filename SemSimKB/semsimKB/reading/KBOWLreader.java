package semsimKB.reading;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import semsimKB.SemSimKBConstants;
import semsimKB.model.CompBioModel;
import semsimKB.model.KnowledgeBase;
import semsimKB.model.physical.DBCompositeEntity;
import semsimKB.model.physical.PhysicalProperty;
import semsimKB.model.physical.ReferencePhysicalEntity;
import semsim.model.annotation.StructuralRelation;
import semsim.model.physical.PhysicalModelComponent;
import semsimKB.owl.KBOWLFactory;

public class KBOWLreader {
	private KnowledgeBase KBModel;
	private OWLDataFactory factory;
	private Map<URI, PhysicalModelComponent> URIandPMCmap = new HashMap<URI, PhysicalModelComponent>();	
	public String SemSimbase = SemSimKBConstants.SEMSIM_NAMESPACE;
	
	public KnowledgeBase readFromFile(File file) throws OWLException, CloneNotSupportedException{
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		factory = manager.getOWLDataFactory();
		OWLOntology ont = manager.loadOntologyFromOntologyDocument(file);
		
		KBModel = new KnowledgeBase();
		KBModel.setName(file.getName().substring(0, file.getName().lastIndexOf(".")));
		
			OWLClass topclass = factory.getOWLClass(IRI.create(SemSimKBConstants.SEMSIM_NAMESPACE + "SemSim_component"));
			if(!ont.getClassesInSignature().contains(topclass)){
				KBModel.addError("Source file does not appear to be a valid Knowledge Base");
				return KBModel;
			}
			//Get models
			for (String modelind : KBOWLFactory.getIndividualsInTreeAsStrings(ont, SemSimKBConstants.KB_COMPUTATIONAL_BIOMODEL_URI.toString())){
				CompBioModel cbm= new CompBioModel(KBOWLFactory.getURIdecodedFragmentFromIRI(modelind));
				cbm.setURI(URI.create(modelind));
				KBModel.addComponent(cbm);
				}
			//Get Reference Physical Entities
			for (String rpes : KBOWLFactory.getIndividualsInTreeAsStrings(ont,  SemSimKBConstants.REFERENCE_PHYSICAL_ENTITY_CLASS_URI.toString())) {
				getPhysicalEntityFromURI(ont, URI.create(rpes));
			}
			
			//Get Physical Properties
			for (String pps : KBOWLFactory.getIndividualsInTreeAsStrings(ont,  SemSimKBConstants.PHYSICAL_PROPERTY_CLASS_URI.toString())) {
				getPhysicalPropertyfromURI(ont, URI.create(pps));
			}
			
			//Get Composite Physical Entities
			for (String cpes : KBOWLFactory.getIndividualsInTreeAsStrings(ont,  SemSimKBConstants.KB_PHYSICAL_ENTITY_CLASS_URI.toString())) {
				getCompositeEntityFromURI(ont, URI.create(cpes));
			}

				
		return KBModel;
	}
	
	private void getPhysicalPropertyfromURI(OWLOntology ont, URI ppuri) throws OWLException {
		String label = KBOWLFactory.getRDFLabels(ont, factory.getOWLNamedIndividual(IRI.create(ppuri)))[0];
		String refersto = KBOWLFactory.getFunctionalIndDatatypeProperty(ont, ppuri.toString(), SemSimbase + "refersTo");
		
		PhysicalProperty pp= new PhysicalProperty();
		pp.setName(label);
		pp.setURI(ppuri);
		pp.addReferenceOntologyAnnotation(SemSimKBConstants.REFERS_TO_RELATION, URI.create(refersto), label);
		KBModel.addComponent(pp);
	}
	
	
	// Get the URI of the object of a triple that uses a structural relation as its predicate
	private DBCompositeEntity getCompositeEntityFromURI(OWLOntology ont, URI compind) throws OWLException{
		ArrayList<StructuralRelation> rels = new ArrayList<StructuralRelation>();
		String enttype = KBOWLFactory.getFunctionalIndDatatypeProperty(ont, compind.toString(), SemSimKBConstants.KB_ENTITY_COMPLEXITY_URI.toString());

		DBCompositeEntity DPE = new DBCompositeEntity(compind, SemSimKBConstants.KNOWLEDGE_BASE_ENTITY_TYPES.get(enttype));
		DPE.setName(KBOWLFactory.getURIdecodedFragmentFromIRI(compind.toString().replace("_", " ")));
		
		Set<String> models = KBOWLFactory.getIndObjectProperty(ont, compind.toString(), SemSimKBConstants.BQM_IS_DERIVED_FROM_URI.toString());
		for (String model : models) {
			DPE.addCompBioModel(KBModel.getModelbyURI(URI.create(model)));
		}
		
		URI firstent = URI.create(KBOWLFactory.getFunctionalIndObjectProperty(ont, compind.toString(), SemSimKBConstants.KB_HAS_SUBCOMPONENT_URI.toString()));		
		DPE.addComponent(getExistingCompositeComponent(ont, firstent));
		
		String partof = KBOWLFactory.getFunctionalIndObjectProperty(ont, compind.toString(), SemSimKBConstants.PART_OF_URI.toString());

		String nextind = null;
		if(!partof.equals("")){
			nextind = KBOWLFactory.getFunctionalIndObjectProperty(ont, compind.toString(), SemSimKBConstants.PART_OF_URI.toString());
			rels.add(SemSimKBConstants.PART_OF_RELATION);
		}
		else{
			nextind = KBOWLFactory.getFunctionalIndObjectProperty(ont, compind.toString(), SemSimKBConstants.CONTAINED_IN_URI.toString());
			rels.add(SemSimKBConstants.CONTAINED_IN_RELATION);
		}
		DPE.addComponent(getExistingCompositeComponent(ont, URI.create(nextind)));
		DPE.addRelation(rels.get(0));
		
		if(!URIandPMCmap.containsKey(URI.create(nextind))){
			URIandPMCmap.put(URI.create(nextind), DPE);
		}

		Set<String> pps = KBOWLFactory.getIndObjectProperty(ont, compind.toString(), SemSimKBConstants.HAS_PHYSICAL_PROPERTY_URI.toString());
		for (String pp : pps) {
			PhysicalProperty pptoadd = KBModel.getPropertybyURI(URI.create(pp));
			OWLIndividual ppind = factory.getOWLNamedIndividual(IRI.create(pp));
			OWLIndividual entind = factory.getOWLNamedIndividual(IRI.create(compind.toString()));
			OWLObjectProperty owlprop = factory.getOWLObjectProperty(IRI.create(SemSimKBConstants.HAS_PHYSICAL_PROPERTY_URI.toString()));
			
			OWLIndividualAxiom axiom = factory.getOWLObjectPropertyAssertionAxiom(owlprop, entind, ppind);
			
			OWLAnnotationProperty annprop = factory.getOWLAnnotationProperty(IRI.create(SemSimKBConstants.BQM_IS_DESCRIBED_BY_URI));
			for(OWLAxiom ax : ont.getAxioms(entind)){
				if(ax.equalsIgnoreAnnotations(axiom)){
					if(!ax.getAnnotations(annprop).isEmpty()){
						for (OWLAnnotation ann : ax.getAnnotations(annprop)) {
							URI moduri = URI.create(ann.getValue().toString());
							DPE.addProperty(pptoadd, moduri);
						}
					}
				}
			}

		}
		
		KBModel.addComponent(DPE);
		
		return DPE;
	} 
	
	private PhysicalModelComponent getExistingCompositeComponent(OWLOntology ont, URI entity) throws OWLException {
		PhysicalModelComponent pe = KBModel.getRefEntitybyURI(entity);
		if (pe==null) {
			pe = KBModel.getComponentbyURI(entity);
			if (pe==null) {
				pe = getCompositeEntityFromURI(ont, entity);
			}
		}
		return pe;
	}
	
	// Retrieve or generate a reference physical entity from its URI in the OWL-encoded SemSim model 
	private void getPhysicalEntityFromURI(OWLOntology ont, URI uri) throws OWLException{
		String label = KBOWLFactory.getRDFLabels(ont, factory.getOWLNamedIndividual(IRI.create(uri)))[0];

		String refersto = KBOWLFactory.getFunctionalIndDatatypeProperty(ont, uri.toString(), SemSimbase + "refersTo");
		ReferencePhysicalEntity rpe = new ReferencePhysicalEntity(URI.create(refersto), label);
		rpe.setURI(uri);
		
		rpe.addReferenceOntologyAnnotation(SemSimKBConstants.REFERS_TO_RELATION, URI.create(refersto), label);
		KBModel.addComponent(rpe);

	}
}
