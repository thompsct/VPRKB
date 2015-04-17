package semsimKB.utilities.descriptors;

import java.util.ArrayList;
import java.util.Set;

import semsimKB.annotation.StructuralRelation;
import semsimKB.model.physical.CompositePhysicalEntity;
import semsimKB.model.physical.PhysicalEntity;
import semsimKB.model.physical.PhysicalProperty;
import semsimKB.model.physical.ReferencePhysicalEntity;

public class CompositeEntityDescriptor {
	ArrayList<String[]> description = new ArrayList<String[]>();
	
	public CompositeEntityDescriptor(CompositePhysicalEntity cpe) {
		description.add(new String[]{"Name:", cpe.getName()});
		ArrayList<PhysicalEntity> pes = new ArrayList<PhysicalEntity>();
		for (ReferencePhysicalEntity rpe :cpe.getArrayListOfEntities()) {
			pes.add(rpe);
		}
		makeEntityDescriptor(pes, cpe.getArrayListOfStructuralRelations());
		makePropertyDescriptor(cpe.getPhysicalProperties());
	}
	
	private void makeEntityDescriptor(ArrayList<PhysicalEntity> pes, ArrayList<StructuralRelation> rels) {
		description.add(new String[]{"Index Entity:", pes.get(0).getFullName()});
		for (int i = 1; i< pes.size(); i++) {
			PhysicalEntity pe = pes.get(i);
			description.add(new String[]{rels.get(i-1).getShortDescription(), pe.getName()});
			i++;
		}
	}
	
	private void makePropertyDescriptor(Set<PhysicalProperty> pps) {
		for (PhysicalProperty pp : pps) {
			description.add(new String[]{"Associated Physical Property:", pp.getName()});
		}		
	}
	
	public ArrayList<String[]> getDescription() {
		return description;
	}
}
