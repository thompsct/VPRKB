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

import semsimKB.SemSimLibrary;
import semsimKB.annotation.Annotation;
import semsimKB.definitions.RDFNamespace;
import semsimKB.definitions.SemSimRelation;
import semsimKB.definitions.SemSimTypes;
import semsimKB.definitions.StructuralRelation;
import semsimKB.definitions.SemSimRelation.KBRelations;
import semsimKB.model.ModelLite;
import semsimKB.model.physical.CompositePhysicalEntity;
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
	private URI physicaldefinitionURI;
	
	public ModelLite readFromFile(File file) throws OWLException, CloneNotSupportedException{
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		factory = manager.getOWLDataFactory();
		ont = manager.loadOntologyFromOntologyDocument(file);

		setPhysicalDefinitionURI();
		
		model.setName(file.getName().substring(0, file.getName().lastIndexOf(".")));
		model.setURI(URI.create(RDFNamespace.SEMSIM_NAMESPACE.getNamespace() + model.getName()));
		
		OWLClass topclass = factory.getOWLClass(IRI.create(RDFNamespace.SEMSIM_NAMESPACE.getNamespace() + "SemSim_component"));
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
	
	private void setPhysicalDefinitionURI(){
		
		if(ont.containsDataPropertyInSignature(IRI.create(RDFNamespace.SEMSIM_NAMESPACE.getNamespace() + "refersTo"))){
			physicaldefinitionURI = URI.create(RDFNamespace.SEMSIM_NAMESPACE.getNamespace() + "refersTo");
		}
		else if(ont.containsDataPropertyInSignature(KBRelations.HAS_PHYSICAL_DEFINITION.getIRI())){
			physicaldefinitionURI = KBRelations.HAS_PHYSICAL_DEFINITION.getURI();
		}
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
			KBRelations rel = SemSimRelation.getRelationFromURI(propertyuri);
			if(rel != KBRelations.UNKNOWN){
				if(ann.getValue() instanceof OWLLiteral){
					OWLLiteral val = (OWLLiteral) ann.getValue();
					
					model.addAnnotation(new Annotation(rel, val.getLiteral()));
				}
			}
		}
	}
	
	private void collectPhysicalProperties() throws OWLException {
		for (String pps : KBOWLFactory.getAllSubclasses(ont,  SemSimTypes.PHYSICAL_PROPERTY.getURIasString(),false)) {
			String label = SemSimOWLFactory.getRDFLabels(ont, factory.getOWLClass(IRI.create(pps)))[0];
			PhysicalProperty pp = new PhysicalProperty(label, URI.create(pps));
			model.addPhysicalProperty(pp);
			ppurimap.put(pps, pp);
		}
	}
	
	private void collectReferenceEntities() throws OWLException {
		for (String rperef : KBOWLFactory.getAllSubclasses(ont,  SemSimTypes.REFERENCE_PHYSICAL_ENTITY.getURIasString(), false)) {
			String label = SemSimOWLFactory.getRDFLabels(ont, factory.getOWLClass(IRI.create(rperef)))[0];
			ReferencePhysicalEntity rpe = new ReferencePhysicalEntity(URI.create(rperef), label);
			rpeurimap.put(rperef, rpe);
			model.addReferencePhysicalEntity(rpe);
		}
	}
	
	private void collectCompositeEntities() throws OWLException {
		for (String cperef : KBOWLFactory.getIndividualsInTreeAsStrings(ont,  SemSimTypes.COMPOSITE_PHYSICAL_ENTITY.getURIasString())) {		
			String ind = SemSimOWLFactory.getFunctionalIndObjectProperty(ont, cperef.toString(), StructuralRelation.INDEX_ENTITY_RELATION.getURIasString());
			ArrayList<ReferencePhysicalEntity> rpes = new ArrayList<ReferencePhysicalEntity>();
			ArrayList<StructuralRelation> rels = new ArrayList<StructuralRelation>();
			Set<String> pps = SemSimOWLFactory.getIndObjectProperty(ont, ind, KBRelations.HAS_PHYSICAL_PROPERTY.getURIasString());
			
			ReferencePhysicalEntity rpe = getClassofIndividual(ind);
			if (rpe == null) continue;
			rpes.add(rpe);
			
			while (true) {
				StructuralRelation rel = StructuralRelation.PART_OF_RELATION;
				String nextind = SemSimOWLFactory.getFunctionalIndObjectProperty(ont, ind.toString(), rel.getURIasString());				
				if (nextind=="") {
					rel = StructuralRelation.CONTAINED_IN_RELATION;
					nextind = SemSimOWLFactory.getFunctionalIndObjectProperty(ont, ind.toString(), rel.getURIasString());
					if (nextind=="") break;
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
		String indclass = SemSimOWLFactory.getFunctionalIndDatatypeProperty(ont, ind, physicaldefinitionURI.toString());
		return rpeurimap.get(indclass);
	}
	
	private PhysicalProperty getClassofProperty(String ind) throws OWLException {
		String indclass = SemSimOWLFactory.getFunctionalIndDatatypeProperty(ont, ind, physicaldefinitionURI.toString());
		return ppurimap.get(indclass);
	}
}
