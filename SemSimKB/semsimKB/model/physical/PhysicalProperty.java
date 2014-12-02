package semsimKB.model.physical;

import java.net.URI;

import semsim.model.physical.PhysicalModelComponent;
import semsimKB.SemSimKBConstants;

public class PhysicalProperty extends PhysicalModelComponent{
	
	private PhysicalModelComponent physicalPropertyOf;

	public PhysicalProperty() {}
	public PhysicalProperty(String name, URI uri) {
		setURI(uri);
		setName(name);
	}
	
	public void setPhysicalPropertyOf(PhysicalModelComponent physicalPropertyOf) {
		this.physicalPropertyOf = physicalPropertyOf;
	}

	public PhysicalModelComponent getPhysicalPropertyOf() {
		return physicalPropertyOf;
	}
	@Override
	public  URI getClassURI() {
		return SemSimKBConstants.PHYSICAL_PROPERTY_CLASS_URI;
	}
}
