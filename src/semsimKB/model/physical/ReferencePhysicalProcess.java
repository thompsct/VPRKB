package semsimKB.model.physical;

import java.net.URI;

import semsimKB.definitions.SemSimTypes;
import semsimKB.definitions.SemSimRelation.KBRelations;

public class ReferencePhysicalProcess extends PhysicalProcess {
	
	public ReferencePhysicalProcess(URI uri, String description){
		super(SemSimTypes.REFERENCE_PHYSICAL_PROCESS);
		addReferenceOntologyAnnotation(KBRelations.HAS_PHYSICAL_DEFINITION, uri, description);
		setName(description);
	}
	@Override
	public  URI getClassURI() {
		return SemSimTypes.REFERENCE_PHYSICAL_PROCESS.getURI();
	}
}
