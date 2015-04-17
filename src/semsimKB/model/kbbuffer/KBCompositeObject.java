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
	
	public KBCompositeObject(T obj, ComponentStatus stat, ArrayList<ComponentStatus> propstatus) {
		super(obj, stat);
		propertystatus = propstatus;
	}
	
	public KBCompositeObject(T obj, ComponentStatus stat) {
		super(obj, stat);
		propertystatus = new ArrayList<ComponentStatus>();
	}
	
	public void changePropertyStatus(int pindex, ComponentStatus pstat) {
		propertystatus.set(pindex, pstat );
	}
	
	public void addPropertyStatus(ComponentStatus pstat) {
		propertystatus.add(pstat );
	}
	
	public void removePropertyAssociation(int pindex) {
		propertystatus.remove(pindex);
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
			propertystatus.add(ComponentStatus.NEW_ASSOCIATED_PHYS_PROPERTY);
		}
		int index = dpc.addProperty(pp, mod);
		if (dpc.addModeltoProperty(index, mod)) {
			propertystatus.set(index, ComponentStatus.NEW_PROPERTY_MODEL_ASSOCIATION);
		}
	}
	
	public int getPropertyModelCount(int pindex) {
		return ((DBPhysicalComponent)object).getPropertyModelCount(pindex);
	}
	
	public boolean equals(T other) {
		return object.equals(other);
	}
}