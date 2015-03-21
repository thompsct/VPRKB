package semsimKB.model.physical;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import semsimKB.model.CompBioModel;
import semsimKB.SemSimKBConstants;

public class DBPhysicalComponent extends PhysicalModelComponent {
	protected LinkedList<URI> components = new LinkedList<URI>();
	protected Set<URI> BioModels = new HashSet<>();
	protected Map<URI, Set<URI>> Properties = new HashMap<URI, Set<URI>>();
	
	public DBPhysicalComponent(PhysicalModelComponent entitytoAdd) {
		setName(entitytoAdd.getName());
		setDescription(entitytoAdd.getDescription());
	}
	
	public DBPhysicalComponent(URI uri) {
		setURI(uri);
	}
		
	public void setBioCompModels(Set<CompBioModel> bioModels) {
		for (CompBioModel cbm : bioModels) {
			BioModels.add(cbm.getURI());
		}
	}
	
	public void setBioCompModelsbyURIs(Set<URI> bioModels) {
			BioModels.addAll(bioModels);
	}
	
	public Set<URI> getBioCompModels() {
		return BioModels;
	}
	
	public void addCompBioModel(CompBioModel model) {
		BioModels.add(model.getURI());
	}
	
	public void addCompBioModel(URI model) {
		BioModels.add(model);
	}
	
	public void removeCompBioModel(CompBioModel model) {
		BioModels.remove(model);
	}
	
	public boolean containsCBModel(CompBioModel model) {
		if (BioModels.contains(model)) return true;
		else return false;
	}
	public boolean containsCBModel(URI modeluri) {
		for (URI cbm : BioModels) {
			if (cbm.equals(modeluri)) return true;	
		}
		return false;
	}
	
	public Set<URI> getPropertyModelList(PhysicalProperty key) {
		return Properties.get(key.getURI());
	}
	
	public Map<URI, Set<URI>> getPropertyMap() {
		return Properties;
	}
	
	public Set<URI> getPropertyList() {
		return Properties.keySet();
	}
	
	public void addComponent(PhysicalModelComponent pmc) {
		components.add(pmc.getURI());
	}
	
	public void addComponent(URI pmc) {
		components.add(pmc);
	}
	
	public void ReplaceComponent(PhysicalModelComponent toreplace, PhysicalModelComponent replacer) {
		components.set(components.indexOf(toreplace.getURI()), replacer.getURI());
	}
	
	public void ReplaceComponent(URI toreplace, URI replacer) {
		components.set(components.indexOf(toreplace), replacer);
	}
	
	public LinkedList<URI> getComponents() {			
		return components;
	}
	
	public void addProperty(PhysicalProperty pptoadd, URI cbmuri) {
		if (!Properties.containsKey(pptoadd.getURI())) {
			Set<URI> cbms = new HashSet<URI>();
			cbms.add(cbmuri);
			Properties.put(pptoadd.getURI(), cbms);
		}
		else addModeltoProperty(pptoadd.getURI(), cbmuri);
	}
	
	public void addProperty(URI pptoadd, URI cbmuri) {
		if (!Properties.containsKey(pptoadd)) {
			Set<URI> cbms = new HashSet<URI>();
			cbms.add(cbmuri);
			Properties.put(pptoadd, cbms);
		}
		else addModeltoProperty(pptoadd, cbmuri);
	}
	
	public void addModeltoProperty(URI ppURItoadd, URI cbmuri) {
		Properties.get(ppURItoadd).add(cbmuri);
	}
	
	public boolean containsProperty(PhysicalProperty pptocheck) {
		if (Properties.containsKey(pptocheck.getURI())) return true;
		return false;
	}
	
	public boolean containsProperty(URI pptocheck) {
		if (Properties.containsKey(pptocheck)) return true;
		return false;
	}
	
	public void removeProperty(PhysicalProperty pptoremove) {
		Properties.remove(Properties.get(pptoremove.getURI()));
	}
	
	public void removeProperty(URI pptoremove) {
		Properties.remove(Properties.get(pptoremove));
	}
	
	@Override
	public  URI getClassURI() {
		return SemSimKBConstants.KB_PHYSICAL_CLASS_URI;
	}

}
