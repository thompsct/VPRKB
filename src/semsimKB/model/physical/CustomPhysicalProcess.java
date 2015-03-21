package semsimKB.model.physical;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import semsimKB.SemSimKBConstants;

public class CustomPhysicalProcess extends PhysicalProcess{
	public String ID; // For CB output
	public Set<CustomPhysicalEntity> setofinputs = new HashSet<CustomPhysicalEntity>(); // For CB output
	public Set<CustomPhysicalEntity> setofoutputs = new HashSet<CustomPhysicalEntity>(); // For CB output
	public Set<CustomPhysicalEntity> setofmediators = new HashSet<CustomPhysicalEntity>(); // For CB output
	
	public CustomPhysicalProcess(String name, String description){
		setName(name);
		setDescription(description);
	}
	@Override
	public  URI getClassURI() {
		return SemSimKBConstants.CUSTOM_PHYSICAL_PROCESS_CLASS_URI;
	}
}
