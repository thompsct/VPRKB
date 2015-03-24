//Definition for the overall knowledge base
package semsimKB.model;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import semsimKB.SemSimKBConstants;
import semsimKB.model.physical.DBCompositeEntity;
import semsimKB.model.physical.DBPhysicalComponent;
import semsimKB.model.physical.DBPhysicalProcess;
import semsimKB.model.physical.PhysicalModelComponent;
import semsimKB.model.physical.PhysicalProperty;
import semsimKB.model.physical.ReferencePhysicalEntity;

public class KnowledgeBase extends SemSimComponent {
	protected SimpleDateFormat sdf;
	protected String namespace;
	protected Set<String> errors = new HashSet<String>();
	
	protected Set<CompBioModel> BioModels = new HashSet<>();
	protected Set<DBCompositeEntity> physicalCompositeEntities = new HashSet<>();
	protected Set<DBPhysicalProcess> PhysicalComponentProcesses = new HashSet<>();
	protected Set<ReferencePhysicalEntity> PhysicalReferenceEntities = new HashSet<>();
	protected Set<PhysicalProperty> PhysicalProperties = new HashSet<>();

	//URI strings and their respective components
	private Map<URI, CompBioModel> URIandCBMmap = new HashMap<URI, CompBioModel>();	
	private Map<URI, PhysicalProperty> URIandPPmap = new HashMap<URI, PhysicalProperty>();	
	private Map<URI, ReferencePhysicalEntity> URIandRPEmap = new HashMap<URI, ReferencePhysicalEntity>();
	private Map<URI, DBPhysicalComponent> URIandDBCmap = new HashMap<URI, DBPhysicalComponent>();	
	
	//Retrieve set of objects by class URI
	protected HashMap<URI, Set<? extends SemSimComponent>> setmap = new HashMap<>();
	
	public KnowledgeBase() {
		setmap.put(SemSimKBConstants.KB_MODEL_URI, BioModels);
		setmap.put(SemSimKBConstants.KB_COMPOSITE_CLASS_URI, physicalCompositeEntities);
		setmap.put(SemSimKBConstants.COMPOSITE_PHYSICAL_ENTITY_CLASS_URI, physicalCompositeEntities);
		setmap.put(SemSimKBConstants.KB_PROCESS_CLASS_URI, PhysicalComponentProcesses);
		setmap.put(SemSimKBConstants.CUSTOM_PHYSICAL_PROCESS_CLASS_URI, PhysicalComponentProcesses);
		setmap.put(SemSimKBConstants.REFERENCE_PHYSICAL_PROCESS_CLASS_URI, PhysicalComponentProcesses);
		setmap.put(SemSimKBConstants.REFERENCE_PHYSICAL_ENTITY_CLASS_URI, PhysicalReferenceEntities);
		setmap.put(SemSimKBConstants.PHYSICAL_PROPERTY_CLASS_URI, PhysicalProperties);
	}
	
	/**
	 * Add an error to the model. Errors are just string notifications indicating a 
	 * problem with the model
	 * @param error A string describing the error
	 */
	public void addError(String error){
		errors.add(error);
	}
	
