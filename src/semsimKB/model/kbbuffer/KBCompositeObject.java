package semsimKB.model.kbbuffer;

import java.util.ArrayList;
import java.util.Set;

import semsimKB.model.CompBioModel;
import semsimKB.model.SemSimComponent;
import semsimKB.model.physical.DBPhysicalComponent;
import semsimKB.model.physical.PhysicalProperty;
import vprExplorer.buffer.ComponentStatus;

public class KBCompositeObject<T extends SemSimComponent> extends KBBufferObject<T> {
	protected ArrayList<ComponentStatus> propertystatus;
	protected ArrayList<ArrayList<ComponentStatus>> modelstatus;
	
	public KBCompositeObject(T obj, ComponentStatus stat, ArrayList<ComponentStatus> propstatus, 
			ArrayList<ArrayList<ComponentStatus>> propmodstatus) {
		super(obj, stat);
		propertystatus = propstatus;
		modelstatus = propmodstatus;	
	}
	
	public KBCompositeObject(T obj, ComponentStatus stat) {
		super(obj, stat);
		propertystatus = new ArrayList<ComponentStatus>();
		modelstatus = new ArrayList<ArrayList<ComponentStatus>>();	
	}
	
	public void changePropertyStatus(int pindex, ComponentStatus pstat) {
		propertystatus.set(pindex, pstat );
	}
	
	public void removePropertyAssociation(int pindex) {
		propertystatus.remove(pindex);
		modelstatus.remove(pindex);
	}
	
	public void removePropertyModelAssociation(int pindex, int mindex) {
		modelstatus.get(pindex).remove(mindex);
	}
	
	public void addPropertyAssociation(ComponentStatus pstat, ComponentStatus mstat) {
		propertystatus.add(pstat);
		ArrayList<ComponentStatus> pmlist = new ArrayList<ComponentStatus>();
		pmlist.add(mstat);
		modelstatus.add(pmlist);
		
	}
	
	public void changeModelAssociationStatus(int pindex, int mindex, ComponentStatus mstat) {
		modelstatus.get(pindex).set(mindex, mstat );
	}
	
	public void addModelAssociationStatus(int pindex, ComponentStatus mstat) {
		modelstatus.get(pindex).add(mstat );
	}
	
	public ComponentStatus getPropertyStatus(int pindex) {
		return propertystatus.get(pindex);
	}
	
	
	public int getPropertyCount() {
		return propertystatus.size();
	}
	
	public void addProperties(Set<PhysicalProperty> pps, CompBioModel mod) {	
		for (PhysicalProperty pp : pps) {
			addProperty(pp ,mod);
		}
	}
	
	public void addProperty(PhysicalProperty pp, CompBioModel mod) {
		DBPhysicalComponent dpc = (DBPhysicalComponent)object;
			if (!dpc.containsProperty(pp)) {
				((DBPhysicalComponent)object).addProperty(pp, mod);
				propertystatus.add(ComponentStatus.NEW_ASSOCIATED_PHYS_PROPERTY);
				return;
			}
			int index = dpc.addProperty(pp, mod);
			if (index==-1) return;
			
			addModelAssociationStatus(index, ComponentStatus.NEW_PROPERTY_MODEL_ASSOCIATION);	
	}
	
	public ComponentStatus getModelAssociationStatus(int pindex, int mindex) {
		return modelstatus.get(pindex).get(mindex);
	}
	
	public int getPropertyModelCount(int pindex) {
		return modelstatus.get(pindex).size();
	}
	
	public boolean equals(T other) {
		return object.equals(other);
	}
}