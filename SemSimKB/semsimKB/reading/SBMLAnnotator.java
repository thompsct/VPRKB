package semsimKB.reading;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.xml.rpc.ServiceException;

import org.jdom.JDOMException;
import org.sbml.libsbml.CVTerm;
import org.sbml.libsbml.Compartment;
import org.sbml.libsbml.Model;
import org.sbml.libsbml.Reaction;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.SBMLReader;
import org.sbml.libsbml.SBase;
import org.sbml.libsbml.Species;
import org.sbml.libsbml.libsbmlConstants;
import org.semanticweb.owlapi.model.OWLException;

import semsimKB.SemSimKBConstants;
import semsimKB.model.ModelLite;
import semsim.model.annotation.Annotation;
import semsimKB.model.annotation.ReferenceOntologyAnnotation;
import semsim.model.physical.PhysicalEntity;
import semsim.model.physical.PhysicalProcess;

public class SBMLAnnotator {

	public static void annotate(File sbmlfile, ModelLite semsimmodel, boolean isonline, Hashtable<String, String[]> ontologytermsandnamescache) throws OWLException, IOException, JDOMException, ServiceException {
		Hashtable<String,MIRIAMannotation> resourcesandanns = new Hashtable<String,MIRIAMannotation>();
		Map<Compartment,PhysicalEntity> compsandphysents = new HashMap<Compartment,PhysicalEntity>();
		Map<Species,PhysicalEntity> speciesandphysents = new HashMap<Species,PhysicalEntity>();

		if(ontologytermsandnamescache==null)
			ontologytermsandnamescache = new Hashtable<String, String[]>();
			
		Model sbmlmodel = null;
		
		SBMLDocument sbmldoc = new SBMLReader().readSBMLFromFile(sbmlfile.getAbsolutePath());
		if (sbmldoc.getNumErrors()>0){
		      System.err.println("Encountered the following SBML errors:");
		      sbmldoc.printErrors();
		}
		else {
			sbmlmodel = sbmldoc.getModel();
			
			// Collect model-level information, apply as annotations on semsimmodel object	
			// First get model notes
			String notes = sbmlmodel.getNotesString();
			notes = notes.replace("<notes>", "");
			notes = notes.replace("</notes>", "");
			semsimmodel.addAnnotation(new Annotation(SemSimKBConstants.HAS_NOTES_RELATION, notes));
			if(sbmlmodel.getName()!=null)
				semsimmodel.addAnnotation(new Annotation(SemSimKBConstants.MODEL_NAME_RELATION, sbmlmodel.getName()));
			if(sbmlmodel.getId()!=null)
				semsimmodel.addAnnotation(new Annotation(SemSimKBConstants.MODEL_ID_RELATION, sbmlmodel.getId()));

			// Get dc terms
			
			// Get CV terms
			for(int i=0; i<sbmlmodel.getNumCVTerms();i++){
				CVTerm term = sbmlmodel.getCVTerm(i);
				if(term.getQualifierType()==1){
					Integer t = Integer.valueOf(term.getBiologicalQualifierType());
					if(SemSimKBConstants.BIOLOGICAL_QUALIFIER_TYPES_AND_RELATIONS.containsKey(t)){
						for(int j=0; j<term.getNumResources(); j++){
							String uri = term.getResourceURI(j);
							semsimmodel.addAnnotation(new Annotation(SemSimKBConstants.BIOLOGICAL_QUALIFIER_TYPES_AND_RELATIONS.get(t), uri));
						}
					}
				}
				if(term.getQualifierType()==0){
					Integer t = Integer.valueOf(term.getModelQualifierType());
					if(SemSimKBConstants.MODEL_QUALIFIER_TYPES_AND_RELATIONS.containsKey(t)){
						for(int h=0; h<term.getNumResources(); h++){
							String uri = term.getResourceURI(h);
							semsimmodel.addAnnotation(new Annotation(SemSimKBConstants.MODEL_QUALIFIER_TYPES_AND_RELATIONS.get(t), uri));
						}
					}
				}
			}
			
			// collect all compartment annotations
			for(int c=0; c<sbmlmodel.getListOfCompartments().size(); c++){
				Compartment comp = sbmlmodel.getCompartment(c);
				
				// Annotate the physical property of the compartment
				PhysicalEntity ent = null;
				String description = null;
				
				if(comp.getName()!=null && !comp.getName().equals("")) description = comp.getName();
				else description = comp.getId();
				
				boolean hasusableann = false;

				// If there is some annotation
				boolean entcreated = false;
				for(int m=0; m<comp.getNumCVTerms(); m++){
					CVTerm cvterm = comp.getCVTerm(m);
					for(int r=0; r<cvterm.getNumResources(); r++){
						String resource = cvterm.getResourceURI(r);
						hasusableann = ((cvterm.getBiologicalQualifierType()==libsbmlConstants.BQB_IS) || 
								(cvterm.getBiologicalQualifierType()==libsbmlConstants.BQB_IS_VERSION_OF));
						MIRIAMannotation ma = getMiriamAnnotation(cvterm.getBiologicalQualifierType(), resource, ontologytermsandnamescache);
						if(ma!=null && !entcreated){
							if(ma.fulluri!=null){
								
								// If the annotation is an "is-a" annotation
								if(cvterm.getBiologicalQualifierType()==libsbmlConstants.BQB_IS){
									ent = semsimmodel.addReferencePhysicalEntity(ma.fulluri, ma.rdflabel);
									description = ent.getFirstRefersToReferenceOntologyAnnotation().getValueDescription();
									resourcesandanns.put(resource, ma);
									entcreated = true;
								}
								else if(cvterm.getBiologicalQualifierType()==libsbmlConstants.BQB_IS_VERSION_OF){
									// Put physical entity in model but don't connect it to data structure
									PhysicalEntity ivoent = semsimmodel.getPhysicalEntityByReferenceURI(ma.fulluri);
									if(ivoent == null){
										ivoent = semsimmodel.addReferencePhysicalEntity(ma.fulluri, ma.rdflabel);
									}
									// Establish is-a relationship with reference annotation here
									if(comp.getName()!=null && !comp.getName().equals(""))
										ent = semsimmodel.addCustomPhysicalEntity(comp.getName(), comp.getName());
									else
										ent = semsimmodel.addCustomPhysicalEntity(comp.getId(), comp.getId());
									ReferenceOntologyAnnotation ann = new ReferenceOntologyAnnotation(
											SemSimKBConstants.BQB_IS_VERSION_OF_RELATION, ma.fulluri, ma.rdflabel);
									ent.addAnnotation(ann);
									entcreated = true;
									description = ent.getDescription();
								}
							}
						}
					}
				}
				
				// If no annotation present, create a new custom physical entity from compartment name
				if(!hasusableann || (isonline && hasusableann && !entcreated)){
					ent = semsimmodel.addCustomPhysicalEntity(description, description);
				}
				if(ent!=null){
					compsandphysents.put(comp, ent);
				}
			}
			// end of compartment for loop, next process semantics for all species
			
			for(int g=0; g<sbmlmodel.getNumSpecies(); g++){
				setCompositeAnnotationForModelComponent(sbmlmodel.getSpecies(g), sbmlmodel, semsimmodel, resourcesandanns, isonline, 
						compsandphysents, speciesandphysents, ontologytermsandnamescache);
			}
			
			// process semantics for reactions
			for(int r=0; r<sbmlmodel.getNumReactions();r++){
				Reaction rxn = sbmlmodel.getReaction(r);
				setCompositeAnnotationForModelComponent(rxn, sbmlmodel, semsimmodel, resourcesandanns, isonline, 
						compsandphysents, speciesandphysents, ontologytermsandnamescache);

				String reactionname = rxn.getId();
				if(rxn.getName()!=null && !rxn.getName().equals("")){
					reactionname = rxn.getName();
				}
				reactionname = reactionname + " reaction";

			}
		}
	}			
			
			
		
