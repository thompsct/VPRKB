package semsimKB.model.physical;

import java.net.URI;

import semsimKB.SemSimKBConstants;
import semsimKB.model.SemSimTypes;

public class ReferencePhysicalProcess extends PhysicalProcess {
	
	public ReferencePhysicalProcess(URI uri, String description){
		super(SemSimTypes.REFERENCE_PHYSICAL_PROCESS);
		addReferenceOntologyAnnotation(SemSimKBConstants.HAS_PHYSICAL_DEFINITION_RELATION, uri, description);
		setName(description);
	}
	@Override
	public  URI getClassURI() {
		return SemSimTypes.REFERENCE_PHYSICAL_PROCESS.getURI();
	}
}
