package vprExplorer.buffer;

//This class provides a local buffer for information pulled from the database or
//information being edited or added before being pushed to the database

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.net.URI;

import semsimKB.SemSimKBConstants;
import semsimKB.SemSimKBConstants.kbcomponentstatus;
import semsimKB.SemSimKBConstants.kbcomptype;
import semsimKB.SemSimKBConstants.modelAnnotations;
import semsimKB.annotation.StructuralRelation;
import semsimKB.model.CompBioModel;
import semsimKB.model.KnowledgeBase;
import semsimKB.model.ModelLite;
import semsimKB.model.SemSimComponent;
import semsimKB.model.physical.CompositePhysicalEntity;
import semsimKB.model.physical.DBPhysicalComponent;
import semsimKB.model.physical.DBCompositeEntity;
import semsimKB.model.physical.PhysicalEntity;
import semsimKB.model.physical.PhysicalModelComponent;
import semsimKB.model.physical.PhysicalProperty;
import semsimKB.model.physical.ReferencePhysicalEntity;
import vprExplorer.knowledgebaseinterface.KnowledgeBaseInterface;

public class KBBufferOperations {
	private KnowledgeBase buffer = new KnowledgeBase();
	private KnowledgeBaseInterface kbsource;
	protected HashSet<URI> modset = new HashSet<>(); //Set of individuals to be changed
	protected HashMap<URI, kbcomponentstatus> statusmap = new HashMap<>();
	protected HashMap<URI, HashMap<URI, kbcomponentstatus>> dbcstatusmap = new HashMap<>();
	protected URI curBioModelURI;
		
	public KBBufferOperations(KnowledgeBaseInterface ldb) {
		kbsource = ldb; 
	}
	
	public void CompareModeltoKB(ModelLite model) {
		//Check if model already in KB
		clearBuffer();
		SemSimComponent localdbmodel = kbsource.getElementwithName(model);

		if (localdbmodel==null) {
			CompBioModel bioModel = new CompBioModel(model);
			bioModel.setURI(URI.create(SemSimKBConstants.VPR_NAMESPACE + model.getName()));
			buffer.addComponent(bioModel);
			curBioModelURI = bioModel.getURI();
			statusmap.put(curBioModelURI, kbcomponentstatus.MISSING);	
		}
		else { 
			buffer.addComponent(localdbmodel);
			curBioModelURI = localdbmodel.getURI();
			statusmap.put(curBioModelURI, kbcomponentstatus.EXACT_MATCH);
		}
		SemSimComponent KBElement = null;
		
		for (PhysicalModelComponent pmc : model.getPhysicalModelComponents()) {
			if ((pmc.getURI()!=null) && (pmc.getURI().toString().contains("_"))) {
				String uri = pmc.getURI().toString();
				int in = uri.lastIndexOf("_");
				if (in >=uri.length()-3) {
					uri = uri.substring(0, in);
					pmc.setURI(URI.create(uri));
				}
		    }
			KBElement = null;
			//Is a composite
			if (pmc.getClassURI() == SemSimKBConstants.COMPOSITE_PHYSICAL_ENTITY_CLASS_URI) {
				pmc.setURI(convertComponentURItoVPRNamespace(pmc));
				KBElement = kbsource.getElementwithURI(pmc.getURI());
				
				if (KBElement!=null) {
					complexComponentStatus(((DBCompositeEntity)KBElement));
					for (URI cbm : ((DBCompositeEntity)KBElement).getBioCompModels()) {
						if (buffer.getModelbyURI(cbm)==null) {
							SemSimComponent mod = kbsource.getElementwithURI(cbm);
							buffer.addComponent(mod);
							statusmap.put(cbm, kbcomponentstatus.EXTERNAL_TO_MODEL);
						}
					}			
					//Ensure composite linked physical properties are in buffer
					for (URI pp : ((DBCompositeEntity)KBElement).getPropertyList()) {
						if (buffer.getPropertybyURI(pp)==null) {
							buffer.addComponent(kbsource.getElementwithURI(pp));
							statusmap.put(pp, kbcomponentstatus.EXTERNAL_TO_MODEL);
						}
					}
					compareComponents((DBCompositeEntity)KBElement, pmc);
					DBPhysicalComponent bc = buffer.getComponentbyURI(KBElement.getURI());
					if (bc == null) {
						buffer.addComponent(KBElement);
					}
				}			
				continue; //Composites handled separately
			}
			KBElement = kbsource.getElementwithURI(pmc.getURI());
						
			if (KBElement != null) {
				buffer.addComponent(KBElement);
				statusmap.put(KBElement.getURI(), kbcomponentstatus.EXACT_MATCH);
			}
		}
		KBElement = null;
		//Retrieve composites only used as subcomponents
		for (URI key : statusmap.keySet()) {
			if (statusmap.get(key)==kbcomponentstatus.EXTERNAL_TO_MODEL) {
				KBElement = kbsource.getElementwithURI(key);
				buffer.addComponent(KBElement);
			}
		}
	}
	
