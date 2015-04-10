package semsimKB.model.physical;

import java.net.URI;

import semsimKB.SemSimKBConstants;

public class ReferencePhysicalEntity extends PhysicalModelComponent implements PhysicalEntity {
	
	public ReferencePhysicalEntity(URI uri, String description){
		addReferenceOntologyAnnotation(SemSimKBConstants.REFERS_TO_RELATION, uri, description);
		setName(description);
	}
	
	public String getName() {
		return getFirstRefersToReferenceOntologyAnnotation().getValueDescription();
	}
	
	@Override
	public  URI getClassURI() {
		return SemSimKBConstants.REFERENCE_PHYSICAL_ENTITY_CLASS_URI;
	}
}
