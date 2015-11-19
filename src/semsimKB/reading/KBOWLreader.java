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
import semsimKB.model.SemSimTypes;
import semsimKB.model.kbbuffer.KBCompositeObject;
import semsimKB.model.kbbuffer.KnowledgeBase;
import semsimKB.model.physical.DBCompositeEntity;
import semsimKB.model.physical.PhysicalEntity;
import semsimKB.model.physical.PhysicalProperty;
import semsimKB.model.physical.ReferencePhysicalEntity;
import semsimKB.owl.KBOWLFactory;
import semsimKB.owl.SemSimOWLFactory;
import vprExplorer.buffer.ComponentStatus;

public class KBOWLreader {
	private KnowledgeBase KBModel = new KnowledgeBase();
	private OWLDataFactory factory;
	private HashMap<String, PhysicalEntity> peurimap = new HashMap<String, PhysicalEntity>();
	private HashMap<String, PhysicalProperty> ppurimap = new HashMap<String, PhysicalProperty>();
	private OWLOntology ont;
	
	public KnowledgeBase readFromFile(File file) throws OWLException, CloneNotSupportedException{
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		factory = manager.getOWLDataFactory();
		ont = manager.loadOntologyFromOntologyDocument(file);
		
			//Get models
			for (String modelind : KBOWLFactory.getIndividualsInTreeAsStrings(ont, SemSimTypes.KB_MODEL.getURIasString())){
				String label = KBOWLFactory.getRDFLabels(ont, factory.getOWLNamedIndividual(IRI.create(modelind)))[0];
				CompBioModel cbm= new CompBioModel(URI.create(modelind), label);
				cbm.setURI(URI.create(modelind));
				KBModel.addModel(cbm, ComponentStatus.EXACT_MATCH);
			}
			//Get Reference Physical Entities
			for (String rpe : KBOWLFactory.getIndividualsInTreeAsStrings(ont,  SemSimTypes.REFERENCE_PHYSICAL_ENTITY.getURIasString())) {
				getPhysicalEntityFromURI(rpe);
			}
			
			//Get Physical Properties
			for (String pps : KBOWLFactory.getIndividualsInTreeAsStrings(ont,  SemSimTypes.PHYSICAL_PROPERTY.getURIasString())) {
				getPhysicalPropertyfromURI(URI.create(pps));
			}
			
			//Get Composite Physical Entities
			for (String cpes : KBOWLFactory.getIndividualsInTreeAsStrings(ont,  SemSimTypes.KB_COMPOSITE_ENTITY.getURIasString())) {
				getCompositeEntityFromURI(URI.create(cpes));
			}
				
		return KBModel;
	}
	
	private void getPhysicalPropertyfromURI(URI ppuri) throws OWLException {
		String label = KBOWLFactory.getRDFLabels(ont, factory.getOWLNamedIndividual(IRI.create(ppuri)))[0];
		
		PhysicalProperty pp= new PhysicalProperty(label, ppuri);
		ppurimap.put(ppuri.toString(), pp);
		KBModel.addPhysicalProperty(pp, ComponentStatus.EXACT_MATCH);
	}
	
	// Retrieve or generate a reference physical entity from its URI in the OWL-encoded SemSim model 
	private void getPhysicalEntityFromURI(String uri) throws OWLException{
		String label = KBOWLFactory.getRDFLabels(ont, factory.getOWLNamedIndividual(IRI.create(uri)))[0];

		ReferencePhysicalEntity rpe = new ReferencePhysicalEntity(URI.create(uri), label);
		
		peurimap.put(uri, rpe);
		KBModel.addReferencePhysicalEntity(rpe, ComponentStatus.EXACT_MATCH);
	}
	
	// Get the URI of the object of a triple that uses a structural relation as its predicate
	private void getCompositeEntityFromURI(URI compind) throws OWLException{
		String label = SemSimOWLFactory.getRDFLabels(ont, factory.getOWLClass(IRI.create(compind)))[0];
		DBCompositeEntity dpe = new DBCompositeEntity(compind, label);
		
		setCompositeComponents(dpe);
		
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
		
		String secondent = KBOWLFactory.getFunctionalIndObjectProperty(ont, compuri.toString(), StructuralRelation.PART_OF_RELATION.getURIasString());

		StructuralRelation rel = StructuralRelation.PART_OF_RELATION;
		if(secondent.equals("")){
			secondent = KBOWLFactory.getFunctionalIndObjectProperty(ont, compuri.toString(), StructuralRelation.CONTAINED_IN_RELATION.getURIasString());
			if (!secondent.equals("")) {
				rel = StructuralRelation.CONTAINED_IN_RELATION;
			}
		}
		
		String firstent = KBOWLFactory.getFunctionalIndObjectProperty(ont, compuri.toString(), StructuralRelation.INDEX_ENTITY_RELATION.getURIasString());		
		
		if (!peurimap.containsKey(firstent)) getCompositeEntityFromURI(URI.create(firstent));
		if (!secondent.equals("")) {
			dpe.setRelation(rel);
			if (!peurimap.containsKey(secondent)) getCompositeEntityFromURI(URI.create(secondent));
			dpe.setComponents(Pair.of(peurimap.get(firstent), peurimap.get(secondent)));
		}
		else {
			dpe.setComponents(Pair.of(peurimap.get(firstent), null));
		}
		
	}
	
	private KBCompositeObject<DBCompositeEntity> makeCompositeObject(DBCompositeEntity dce) {
		ArrayList<ComponentStatus> pstat = new ArrayList<ComponentStatus>();
		
		int psize = dce.getPropertyList().size();
		for (int i=0; i<psize; i++) {
			pstat.add(ComponentStatus.EXACT_MATCH);
		}	
		

		return new KBCompositeObject<DBCompositeEntity>(dce, ComponentStatus.EXACT_MATCH, pstat);
	}
}
