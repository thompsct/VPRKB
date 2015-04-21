package semsimKB.utilities.descriptors;

import java.util.ArrayList;
import java.util.Set;

import semsimKB.annotation.StructuralRelation;
import semsimKB.model.physical.CompositePhysicalEntity;
import semsimKB.model.physical.PhysicalEntity;
import semsimKB.model.physical.PhysicalProperty;
import semsimKB.model.physical.ReferencePhysicalEntity;

public class CompositeEntityDescriptor {
	private ArrayList<String[]> description = new ArrayList<String[]>();
	private ArrayList<String[]> additional = new ArrayList<String[]>();
	private String[] blank = new String[]{"", ""};
	
	public CompositeEntityDescriptor(CompositePhysicalEntity cpe) {
		addRow(new String[]{"Name:", cpe.getName()});
		
		ArrayList<PhysicalEntity> pes = new ArrayList<PhysicalEntity>();
		for (ReferencePhysicalEntity rpe :cpe.getArrayListOfEntities()) {
			pes.add(rpe);
		}
		makeEntityDescriptor(pes, cpe.getArrayListOfStructuralRelations());
		makePropertyDescriptor(cpe.getPhysicalProperties());
	}
	

	
	private void makeEntityDescriptor(ArrayList<PhysicalEntity> pes, ArrayList<StructuralRelation> rels) {
		addRow(new String[]{"Index Entity:", pes.get(0).getFullName()}, new String[]{"", pes.get(0).getURI().toString()});
		for (int i = 1; i< pes.size(); i++) {
			PhysicalEntity pe = pes.get(i);
			addRow(new String[]{rels.get(i-1).getShortDescription(), pe.getFullName()}, new String[]{"", pe.getURI().toString()});
			i++;
		}
	}
	
	private void addRow(String[] text) {
		description.add(text);
		additional.add(blank);
	}
	
	private void addRow(String[] text, String[] tooltip) {
		description.add(text);
		additional.add(tooltip);
	}
	
	private void makePropertyDescriptor(Set<PhysicalProperty> pps) {
		for (PhysicalProperty pp : pps) {
			addRow(new String[]{"Associated Physical Property:", pp.getName()});
		}		
	}
	
	public ArrayList<String[]> getDescription() {
		return description;
	}
	
	public String[] getAdditionalInformation(int index) {
		return additional.get(index);
	}
}