	private static Boolean setCompositeAnnotationForModelComponent(SBase modelcomp, Model sbmlmodel, ModelLite semsimmodel, 
			Hashtable<String,MIRIAMannotation> resourcesandanns, boolean isonline, Map<Compartment,PhysicalEntity> compsandphysents,
			Map<Species,PhysicalEntity> speciesandphysents, Hashtable<String, String[]> ontologytermsandnamescache) {
		
		// If we're annotating a chemical species
		if(modelcomp instanceof Species){
			PhysicalEntity speciesent = null;
			Boolean hasisannotation = false;

			Species species = (Species)modelcomp;
			Set<MIRIAMannotation> isversionofmas = new HashSet<MIRIAMannotation>();
			
			for(int h=0;h<species.getNumCVTerms();h++){
				CVTerm cvterm = species.getCVTerm(h);
				if(cvterm.getBiologicalQualifierType()==libsbmlConstants.BQB_IS_VERSION_OF){
					for(int r=0; r<cvterm.getNumResources(); r++){
						MIRIAMannotation ma = collectMiriamAnnotation(cvterm.getBiologicalQualifierType(), cvterm.getResourceURI(r),
								resourcesandanns, ontologytermsandnamescache);
						if(ma.fulluri!=null) isversionofmas.add(ma);
					}
				}
			}
			
			for(int h=0;h<species.getNumCVTerms();h++){
				CVTerm cvterm = species.getCVTerm(h);
				if(cvterm.getBiologicalQualifierType()==libsbmlConstants.BQB_IS
						//|| cvterm.getModelQualifierType()==libsbmlConstants.BQM_IS // because sometimes BioModels annotators slip up
						){ 
					for(int r=0; r<cvterm.getNumResources(); r++){
						
						MIRIAMannotation ma = collectMiriamAnnotation(cvterm.getBiologicalQualifierType(), cvterm.getResourceURI(r),
								resourcesandanns, ontologytermsandnamescache);
		
						// If we have a context for the annotation and a human-readable name, create a new reference physical entity
						if(ma!=null){
							if(ma.fulluri!=null){
								resourcesandanns.put(cvterm.getResourceURI(r), ma);
								speciesent = semsimmodel.getPhysicalEntityByReferenceURI(ma.fulluri);
								if(speciesent == null)
									speciesent = semsimmodel.addReferencePhysicalEntity(ma.fulluri, ma.rdflabel);
								// If the species has already been annotated against a reference term
								// and there is another reference term that is annotated against, add the annotation
								else if(hasisannotation) speciesent.addReferenceOntologyAnnotation(SemSimKBConstants.REFERS_TO_RELATION, ma.fulluri, ma.rdflabel);
								hasisannotation = true;
							}
						}
					}
				}
			}
			// Otherwise we need to create a custom physical entity for the species
			if(!hasisannotation && isonline){
				if(species.getName()!=null && !species.getName().equals(""))
					speciesent = semsimmodel.addCustomPhysicalEntity(species.getName(), species.getName());
				else
					speciesent = semsimmodel.addCustomPhysicalEntity(species.getId(), species.getId());
				
				if(!isversionofmas.isEmpty()){
					MIRIAMannotation appliedann = isversionofmas.toArray(new MIRIAMannotation[]{})[0];
					semsimmodel.addReferencePhysicalEntity(appliedann.fulluri, appliedann.rdflabel);
					speciesent.addReferenceOntologyAnnotation(SemSimKBConstants.BQB_IS_VERSION_OF_RELATION, appliedann.fulluri, appliedann.rdflabel);
				}
			}

			return true;
		}
			
		// If the SBML element is a reaction
		else if(modelcomp instanceof Reaction){
			Reaction rxn = (Reaction)modelcomp;
			PhysicalProcess pproc = null;
			Boolean gotisannotation = false;
			Boolean gotisversionofannotation = false;
			
			// If we are applying annotations
			Set<MIRIAMannotation> isversionofmas = new HashSet<MIRIAMannotation>();
			boolean hasusableisann = false;
			boolean hasusableisversionofann = false;
			for(int h=0;h<rxn.getNumCVTerms();h++){
				CVTerm cvterm = rxn.getCVTerm(h);
				if(cvterm.getBiologicalQualifierType()==libsbmlConstants.BQB_IS_VERSION_OF){
					hasusableisversionofann = true;
					for(int r=0; r<cvterm.getNumResources(); r++){
						MIRIAMannotation ma = collectMiriamAnnotation(cvterm.getBiologicalQualifierType(), cvterm.getResourceURI(r),
								resourcesandanns, ontologytermsandnamescache);
						if(ma.fulluri!=null){
							isversionofmas.add(ma);
							gotisversionofannotation = true;
						}
					}
				}
			}
			
			for(int h=0;h<rxn.getNumCVTerms();h++){
				CVTerm cvterm = rxn.getCVTerm(h);
				if(cvterm.getBiologicalQualifierType()==libsbmlConstants.BQB_IS){ // 
					hasusableisann = true;
					
					for(int r=0; r<cvterm.getNumResources(); r++){

						MIRIAMannotation ma = collectMiriamAnnotation(cvterm.getBiologicalQualifierType(), cvterm.getResourceURI(r),
								resourcesandanns, ontologytermsandnamescache);
						// If we have a context for the annotation and a human-readable name
						if(ma.fulluri!=null){
							gotisannotation = true;
							resourcesandanns.put(cvterm.getResourceURI(r), ma);
							pproc = semsimmodel.getPhysicalProcessByReferenceURI(ma.fulluri);
							if(pproc == null){
								pproc = semsimmodel.addReferencePhysicalProcess(ma.fulluri, ma.rdflabel);
							}
							break;
						}
					}
				}
			}
			
			// If no usable annotations, or if we are online and couldn't get a usable annotation, create custom process
			if((!hasusableisann && !hasusableisversionofann) || 
					(isonline && ((hasusableisann && !gotisannotation) || (hasusableisversionofann && !gotisversionofannotation)
							|| (!hasusableisann && hasusableisversionofann && gotisversionofannotation)))){
				if(rxn.getName()!=null && !rxn.getName().equals("")){
					pproc = semsimmodel.addCustomPhysicalProcess(rxn.getName(), "Custom process: " + rxn.getName());
				}
				else{
					pproc = semsimmodel.addCustomPhysicalProcess(rxn.getId(), "Custom process: " + rxn.getId());
				}
				if(!isversionofmas.isEmpty()){
					MIRIAMannotation appliedann = isversionofmas.toArray(new MIRIAMannotation[]{})[0];
					semsimmodel.addReferencePhysicalProcess(appliedann.fulluri, appliedann.rdflabel);
					pproc.addReferenceOntologyAnnotation(SemSimKBConstants.BQB_IS_VERSION_OF_RELATION, appliedann.fulluri, appliedann.rdflabel);
				}
			}

		}
		return true;
	}
		
	private static MIRIAMannotation collectMiriamAnnotation(int qualifier, String resource, Hashtable<String,MIRIAMannotation> resourcesandanns, 
			Hashtable<String, String[]> ontologytermsandnamescache){
		MIRIAMannotation ma = null;
		if(resourcesandanns.containsKey(resource)) ma = resourcesandanns.get(resource);
		else ma = getMiriamAnnotation(qualifier, resource, ontologytermsandnamescache);
		return ma;
	}
		
	
	private static MIRIAMannotation getMiriamAnnotation(int qualifier, String resource, 
			Hashtable<String, String[]> ontologytermsandnamescache){
		
		String rdflabel = null;
		
		if(ontologytermsandnamescache.containsKey(resource))
			rdflabel = ontologytermsandnamescache.get(resource)[0];
		
		MIRIAMannotation ma = new MIRIAMannotation(qualifier, resource, rdflabel);
		return ma;
	}
		
}
