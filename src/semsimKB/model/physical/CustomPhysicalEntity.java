package semsimKB.model.physical;

import java.net.URI;

import semsimKB.SemSimKBConstants;


public class CustomPhysicalEntity extends PhysicalModelComponent implements PhysicalEntity{
	
	public CustomPhysicalEntity(String name, String description){
		setName(name);
		setDescription(description);
	}
	
	public String getFullName() {
		return getName();
	}
	
	@Override
	public  URI getClassURI() {
		return SemSimKBConstants.CUSTOM_PHYSICAL_ENTITY_CLASS_URI;
	}
}
