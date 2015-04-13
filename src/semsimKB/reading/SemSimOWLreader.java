package semsimKB.reading;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import semsimKB.SemSimKBConstants;
import semsimKB.SemSimLibrary;
import semsimKB.annotation.Annotation;
import semsimKB.annotation.StructuralRelation;
import semsimKB.model.ModelLite;
import semsimKB.model.physical.CompositePhysicalEntity;
import semsimKB.model.physical.PhysicalEntity;
import semsimKB.model.physical.PhysicalProperty;
import semsimKB.model.physical.ReferencePhysicalEntity;
import semsimKB.owl.KBOWLFactory;
import semsimKB.owl.SemSimOWLFactory;

public class SemSimOWLreader {
	private ModelLite model = new ModelLite();
	private OWLOntology ont;
	private OWLDataFactory factory;
	private HashMap<String, ReferencePhysicalEntity> rpeurimap = new HashMap<String, ReferencePhysicalEntity>();
	private HashMap<String, PhysicalProperty> ppurimap = new HashMap<String, PhysicalProperty>();
	
	public ModelLite readFromFile(File file) throws OWLException, CloneNotSupportedException{
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		factory = manager.getOWLDataFactory();
		ont = manager.loadOntologyFromOntologyDocument(file);

		model.setName(file.getName().substring(0, file.getName().lastIndexOf(".")));
		model.setURI(URI.create(SemSimKBConstants.SEMSIM_NAMESPACE + model.getName()));
		
		OWLClass topclass = factory.getOWLClass(IRI.create(SemSimKBConstants.SEMSIM_NAMESPACE + "SemSim_component"));
		if(!ont.getClassesInSignature().contains(topclass)){
			model.addError("Source file does not appear to be a valid KB model");
			return model;
		}

		collectModelAnnotations();
		collectPhysicalProperties();
		collectReferenceEntities();
		collectCompositeEntities();
		
		return model;
	}
		
	private void collectModelAnnotations() {
		// Get model-level annotations
		Set<OWLAnnotation> anns = ont.getAnnotations();
		Set<OWLAnnotation> annstoremove = new HashSet<OWLAnnotation>();
		for (OWLAnnotation named : anns) {
			if (named.getProperty().getIRI().equals(SemSimLibrary.SEMSIM_VERSION_IRI)) {
				model.setSemsimversion(((OWLLiteral)named.getValue()).getLiteral());
				annstoremove.add(named);
			};
			if (named.getProperty().getIRI().equals(ModelLite.LEGACY_CODE_LOCATION_IRI)) {
				model.setSourcefilelocation(((OWLLiteral)named.getValue()).getLiteral());
				annstoremove.add(named);
			};
		}
		model.getCurationalMetadata().setCurationalMetadata(anns, annstoremove);
		anns.removeAll(annstoremove);
		
		//Add remaining annotations
		for(OWLAnnotation ann : anns){
			URI propertyuri = ann.getProperty().getIRI().toURI();
			if(SemSimKBConstants.URIS_AND_SEMSIM_RELATIONS.containsKey(propertyuri)){
				if(ann.getValue() instanceof OWLLiteral){
					OWLLiteral val = (OWLLiteral) ann.getValue();
					
					model.addAnnotation(new Annotation(SemSimKBConstants.getRelationFromURI(propertyuri), val.getLiteral()));
				}
			}
		}
	}
	
	private void collectPhysicalProperties() throws OWLException {
		for (String pps : KBOWLFactory.getAllSubclasses(ont,  SemSimKBConstants.PHYSICAL_PROPERTY_CLASS_URI.toString(),false)) {
			String label = SemSimOWLFactory.getRDFLabels(ont, factory.getOWLClass(IRI.create(pps)))[0];
			
			model.addPhysicalProperty(new PhysicalProperty(label, URI.create(pps)));
		}
	}
	
	private void collectReferenceEntities() throws OWLException {
		for (String rperef : KBOWLFactory.getAllSubclasses(ont,  SemSimKBConstants.REFERENCE_PHYSICAL_ENTITY_CLASS_URI.toString(), false)) {
			String label = SemSimOWLFactory.getRDFLabels(ont, factory.getOWLClass(IRI.create(rperef)))[0];
			ReferencePhysicalEntity rpe = new ReferencePhysicalEntity(URI.create(rperef), label);
			rpeurimap.put(rperef, rpe);
			model.addReferencePhysicalEntity(rpe);
		}
	}
	
	private void collectCompositeEntities() throws OWLException {
		for (String cperef : KBOWLFactory.getIndividualsInTreeAsStrings(ont,  SemSimKBConstants.COMPOSITE_PHYSICAL_ENTITY_CLASS_URI.toString())) {		
			String ind = SemSimOWLFactory.getFunctionalIndObjectProperty(ont, cperef.toString(), SemSimKBConstants.HAS_INDEX_ENTITY_URI.toString());
			ArrayList<PhysicalEntity> rpes = new ArrayList<PhysicalEntity>();
			ArrayList<StructuralRelation> rels = new ArrayList<StructuralRelation>();
			Set<String> pps = SemSimOWLFactory.getIndObjectProperty(ont, ind, SemSimKBConstants.HAS_PHYSICAL_PROPERTY_URI.toString());
			
			ReferencePhysicalEntity rpe = getClassofIndividual(ind);
			if (rpe == null) continue;
			rpes.add(rpe);
			
			while (true) {
				String nextind = SemSimOWLFactory.getFunctionalIndObjectProperty(ont, ind.toString(), SemSimKBConstants.PART_OF_URI.toString());
				StructuralRelation rel = SemSimKBConstants.PART_OF_RELATION;
				if (nextind=="") {
					nextind = SemSimOWLFactory.getFunctionalIndObjectProperty(ont, ind.toString(), SemSimKBConstants.CONTAINED_IN_URI.toString());
					if (nextind=="") break;
					rel = SemSimKBConstants.CONTAINED_IN_RELATION;
				}
				rpe = getClassofIndividual(nextind);
				if (rpe == null) break;
				rpes.add(rpe);
				rels.add(rel);
				ind = nextind;
			}
			if (rpe==null) continue; 
			CompositePhysicalEntity newcpe = model.addCompositePhysicalEntity(rpes, rels);
			for (String pp : pps) {
				newcpe.addPhysicalProperty(getClassofProperty(pp));
			}
			
		}
	}
	
	private ReferencePhysicalEntity getClassofIndividual(String ind) throws OWLException {
		String indclass = SemSimOWLFactory.getFunctionalIndDatatypeProperty(ont, ind, SemSimKBConstants.REFERS_TO_URI.toString());
		return rpeurimap.get(indclass);
	}
	
	private PhysicalProperty getClassofProperty(String ind) throws OWLException {
		String indclass = SemSimOWLFactory.getFunctionalIndDatatypeProperty(ont, ind, SemSimKBConstants.REFERS_TO_URI.toString());
		return ppurimap.get(indclass);
	}
}
