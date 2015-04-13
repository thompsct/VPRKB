package semsimKB.reading;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import semsimKB.SemSimKBConstants;
import semsimKB.annotation.StructuralRelation;
import semsimKB.model.CompBioModel;
import semsimKB.model.kbbuffer.KBCompositeObject;
import semsimKB.model.kbbuffer.KnowledgeBase;
import semsimKB.model.physical.DBCompositeEntity;
import semsimKB.model.physical.PhysicalEntity;
import semsimKB.model.physical.PhysicalProperty;
import semsimKB.model.physical.ReferencePhysicalEntity;
import semsimKB.owl.KBOWLFactory;
import vprExplorer.buffer.ComponentStatus;

public class KBOWLreader {
	private KnowledgeBase KBModel = new KnowledgeBase();
	private OWLDataFactory factory;
	private String SemSimbase = SemSimKBConstants.SEMSIM_NAMESPACE;
	private HashMap<String, PhysicalEntity> peurimap = new HashMap<String, PhysicalEntity>();
	private OWLOntology ont;
	
	public KnowledgeBase readFromFile(File file) throws OWLException, CloneNotSupportedException{
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		factory = manager.getOWLDataFactory();
		ont = manager.loadOntologyFromOntologyDocument(file);
		
			//Get models
			for (String modelind : KBOWLFactory.getIndividualsInTreeAsStrings(ont, SemSimKBConstants.KB_MODEL_URI.toString())){
				String label = KBOWLFactory.getRDFLabels(ont, factory.getOWLNamedIndividual(IRI.create(modelind)))[0];
				CompBioModel cbm= new CompBioModel(URI.create(modelind), label);
				cbm.setURI(URI.create(modelind));
				KBModel.addModel(cbm, ComponentStatus.EXACT_MATCH);
			}
			//Get Reference Physical Entities
			for (String rpes : KBOWLFactory.getIndividualsInTreeAsStrings(ont,  SemSimKBConstants.REFERENCE_PHYSICAL_ENTITY_CLASS_URI.toString())) {
				getPhysicalEntityFromURI(URI.create(rpes));
			}
			
			//Get Physical Properties
			for (String pps : KBOWLFactory.getIndividualsInTreeAsStrings(ont,  SemSimKBConstants.PHYSICAL_PROPERTY_CLASS_URI.toString())) {
				getPhysicalPropertyfromURI(URI.create(pps));
			}
			
			//Get Composite Physical Entities
			for (String cpes : KBOWLFactory.getIndividualsInTreeAsStrings(ont,  SemSimKBConstants.KB_COMPOSITE_CLASS_URI.toString())) {
				getCompositeEntityFromURI(URI.create(cpes));
			}
			for (DBCompositeEntity dce : KBModel.getComposites()) {
				this.setCompositeComponents(dce);
			}
				
		return KBModel;
	}
	
	private void getPhysicalPropertyfromURI(URI ppuri) throws OWLException {
		String label = KBOWLFactory.getRDFLabels(ont, factory.getOWLNamedIndividual(IRI.create(ppuri)))[0];
		String refersto = KBOWLFactory.getFunctionalIndDatatypeProperty(ont, ppuri.toString(), SemSimbase + "refersTo");
		
		PhysicalProperty pp= new PhysicalProperty();
		pp.setName(label);
		pp.setURI(ppuri);
		pp.addReferenceOntologyAnnotation(SemSimKBConstants.REFERS_TO_RELATION, URI.create(refersto), label);
		KBModel.addPhysicalProperty(pp, ComponentStatus.EXACT_MATCH);
	}
	
	// Get the URI of the object of a triple that uses a structural relation as its predicate
	private void getCompositeEntityFromURI(URI compind) throws OWLException{
		DBCompositeEntity dpe = new DBCompositeEntity(compind);
		dpe.setName(KBOWLFactory.getURIdecodedFragmentFromIRI(compind.toString().replace("_", " ")));
		
		Set<String> models = KBOWLFactory.getIndObjectProperty(ont, compind.toString(), SemSimKBConstants.BQM_IS_DERIVED_FROM_URI.toString());
		for (String model : models) {
			dpe.addCompBioModel(KBModel.getModelbyURI(URI.create(model)));
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
							CompBioModel mod = KBModel.getModelbyURI(URI.create(ann.getValue().toString()));
							dpe.addProperty(pptoadd, mod);
						}
					}
				}
			}
		}
		peurimap.put(dpe.getURI().toString(), dpe);
		KBCompositeObject<DBCompositeEntity> object = makeCompositeObject(dpe);
		
		KBModel.addComposite(object);

	} 
	
	private void setCompositeComponents(DBCompositeEntity dpe) throws OWLException {
		URI compuri = dpe.getURI();
		
		String partof = KBOWLFactory.getFunctionalIndObjectProperty(ont, compuri.toString(), SemSimKBConstants.PART_OF_URI.toString());

		String secondent = null;
		StructuralRelation rel = SemSimKBConstants.PART_OF_RELATION;
		if(partof.equals("")){
			secondent = KBOWLFactory.getFunctionalIndObjectProperty(ont, compuri.toString(), SemSimKBConstants.CONTAINED_IN_URI.toString());
			rel = SemSimKBConstants.CONTAINED_IN_RELATION;
		}
		dpe.setRelation(rel);
		URI firstent = URI.create(KBOWLFactory.getFunctionalIndObjectProperty(ont, compuri.toString(), SemSimKBConstants.KB_HAS_SUBCOMPONENT_URI.toString()));		

		dpe.setComponents(Pair.of(peurimap.get(firstent), peurimap.get(secondent)));
	}
	
	// Retrieve or generate a reference physical entity from its URI in the OWL-encoded SemSim model 
	private void getPhysicalEntityFromURI(URI uri) throws OWLException{
		String label = KBOWLFactory.getRDFLabels(ont, factory.getOWLNamedIndividual(IRI.create(uri)))[0];

		String refersto = KBOWLFactory.getFunctionalIndDatatypeProperty(ont, uri.toString(), SemSimbase + "refersTo");
		ReferencePhysicalEntity rpe = new ReferencePhysicalEntity(URI.create(refersto), label);
		rpe.setURI(uri);
		
		rpe.addReferenceOntologyAnnotation(SemSimKBConstants.REFERS_TO_RELATION, URI.create(refersto), label);
		peurimap.put(refersto, rpe);
		KBModel.addReferencePhysicalEntity(rpe, ComponentStatus.EXACT_MATCH);

	}
	
	private KBCompositeObject<DBCompositeEntity> makeCompositeObject(DBCompositeEntity dce) {
		ArrayList<ComponentStatus> pstat = new ArrayList<ComponentStatus>();
		ArrayList<ArrayList<ComponentStatus>> pmstat = new ArrayList<ArrayList<ComponentStatus>>();
		
		int psize = dce.getPropertyList().size();
		for (int i=0; i<psize; i++) {		
			pstat.add(ComponentStatus.EXACT_MATCH);
			ArrayList<ComponentStatus> mstat = new ArrayList<ComponentStatus>();
			int msize = dce.getPropertyModelList(psize).size();
			for (int j=0; j<msize; j++) {
				mstat.add(ComponentStatus.EXACT_MATCH);
			}
			pmstat.add(mstat);
		}	
		

		return new KBCompositeObject<DBCompositeEntity>(dce, ComponentStatus.EXACT_MATCH, pstat, pmstat);
	}
}