	//Untangle complex composites
	private void complexComponentStatus(DBCompositeEntity complexent) {
			if (complexent.getType()==kbcomptype.COMPLEX) {
				for (URI scuri : complexent.getComponents() ) {
					if (!statusmap.containsKey(scuri)) {
						SemSimComponent scp = kbsource.getElementwithURI(scuri);
						if (scp.getClassURI().equals(SemSimKBConstants.KB_PHYSICAL_ENTITY_CLASS_URI)) {
								statusmap.put(scuri, kbcomponentstatus.EXTERNAL_TO_MODEL);
						}
					}	
				}
			}
			else {
				buffer.addComponent(complexent);
			}
	}
	//Compare similarity of composite properties
	private void compareComponents(DBCompositeEntity KBElement, PhysicalModelComponent pmcElement) {
		kbcomponentstatus status = 	kbcomponentstatus.EXACT_MATCH;	
		URI eleuri = KBElement.getURI();
		//Compare database composite entity to model instance
		if (eleuri.equals(pmcElement.getURI())) {
			for (PhysicalProperty pp : pmcElement.getPhysicalProperties()) {
				if (!KBElement.containsProperty(pp.getURI())) {
					//Check if buffer has physical property, if not add it
					addPropertytoKB(pp);
					KBElement.addProperty(pp, curBioModelURI);
					status = kbcomponentstatus.NEW_ASSOCIATED_PHYS_PROPERTY;
					addPhysComptStatus(eleuri, pp.getURI(), status);
				}
				else { //Add the current model to an existing property if not already associated
					if (!KBElement.getPropertyModelList(pp).contains(curBioModelURI)) {
						KBElement.getPropertyModelList(pp).add(curBioModelURI);
						status = kbcomponentstatus.NEW_INSTANCE_PHYS_PROPERTY; 
						addPhysComptStatus(eleuri, pp.getURI(), status);
					}
				}
			}
			//Mark if property is associated with KB component in another model
			for (PhysicalProperty pp : KBElement.getPhysicalProperties()) {
				if (!pmcElement.getPhysicalProperties().contains(pp))
					addPhysComptStatus(eleuri, pp.getURI(), kbcomponentstatus.EXTERNAL_TO_MODEL);
			}
			//Component linked to current model
			if (!KBElement.containsCBModel(curBioModelURI)) {
				KBElement.addCompBioModel(buffer.getModelbyURI(curBioModelURI));
				status = kbcomponentstatus.NEW_INSTANCE_MODEL;
				addPhysComptStatus(eleuri, curBioModelURI, status);
			}
		}
		statusmap.put(KBElement.getURI(), status);
	}
	
	//Add composite to KB or change existing component
	public int changeComponent(PhysicalModelComponent comp) {
		URI vpruri = convertComponentURItoVPRNamespace(comp);
		comp.setURI(vpruri);
		if (!isModifiable(vpruri)) return 1; 
		if (!hasComponentURI(vpruri)) {
			addComponenttoKB(comp);
			modset.add(vpruri);
			return 0;
		}
		DBPhysicalComponent dbcomp = buffer.getComponentbyURI(vpruri);
		if (hasPhysCompStatusList(dbcomp)) {
			for (URI asuri : dbcstatusmap.get(dbcomp.getURI()).keySet()) {
				int status = dbcstatusmap.get(dbcomp.getURI()).get(asuri).ordinal();
				switch (status) {
					case(2):
						dbcomp.addCompBioModel(buffer.getModelbyURI(curBioModelURI));
						break;					
					case(3): 
						addPropertiestoComponent(dbcomp, comp.getPhysicalProperties());
						break;
					case(4):
						dbcomp.addModeltoProperty(asuri, curBioModelURI);
						break;
				}
			}
		}
		modset.add(vpruri);
		return 2;
	}
	
	public URI convertComponentURItoVPRNamespace(SemSimComponent pmc) {
		pmc.setName(pmc.getName().trim());
		URI uri = URI.create(SemSimKBConstants.VPR_NAMESPACE.replace("#", "/")+pmc.getName().replace(" ", "_"));
		return uri;
	}	
	
