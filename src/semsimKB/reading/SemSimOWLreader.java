package semsimKB.reading;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import semsimKB.SemSimKBConstants;
import semsimKB.annotation.Annotation;
import semsimKB.annotation.StructuralRelation;
import semsimKB.model.ModelLite;
import semsimKB.model.physical.CompositePhysicalEntity;
import semsimKB.model.physical.PhysicalEntity;
import semsimKB.model.physical.PhysicalModelComponent;
import semsimKB.model.physical.PhysicalProcess;
import semsimKB.model.physical.PhysicalProperty;
import semsimKB.owl.SemSimOWLFactory;

public class SemSimOWLreader {
	private ModelLite KBModel = new ModelLite();
	private OWLOntology ont;
	private OWLDataFactory factory;
	private Map<URI, PhysicalModelComponent> URIandPMCmap = new HashMap<URI, PhysicalModelComponent>();	
	private Map<URI, PhysicalProperty> ppmap = new HashMap<URI, PhysicalProperty>();
	
	public ModelLite readFromFile(File file) throws OWLException, CloneNotSupportedException{
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		factory = manager.getOWLDataFactory();
		ont = manager.loadOntologyFromOntologyDocument(file);

		KBModel.setName(file.getName().substring(0, file.getName().lastIndexOf(".")));
		KBModel.setURI(URI.create(SemSimKBConstants.SEMSIM_NAMESPACE + KBModel.getName()));
		
		OWLClass topclass = factory.getOWLClass(IRI.create(SemSimKBConstants.SEMSIM_NAMESPACE + "SemSim_component"));
		if(!ont.getClassesInSignature().contains(topclass)){
			KBModel.addError("Source file does not appear to be a valid SemSim model");
			return KBModel;
		}
		// Get model-level annotations
		for(OWLAnnotation ann : ont.getAnnotations()){
			URI propertyuri = ann.getProperty().getIRI().toURI();
			
			if(SemSimKBConstants.URIS_AND_SEMSIM_RELATIONS.containsKey(propertyuri)){
				if(ann.getValue() instanceof OWLLiteral){
					
					OWLLiteral val = (OWLLiteral) ann.getValue();
					KBModel.addAnnotation(new Annotation(SemSimKBConstants.getRelationFromURI(propertyuri), val.getLiteral()));
				}
				else if((ann.getValue() instanceof IRI) || (ann.getValue() instanceof OWLAnonymousIndividual)) continue;
		}

		
		for(String dsind : SemSimOWLFactory.getIndividualsInTreeAsStrings(ont, SemSimKBConstants.DATA_STRUCTURE_CLASS_URI.toString())){
			String propind = SemSimOWLFactory.getFunctionalIndObjectProperty(ont, dsind, SemSimKBConstants.IS_COMPUTATIONAL_COMPONENT_FOR_URI.toString());
			if(!propind.equals("")){
				URI referstoval = URI.create(SemSimOWLFactory.getFunctionalIndDatatypeProperty(ont, propind, SemSimKBConstants.REFERS_TO_URI.toString()));
				PhysicalProperty pp = new PhysicalProperty();
				if(!referstoval.equals("")) {
				 	if (!ppmap.containsKey(referstoval)) {
						// If the property is annotated, store annotation
						
							String label = SemSimOWLFactory.getRDFLabels(ont, factory.getOWLNamedIndividual(IRI.create(propind)))[0];
							pp.addReferenceOntologyAnnotation(SemSimKBConstants.REFERS_TO_RELATION, referstoval, label);
							pp.setName(label);
							pp.setURI(referstoval);
							ppmap.put(referstoval, pp);
						}
				 	else pp = ppmap.get(referstoval);
							
						// Set the connection between the physical property and what it's a property of
						String propofind = SemSimOWLFactory.getFunctionalIndObjectProperty(ont, propind, SemSimKBConstants.PHYSICAL_PROPERTY_OF_URI.toString());
			
						// If it's a property of a physical entity, get the entity. Follow composite, if present
						if(SemSimOWLFactory.indExistsInTree(propofind, SemSimKBConstants.PHYSICAL_ENTITY_CLASS_URI.toString(), ont)){
							PhysicalEntity ent = getPhysicalEntityFromURI(ont, URI.create(propofind));					
							ent.addPhysicalProperty(pp);
					}
				}
			}
			
		}
		
		// Deal with physical processes
		for(String processind : SemSimOWLFactory.getIndividualsInTreeAsStrings(ont, SemSimKBConstants.PHYSICAL_PROCESS_CLASS_URI.toString())){
			Set<String> propinds = SemSimOWLFactory.getIndObjectProperty(ont, processind, SemSimKBConstants.HAS_PHYSICAL_PROPERTY_URI.toString());
			URI procinduri =  URI.create(processind);
			
			for(String propind : propinds){
				PhysicalProcess pproc = null;
				
				String label = SemSimOWLFactory.getRDFLabels(ont, factory.getOWLNamedIndividual(IRI.create(processind)))[0];
				String description = null;
				if(SemSimOWLFactory.getRDFComments(ont, processind)!=null)
					description = SemSimOWLFactory.getRDFComments(ont, processind)[0];
				
				if(isReferencedIndividual(ont, procinduri)){
					String refersto = SemSimOWLFactory.getFunctionalIndDatatypeProperty(ont, processind, SemSimKBConstants.REFERS_TO_URI.toString());
					pproc = KBModel.addReferencePhysicalProcess(URI.create(refersto), label); // if equivalent process is available, will return it
					
					String referstoval = SemSimOWLFactory.getFunctionalIndDatatypeProperty(ont, propind, SemSimKBConstants.REFERS_TO_URI.toString());
					String propertylabel = SemSimOWLFactory.getRDFLabels(ont, factory.getOWLNamedIndividual(IRI.create(propind)))[0];
					
					// Create a new physical property associated with the process and data structure
					PhysicalProperty pp = new PhysicalProperty();
					pp.addReferenceOntologyAnnotation(SemSimKBConstants.REFERS_TO_RELATION, URI.create(referstoval), propertylabel);
					String uri = SemSimKBConstants.SEMSIM_NAMESPACE.subSequence(0, SemSimKBConstants.SEMSIM_NAMESPACE.length()-1) + referstoval.substring(referstoval.lastIndexOf('/'), referstoval.length());
					pp.setURI(URI.create(uri));
					pproc.addPhysicalProperty(pp);
				}
				else  pproc = KBModel.addCustomPhysicalProcess(label, description);  // if equivalent process is available, will return it
				pproc.setURI(procinduri);
				
				// If needed, create new process object in model and store in uri-physmodelcomp map
			    if(!URIandPMCmap.containsKey(procinduri)){
					Set<String> srcs = SemSimOWLFactory.getIndObjectProperty(ont, processind, SemSimKBConstants.HAS_SOURCE_URI.toString());
					Set<String> sinks = SemSimOWLFactory.getIndObjectProperty(ont, processind, SemSimKBConstants.HAS_SINK_URI.toString());
					Set<String> mediators = SemSimOWLFactory.getIndObjectProperty(ont, processind, SemSimKBConstants.HAS_MEDIATOR_URI.toString());
		
					// Enter source information
					for(String src : srcs){
						PhysicalEntity srcent = getPhysicalEntityFromURI(ont, URI.create(src));
						double m = getMultiplierForProcessParticipant(ont, processind, 
								SemSimKBConstants.HAS_SOURCE_URI.toString(), src);
						pproc.addSource(srcent, m);
					}
					// Enter sink info
					for(String sink : sinks){
						PhysicalEntity sinkent = getPhysicalEntityFromURI(ont, URI.create(sink));
						double m = getMultiplierForProcessParticipant(ont, processind, 
								SemSimKBConstants.HAS_SINK_URI.toString(), sink);
						pproc.addSink(sinkent, m);
					}
					// Enter mediator info
					for(String med : mediators){
						PhysicalEntity medent = getPhysicalEntityFromURI(ont, URI.create(med));
						double m = getMultiplierForProcessParticipant(ont, processind, 
								SemSimKBConstants.HAS_MEDIATOR_URI.toString(), med);
						pproc.addMediator(medent, m);
					}
					URIandPMCmap.put(procinduri, pproc);
				}
			}
		}
		
		// Get additional annotations on custom terms
		// Make sure to get reference classes that are there to define custom classes
		URI[] customclasses = new URI[]{SemSimKBConstants.CUSTOM_PHYSICAL_ENTITY_CLASS_URI, SemSimKBConstants.CUSTOM_PHYSICAL_PROCESS_CLASS_URI};
		
		// For the two custom physical model classes...
		for(URI customclassuri : customclasses){
			
			// For each custom term in them...
			for(String custstring : SemSimOWLFactory.getIndividualsAsStrings(ont, customclassuri.toString())){
				OWLNamedIndividual custind = factory.getOWLNamedIndividual(IRI.create(custstring));
				
				// For each super class that is not the custom physical component class itself...
				for(OWLClassExpression supercls : custind.asOWLNamedIndividual().getTypes(ont)){
					URI superclsuri = supercls.asOWLClass().getIRI().toURI();
					if(!superclsuri.toString().equals(customclassuri.toString())){
						String label = SemSimOWLFactory.getRDFLabels(ont, supercls.asOWLClass())[0];
						
						// If the reference term hasn't been added yet, add it
						if(!URIandPMCmap.containsKey(supercls.asOWLClass().getIRI().toURI())){
							PhysicalModelComponent refpmc = null;
							if(customclassuri==SemSimKBConstants.CUSTOM_PHYSICAL_PROCESS_CLASS_URI)
								refpmc = KBModel.addReferencePhysicalProcess(superclsuri, label);
							if(customclassuri==SemSimKBConstants.CUSTOM_PHYSICAL_ENTITY_CLASS_URI)
								refpmc = KBModel.addReferencePhysicalEntity(superclsuri, label);
							URIandPMCmap.put(superclsuri, refpmc);

						}
						
						// Add isVersionOf annotation
						PhysicalModelComponent pmc = null;
						if(customclassuri==SemSimKBConstants.CUSTOM_PHYSICAL_PROCESS_CLASS_URI)
							pmc = KBModel.getCustomPhysicalProcessByName(SemSimOWLFactory.getRDFLabels(ont, custind)[0]);
						if(customclassuri==SemSimKBConstants.CUSTOM_PHYSICAL_ENTITY_CLASS_URI)
							pmc = KBModel.getCustomPhysicalEntityByName(SemSimOWLFactory.getRDFLabels(ont, custind)[0]);
						if(pmc!=null){
							pmc.addReferenceOntologyAnnotation(SemSimKBConstants.BQB_IS_VERSION_OF_RELATION, superclsuri, label);
						}
						else KBModel.addError("Attempt to apply reference ontology annotation (BQB:isVersionOf) to " + custstring + " failed. Could not find individual in set of processed physical model components");
					}
				}
			}
		}
		}
			Set<PhysicalProperty> pps = new HashSet<PhysicalProperty>(ppmap.values());
			KBModel.setPhysicalProperties(pps);

		return KBModel;
	}
	
	
	// Get the URI of the object of a triple that uses a structural relation as its predicate
	private CompositePhysicalEntity getURIofObjectofPhysicalEntityStructuralRelation(OWLOntology ont, CompositePhysicalEntity cpe, 
			URI startind) throws OWLException{
		String partof = SemSimOWLFactory.getFunctionalIndObjectProperty(ont, startind.toString(), SemSimKBConstants.PART_OF_URI.toString());
		String containedin = SemSimOWLFactory.getFunctionalIndObjectProperty(ont, startind.toString(), SemSimKBConstants.CONTAINED_IN_URI.toString());

		if(!partof.equals("") || !containedin.equals("")){
			String nextind = null;
			if(!partof.equals("")){
				nextind = SemSimOWLFactory.getFunctionalIndObjectProperty(ont, startind.toString(), SemSimKBConstants.PART_OF_URI.toString());
				cpe.getArrayListOfStructuralRelations().add(SemSimKBConstants.PART_OF_RELATION);
			}
			else{
				nextind = SemSimOWLFactory.getFunctionalIndObjectProperty(ont, startind.toString(), SemSimKBConstants.CONTAINED_IN_URI.toString());
				cpe.getArrayListOfStructuralRelations().add(SemSimKBConstants.CONTAINED_IN_RELATION);
			}
			String label = SemSimOWLFactory.getRDFLabels(ont, factory.getOWLNamedIndividual(IRI.create(nextind)))[0];
			URI nextinduri = URI.create(nextind);
			PhysicalEntity pmc = null;
			
			if(isReferencedIndividual(ont, nextinduri)){
				URI refuri = URI.create(SemSimOWLFactory.getFunctionalIndDatatypeProperty(ont, nextind, SemSimKBConstants.REFERS_TO_URI.toString()));
				if(!refuri.toString().startsWith("http://www.bhi.washington.edu/SemSim/")){  // Account for older models that used refersTo pointers to custom annotations
					pmc = KBModel.addReferencePhysicalEntity(refuri, label);
					cpe.getArrayListOfEntities().add(pmc);	
				}

			}
			
			cpe = getURIofObjectofPhysicalEntityStructuralRelation(ont, cpe, nextinduri);
			String uri = SemSimKBConstants.SEMSIM_NAMESPACE.subSequence(0, SemSimKBConstants.SEMSIM_NAMESPACE.length()-1) + nextind.substring(nextind.lastIndexOf('#'), nextind.length());
					pmc.setURI(URI.create(uri));
			if(!URIandPMCmap.containsKey(nextinduri)){
				URIandPMCmap.put(nextinduri, pmc);
			}
		}
		return cpe;
	}
	
