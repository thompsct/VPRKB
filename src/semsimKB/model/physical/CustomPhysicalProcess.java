package semsimKB.model.physical;

import java.util.HashSet;
import java.util.Set;

import semsimKB.model.SemSimTypes;

public class CustomPhysicalProcess extends PhysicalProcess{
	public String ID; // For CB output
	public Set<CustomPhysicalEntity> setofinputs = new HashSet<CustomPhysicalEntity>(); // For CB output
	public Set<CustomPhysicalEntity> setofoutputs = new HashSet<CustomPhysicalEntity>(); // For CB output
	public Set<CustomPhysicalEntity> setofmediators = new HashSet<CustomPhysicalEntity>(); // For CB output
	
	public CustomPhysicalProcess(String name, String description){
		super(SemSimTypes.CUSTOM_PHYSICAL_PROCESS);
		setName(name);
		setDescription(description);
	}
}