	//Method for adding a new physical composite entity or process to kb
	public int addComponenttoKB(PhysicalModelComponent EntitytoAdd){
		URI curi = EntitytoAdd.getClassURI();
		DBPhysicalComponent newDBEntry = new DBPhysicalComponent(EntitytoAdd);
		Set<PhysicalProperty> pps = EntitytoAdd.getPhysicalProperties();
		addPropertiestoKB(pps);
		if (curi==SemSimKBConstants.COMPOSITE_PHYSICAL_ENTITY_CLASS_URI) {
			CompositePhysicalEntity CEtoAdd = (CompositePhysicalEntity)EntitytoAdd;
			//Get all singular reference entities
			for (PhysicalEntity pe : CEtoAdd.getArrayListOfEntities()) {
				//remove numerical index if present
				if (pe.getURI().toString().contains("_")) {
						String uri = pe.getURI().toString();
						int in = uri.lastIndexOf("_");
						if (in >=uri.length()-3) {
							uri = uri.replace(uri.substring(in, uri.length()), "");
							pe.setURI(URI.create(uri));
						}
				}
				//Add Reference Entity if not already in KB
				if (buffer.getRefEntitybyURI(pe.getURI())==null) {
					buffer.addComponent((ReferencePhysicalEntity)pe);
					statusmap.put(pe.getURI(), kbcomponentstatus.MISSING);
				}	
			}
			//Check number of relations, if greater than one, then treat it as a complex comp.
			int rellstsz = CEtoAdd.getArrayListOfStructuralRelations().size();
			if (rellstsz > 1) {
				ArrayList<CompositePhysicalEntity> cpes = new ArrayList<CompositePhysicalEntity>();
				//Break up into smaller composites
				for (int i=1; i <= rellstsz; i++) {
					ArrayList<PhysicalEntity> pear = new ArrayList<PhysicalEntity>();	
					ArrayList<StructuralRelation> srar = new ArrayList<StructuralRelation>();	
					pear.add(CEtoAdd.getArrayListOfEntities().get(i-1)); 
					pear.add(CEtoAdd.getArrayListOfEntities().get(i));
					srar.add(CEtoAdd.getArrayListOfStructuralRelations().get(i-1));
					
					CompositePhysicalEntity newcpe = new CompositePhysicalEntity(pear, srar);
					newDBEntry = new DBCompositeEntity(newcpe, SemSimKBConstants.kbcomptype.BASE);
					newcpe.setURI(newDBEntry.getURI());
					cpes.add(newcpe);
					
					if (buffer.addComponent(newDBEntry)) continue;
					statusmap.put(newDBEntry.getURI(), kbcomponentstatus.MISSING);
					modset.add(newDBEntry.getURI());
				} //create complex composites
				while (cpes.size()>1) {
					for (int i=1; i<=cpes.size(); i++) {
						ArrayList<PhysicalEntity> pear = new ArrayList<PhysicalEntity>();	
						ArrayList<StructuralRelation> srar = new ArrayList<StructuralRelation>();	
						
						pear.add(cpes.get(0));
						pear.add(cpes.get(1));
						srar.add(cpes.get(0).getArrayListOfStructuralRelations().get(0));

						CompositePhysicalEntity newcpe = new CompositePhysicalEntity(pear, srar);
						
						newDBEntry = new DBCompositeEntity(newcpe, SemSimKBConstants.kbcomptype.COMPLEX);
						DBComponentChecker(newDBEntry);
						
						((DBCompositeEntity)newDBEntry).addRelation(cpes.get(1).getArrayListOfStructuralRelations().get(0));
						cpes.add(newcpe);
						boolean b = buffer.addComponent(newDBEntry); //return true if component already exists
						cpes.remove(0); //Remove joined composite from table
						cpes.remove(0);
						if (b) continue;
						statusmap.put(newDBEntry.getURI(), kbcomponentstatus.MISSING);
						modset.add(newDBEntry.getURI());
					}
				}
			}
			else newDBEntry = new DBCompositeEntity(CEtoAdd, SemSimKBConstants.kbcomptype.BASE);
		}
		newDBEntry.addCompBioModel(buffer.getModelbyURI(curBioModelURI));
		addPropertiestoComponent(newDBEntry, pps);
		
		buffer.addComponent(newDBEntry);
		modset.add(newDBEntry.getURI());
		statusmap.put(newDBEntry.getURI(), kbcomponentstatus.MISSING);
		return 0;
	}
	
	private void DBComponentChecker(DBPhysicalComponent newDBEntry) {
		LinkedList<PhysicalModelComponent> invlist = checkComponentValidity(newDBEntry);
		if (!invlist.isEmpty()) {
			for (PhysicalModelComponent pmc : invlist) {
				newDBEntry.ReplaceComponent(pmc, buffer.getComponentbyURI(pmc.getURI()));
			}
		}
	}
	
