package semsimKB.model.physical;

import semsimKB.model.SemSimTypes;


public class CustomPhysicalEntity extends PhysicalModelComponent implements PhysicalEntity{
	
	public CustomPhysicalEntity(String name, String description){
		super(SemSimTypes.CUSTOM_PHYSICAL_ENTITY);
		setName(name);
		setDescription(description);
	}
	
	public String getFullName() {
		return getName();
	}
	
	@Override
	protected boolean isEquivalent(Object obj) {
		return ((CustomPhysicalEntity)obj).getName().equals(getName());
	}
}
