package semsimKB.utilities;

import java.util.HashMap;

import semsimKB.model.SemSimComponent;
import semsimKB.utilities.descriptors.Descriptor;

public abstract class SemSimComponentDescriptor {
	protected HashMap<Descriptor, String> descriptormap = new HashMap<Descriptor, String>();
	
	public SemSimComponentDescriptor(SemSimComponent ssc) {
		descriptormap.put(Descriptor.name, ssc.getName());
		descriptormap.put(Descriptor.description, ssc.getDescription());
		descriptormap.put(Descriptor.uri, ssc.getURI().toString());
	}
	
	public String getDescriptorValue(Descriptor desc) {
		return descriptormap.get(desc);
	}
}
	