	private void addPropertiestoComponent(DBPhysicalComponent DBEntry, Set<PhysicalProperty> pps) {
		for (PhysicalProperty pp : pps) {
			buffer.addComponent(pp);
			DBEntry.addProperty(pp, curBioModelURI);
		}
	}
	
	public KnowledgeBase getKB() {
		return buffer;
	}
	
	public void removeBufferComponent(DBPhysicalComponent Entitytoremove) {
		modset.remove(Entitytoremove.getURI());
		dbcstatusmap.remove(Entitytoremove.getURI());
		statusmap.remove(Entitytoremove.getURI());
		buffer.removeComponent(Entitytoremove);
	}
	
	public kbcomponentstatus getComponentStatus(SemSimComponent comp) {
		kbcomponentstatus status = statusmap.get(comp.getURI());
		if (status==null) status = statusmap.get(convertComponentURItoVPRNamespace(comp));
		return status;
	}
	public boolean hasPhysCompStatusList(DBPhysicalComponent comp) {
		if (dbcstatusmap.containsKey(comp.getURI())) return true;
		return false;
	}
	
	public HashMap<URI, kbcomponentstatus> getPhysCompStatusList(DBPhysicalComponent comp) {
		if (hasPhysCompStatusList(comp))
			return dbcstatusmap.get(comp.getURI());
		return null;
	}
	
	public kbcomponentstatus getPhysComptStatus(DBPhysicalComponent comp, URI associatedComp) {
		return dbcstatusmap.get(comp.getURI()).get(associatedComp);
	}
	
	public kbcomponentstatus getPhysComptStatus(URI comp, URI associatedComp) {
		return dbcstatusmap.get(comp).get(associatedComp);
	}	
	
	public void addPhysComptStatus(URI comp, URI associatedComp, kbcomponentstatus status) {
		if (!dbcstatusmap.containsKey(comp))
			dbcstatusmap.put(comp, new HashMap<URI, kbcomponentstatus>());
		
		dbcstatusmap.get(comp).put(associatedComp, status);
	}	
	
	public int addPropertiestoKB(Set<PhysicalProperty> pps) {
		for (PhysicalProperty pp : pps) {
			addPropertytoKB(pp);
		}
		return 0;
	}
	
	public void addPropertytoKB(PhysicalProperty pp) {
			if (!buffer.kbHasComponent(pp)) {
				buffer.addComponent(pp);
				statusmap.put(pp.getURI(), kbcomponentstatus.MISSING);
			}
	}
	
	public boolean hasComponentURI(URI uri) {
		return (statusmap.containsKey(uri));
	}
	
	public Set<? extends SemSimComponent> getComponentSet(URI classURI) {
		return buffer.getSet(classURI);
	}
	
	public URI getCurrentModelURI() {
		return this.curBioModelURI;
	}
	
	public void addModelEdit(modelAnnotations field, String edit) {
		CompBioModel cbm = buffer.getModelbyURI(curBioModelURI);
		switch (field) {
			case CELLML_MODEL_URL_URI:
				cbm.addModelURL(SemSimKBConstants.CELLML_MODEL_URL_URI, edit);
				break;
			case JSIM_MODEL_URL_URI:
				cbm.addModelURL(SemSimKBConstants.JSIM_MODEL_URL_URI, edit);
				break;
			case MATLAB_MODEL_URL_URI:
				cbm.addModelURL(SemSimKBConstants.MATLAB_MODEL_URL_URI, edit);
				break;
			default:
				break;
		}
	}
	//Checks for invalid components and returns a list of them
	public LinkedList<PhysicalModelComponent> checkComponentValidity(DBPhysicalComponent dpc) {
		LinkedList<PhysicalModelComponent> list = new LinkedList<PhysicalModelComponent>();
		LinkedList<PhysicalModelComponent> clist = buffer.getPhysicalSubComponents(dpc);
		for (PhysicalModelComponent pmc : clist) {
			if (pmc.getClassURI()==SemSimKBConstants.COMPOSITE_PHYSICAL_ENTITY_CLASS_URI) {
				list.add(pmc);
			}
		}
		return list;
	}
	
	public boolean isModifiable(URI uri) {
		if ((statusmap.get(uri)==kbcomponentstatus.EXACT_MATCH) || (statusmap.get(uri)==kbcomponentstatus.EXTERNAL_TO_MODEL)) {
			return false;
		}
		return true;
	}
	
	public boolean tobeModified(URI uri) {
		return modset.contains(uri);
	}
	
	public void cancelModify(URI uri) {
		modset.remove(uri);	
	}
	
	private void clearBuffer() {
		buffer.clear(); statusmap.clear(); 
		modset.clear();
		dbcstatusmap.clear();
	}

}
