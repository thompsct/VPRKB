package semsimKB.utilities.descriptors;

import java.util.ArrayList;
import java.util.Set;

import semsimKB.model.physical.CompositePhysicalEntity;
import semsimKB.model.physical.PhysicalEntity;
import semsimKB.model.physical.PhysicalProperty;
import semsimKB.utilities.SemSimComponentDescriptor;

public class CompositeEntityDescriptor extends SemSimComponentDescriptor{

	public CompositeEntityDescriptor(CompositePhysicalEntity cpe) {
		super(cpe);
		
		makeEntityDescriptor(cpe.getArrayListOfEntities());
		makePropertyDescriptor(cpe.getPhysicalProperties());
	}
	
	private void makeEntityDescriptor(ArrayList<PhysicalEntity> pes) {
		String pestring = new String("");
		for (PhysicalEntity pe : pes) {
			pestring = pestring + pe.getName()+ ",";
		}
		
		descriptormap.put(Descriptor.name, pestring);
	}
	
	private void makePropertyDescriptor(Set<PhysicalProperty> pps) {
		String pestring = new String("");
		for (PhysicalProperty pp : pps) {
			pestring = pestring + pp.getName()+ ",";
		}
		
		descriptormap.put(Descriptor.name, pestring);
	}
}
