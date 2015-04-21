package semsimKB.model.physical;

import java.net.URI;
import java.util.ArrayList;

import semsimKB.model.CompBioModel;
import semsimKB.model.SemSimComponent;

public abstract class DBPhysicalComponent extends SemSimComponent {
	protected ArrayList<CompBioModel> models = new ArrayList<CompBioModel>();
	protected ArrayList<PhysicalProperty> proplist = new ArrayList<PhysicalProperty>();
	protected ArrayList<ArrayList<CompBioModel>> propmodlist = new ArrayList<ArrayList<CompBioModel>>();
	
	public DBPhysicalComponent() {}
	
	public DBPhysicalComponent(URI uri, String label) {
		setURI(uri);
		setName(label);
	}
	
	public void setBioCompModels(ArrayList<CompBioModel> biomod) {
		models = biomod;
	}
	
	public ArrayList<CompBioModel> getBioCompModels() {
		return models;
	}
	
	public void addCompBioModel(CompBioModel model) {
		models.add(model);
	}

	public void removeCompBioModel(CompBioModel model) {
		models.remove(model);
	}
	
	public boolean containsCBModel(CompBioModel model) {
		return models.contains(model);
	}

	public boolean containsCBModel(URI modeluri) {
		for (CompBioModel cbm : models) {
			if (cbm.equals(modeluri)) return true;	
		}
		return false;
	}
	
	public ArrayList<CompBioModel> getPropertyModelList(int key) {
		return propmodlist.get(key);
	}
	
	public ArrayList<CompBioModel> getPropertyModelList(PhysicalProperty pp) {
		return propmodlist.get(proplist.indexOf(pp));
	}
	
	public ArrayList<PhysicalProperty> getPropertyList() {
		return proplist;
	}
	
	public int addProperty(PhysicalProperty pptoadd, CompBioModel cbmuri) {
		if (!proplist.contains(pptoadd)) {
			ArrayList<CompBioModel> cbms = new ArrayList<CompBioModel>();
			cbms.add(cbmuri);
			proplist.add(pptoadd);
			propmodlist.add(cbms);
			return proplist.size()-1;
		}
		return proplist.indexOf(pptoadd);
	}
		
	public boolean addModeltoProperty(int index, CompBioModel cbmuri) {
		if (!propmodlist.get(index).contains(cbmuri)) {
			propmodlist.get(index).add(cbmuri);
			return true;
		}
		return false;
	}
	
	public boolean containsProperty(PhysicalProperty pptocheck) {
		return containsProperty(pptocheck.getURI());
	}
	
	public boolean containsProperty(URI pptocheck) {
		for (PhysicalProperty pp : proplist) {
			if (pp.getURI().compareTo(pptocheck)==0) return true;
		}
		return false;
	}
	
	public void removeProperty(PhysicalProperty pptoremove) {
		removeProperty(proplist.indexOf(pptoremove));
	}
	
	public void removeProperty(int pptoremove) {
		proplist.remove(pptoremove);
		propmodlist.remove(pptoremove);
	}
	
	public PhysicalProperty getPhysicalProperty(int pindex) {
		return proplist.get(pindex);
	}
	
	public ArrayList<CompBioModel> getPPModelAssociationList(int index) {
		return propmodlist.get(index);
	}
	
	public int getPropertyModelCount(int pindex) {
		return propmodlist.get(pindex).size();
	}
	
	public int getPropertyCount() {
		return proplist.size();
	}

}