	/**
	 * Set the namespace of the model.
	 * @param namespace The namespace to use.
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	//Remove all data from knowledge base object	
	public void clear() {
		setURI(null); setName(""); namespace = ""; 
		BioModels.clear(); physicalCompositeEntities.clear();
		 errors.clear(); PhysicalComponentProcesses.clear();
		 PhysicalReferenceEntities.clear(); PhysicalProperties.clear();
		 URIandCBMmap.clear(); URIandDBCmap.clear(); 
		 URIandPPmap.clear(); URIandRPEmap.clear();
	}
		
	public Set<? extends SemSimComponent> getSet(URI uri) {
		Set<? extends SemSimComponent> ComponentSet;
		if (uri==SemSimKBConstants.KB_PHYSICAL_CLASS_URI) {
			ComponentSet = getKBPhysicalModelComponents();
		}
		else 
			if (uri==SemSimKBConstants.PHYSICAL_MODEL_COMPONENT_CLASS_URI) 
				ComponentSet = getPhysicalModelComponents();
			else ComponentSet = setmap.get(uri); 
		return ComponentSet;
	}
	
		//Return all physical components in the buffer database
	public Set<PhysicalModelComponent> getPhysicalModelComponents() {
		Set<PhysicalModelComponent> allComponents = new HashSet<PhysicalModelComponent>();
		
		if (!physicalCompositeEntities.isEmpty()) allComponents.addAll(physicalCompositeEntities);
		if (!PhysicalComponentProcesses.isEmpty()) allComponents.addAll(PhysicalComponentProcesses);
		if (!PhysicalReferenceEntities.isEmpty()) allComponents.addAll(PhysicalReferenceEntities);
		if (!PhysicalProperties.isEmpty()) allComponents.addAll(PhysicalProperties);
		
		return allComponents;
	}

	//Return all physical kb components in the buffer database
public Set<DBPhysicalComponent> getKBPhysicalModelComponents() {
	Set<DBPhysicalComponent> allkbComponents = new HashSet<DBPhysicalComponent>();
	
	if (!physicalCompositeEntities.isEmpty()) allkbComponents.addAll(physicalCompositeEntities);
	if (!PhysicalComponentProcesses.isEmpty()) allkbComponents.addAll(PhysicalComponentProcesses);
	
	return allkbComponents;
}
	

	public boolean addComponent(SemSimComponent ElementtoAdd) {
		if (kbHasComponent(ElementtoAdd)) return true;
		URI compURI = ElementtoAdd.getClassURI();
		if (compURI==SemSimKBConstants.KB_COMPOSITE_CLASS_URI) {
			physicalCompositeEntities.add((DBCompositeEntity) ElementtoAdd);	
			URIandDBCmap.put(ElementtoAdd.getURI(), (DBCompositeEntity)ElementtoAdd);
		}
		else if (compURI==SemSimKBConstants.KB_PROCESS_CLASS_URI) {
			PhysicalComponentProcesses.add((DBPhysicalProcess)ElementtoAdd);
			URIandDBCmap.put(ElementtoAdd.getURI(), (DBPhysicalProcess)ElementtoAdd);
		}
		else if (compURI==SemSimKBConstants.KB_MODEL_URI) {
			BioModels.add((CompBioModel)ElementtoAdd);		
			URIandCBMmap.put(ElementtoAdd.getURI(), (CompBioModel)ElementtoAdd);
		}
			
		else if (compURI==SemSimKBConstants.REFERENCE_PHYSICAL_ENTITY_CLASS_URI) {
			PhysicalReferenceEntities.add((ReferencePhysicalEntity)ElementtoAdd);	
			URIandRPEmap.put(ElementtoAdd.getURI(), (ReferencePhysicalEntity)ElementtoAdd); 
		}
		else if (compURI==SemSimKBConstants.PHYSICAL_PROPERTY_CLASS_URI) {
			PhysicalProperties.add((PhysicalProperty)ElementtoAdd);	
			URIandPPmap.put(ElementtoAdd.getURI(), (PhysicalProperty)ElementtoAdd);
		}
		return false;
	}
	
	public SemSimComponent getComponent(SemSimComponent component) {
		for (SemSimComponent ssc : setmap.get(component.getClassURI())) {
			if (ssc.getName()==component.getName()) return ssc;
		}
		return null;
	}
	
	public DBPhysicalComponent getComponentbyURI(URI uritoget) {
		if (URIandDBCmap.containsKey(uritoget)) 
			return URIandDBCmap.get(uritoget);
		return null;
	}
	
	public CompBioModel getModelbyURI(URI uritoget) {
		if (URIandCBMmap.containsKey(uritoget)) 
			return URIandCBMmap.get(uritoget);
		return null;
	}
	
	public ReferencePhysicalEntity getRefEntitybyURI(URI uritoget) {
		if (URIandRPEmap.containsKey(uritoget)) 
			return URIandRPEmap.get(uritoget);
		return null;
	}
	
	public PhysicalProperty getPropertybyURI(URI uritoget) {
		if (URIandPPmap.containsKey(uritoget)) 
			return URIandPPmap.get(uritoget);
		return null;
	}
	
	public boolean kbHasComponent(SemSimComponent component) {
		Set<? extends SemSimComponent> set = setmap.get(component.getClassURI());
		if (!set.isEmpty()) {
			if (set.contains(component)) return true;
		}
			
		return false;
	}
	
	public void removeComponent(SemSimComponent component) {
		URI compURI = component.getClassURI();
		URI curi = component.getURI();
		if (compURI==SemSimKBConstants.KB_COMPOSITE_CLASS_URI) {
			URIandDBCmap.remove(curi);
			physicalCompositeEntities.remove(getComponentbyURI(curi));
		}
		else if (compURI==SemSimKBConstants.KB_PROCESS_CLASS_URI) {
			URIandDBCmap.remove(component.getURI());
			physicalCompositeEntities.remove((DBCompositeEntity) component);
		}
			
		else if (compURI==SemSimKBConstants.KB_MODEL_URI) {
			URIandPPmap.remove(component.getURI());
			PhysicalComponentProcesses.remove((DBPhysicalProcess) component);
		}
		else if (compURI==SemSimKBConstants.REFERENCE_PHYSICAL_ENTITY_CLASS_URI) {
			URIandRPEmap.remove(component.getURI());
			PhysicalReferenceEntities.remove((ReferencePhysicalEntity)component);	
		}
		else if (compURI==SemSimKBConstants.PHYSICAL_PROPERTY_CLASS_URI) {
			URIandPPmap.remove(component.getURI());
			PhysicalProperties.remove((PhysicalProperty)component);	
		}
	}
	
	public CompBioModel getCompBioModelbyName(String modelname) {
		for (CompBioModel cbm : BioModels)
				if (modelname.equals(cbm.getName())) return cbm;
		return null;
	}
	
	public PhysicalModelComponent getPhysicalComponentbyName(String compname) {
		
		for (PhysicalModelComponent comp : getPhysicalModelComponents())
				if (compname.equals(comp.getName())) return comp;
		return null;
	}
	
	public LinkedList<PhysicalModelComponent> getPhysicalSubComponents(DBPhysicalComponent pmc) {
		LinkedList<URI> scuris = pmc.getComponents();
		LinkedList<PhysicalModelComponent> comps = new LinkedList<PhysicalModelComponent>();
		for (URI scuri : scuris) {
			if (URIandRPEmap.containsKey(scuri)) {
				comps.add(URIandRPEmap.get(scuri));	
			}
			else if (URIandDBCmap.containsKey(scuri)) {
				comps.add(URIandDBCmap.get(scuri));
			}
		}
		return comps;
	}
	
	@Override
	public  URI getClassURI() {
		return SemSimKBConstants.KNOWLEDGE_BASE_CLASS_URI;
	}
}

