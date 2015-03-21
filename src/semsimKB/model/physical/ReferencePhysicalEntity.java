package semsimKB.model.physical;

import java.net.URI;

import semsimKB.SemSimKBConstants;

public class ReferencePhysicalEntity extends PhysicalEntity {
	
	public ReferencePhysicalEntity(URI uri, String description){
		addReferenceOntologyAnnotation(SemSimKBConstants.REFERS_TO_RELATION, uri, description);
		setName(description);
	}
	
	@Override
	public  URI getClassURI() {
		return SemSimKBConstants.REFERENCE_PHYSICAL_ENTITY_CLASS_URI;
	}
}