	// Determine if the individual is annotated against a reference ontology term
	private Boolean isReferencedIndividual(OWLOntology ont, URI induri) throws OWLException{
		return !SemSimOWLFactory.getFunctionalIndDatatypeProperty(ont, induri.toString(), SemSimKBConstants.REFERS_TO_URI.toString()).equals("");
	}
	
	// Retrieve or generate a physical entity from its URI in the OWL-encoded SemSim model 
	private PhysicalEntity getPhysicalEntityFromURI(OWLOntology ont, URI uri) throws OWLException{
		PhysicalEntity ent = null;
		String label = SemSimOWLFactory.getRDFLabels(ont, factory.getOWLNamedIndividual(IRI.create(uri)))[0];
		
		if(URIandPMCmap.containsKey(uri))
			ent = (PhysicalEntity) URIandPMCmap.get(uri);
		else{
			if(isReferencedIndividual(ont, uri)){
				String refersto = SemSimOWLFactory.getFunctionalIndDatatypeProperty(ont, uri.toString(), SemSimKBConstants.REFERS_TO_URI.toString());
				ent = KBModel.addReferencePhysicalEntity(URI.create(refersto), label);
				
				String curi = SemSimKBConstants.SEMSIM_NAMESPACE.subSequence(0, SemSimKBConstants.SEMSIM_NAMESPACE.length()) + refersto.substring(refersto.lastIndexOf('/')+1, refersto.length());
				ent.setURI(URI.create(curi));
			}
			else
				return null; //ignore custom entities and any composites containing them
			
			// If it's a part of a composite physical entity
			if(!SemSimOWLFactory.getFunctionalIndObjectProperty(ont, uri.toString(), SemSimKBConstants.PART_OF_URI.toString()).equals("")
					|| !SemSimOWLFactory.getFunctionalIndObjectProperty(ont, uri.toString(), SemSimKBConstants.CONTAINED_IN_URI.toString()).equals("")){
				ArrayList<PhysicalEntity> ents = new ArrayList<PhysicalEntity>();
				ents.add(ent);
				ArrayList<StructuralRelation> rels = new ArrayList<StructuralRelation>();
				CompositePhysicalEntity cent = new CompositePhysicalEntity(ents, rels);
				cent = getURIofObjectofPhysicalEntityStructuralRelation(ont, cent, uri);
				ent = KBModel.addCompositePhysicalEntity(cent.getArrayListOfEntities(), cent.getArrayListOfStructuralRelations());
				
				String curi = SemSimKBConstants.SEMSIM_NAMESPACE.subSequence(0, SemSimKBConstants.SEMSIM_NAMESPACE.length()) + cent.getName();
				ent.setURI(URI.create(curi.replace(" ", "_")));

			}
			URIandPMCmap.put(uri, ent);
		}
		return ent;
	}
	
	// Get the multiplier for a process participant
	private double getMultiplierForProcessParticipant(OWLOntology ont, String process, String prop, String ent){
		double val = 1.0;
		OWLIndividual procind = factory.getOWLNamedIndividual(IRI.create(process));
		OWLIndividual entind = factory.getOWLNamedIndividual(IRI.create(ent));
		OWLObjectProperty owlprop = factory.getOWLObjectProperty(IRI.create(prop));
		OWLAxiom axiom = factory.getOWLObjectPropertyAssertionAxiom(owlprop, procind, entind);
		
		OWLAnnotationProperty annprop = factory.getOWLAnnotationProperty(IRI.create(SemSimKBConstants.HAS_MULTIPLIER_URI));
		for(OWLAxiom ax : ont.getAxioms(procind)){
			if(ax.equalsIgnoreAnnotations(axiom)){
				if(!ax.getAnnotations(annprop).isEmpty()){
					OWLLiteral litval = (OWLLiteral) ax.getAnnotations(annprop).toArray(new OWLAnnotation[]{})[0].getValue();
					val = litval.parseDouble();
				}
			}
		}
		return val;
	}
}
