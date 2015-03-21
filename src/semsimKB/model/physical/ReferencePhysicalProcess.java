package semsimKB.model.physical;

import java.net.URI;

import semsimKB.SemSimKBConstants;

public class ReferencePhysicalProcess extends PhysicalProcess {
	
	public ReferencePhysicalProcess(URI uri, String description){
		addReferenceOntologyAnnotation(SemSimKBConstants.REFERS_TO_RELATION, uri, description);
		setName(description);
	}
	@Override
	public  URI getClassURI() {
		return SemSimKBConstants.REFERENCE_PHYSICAL_PROCESS_CLASS_URI;
	}
}
