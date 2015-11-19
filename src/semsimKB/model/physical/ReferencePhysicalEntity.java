package semsimKB.model.physical;

import java.net.URI;

import semsimKB.SemSimKBConstants;
import semsimKB.model.SemSimTypes;

public class ReferencePhysicalEntity extends PhysicalModelComponent implements PhysicalEntity {
	
	public ReferencePhysicalEntity(URI uri, String description){
		super(SemSimTypes.REFERENCE_PHYSICAL_ENTITY);
		addReferenceOntologyAnnotation(SemSimKBConstants.HAS_PHYSICAL_DEFINITION_RELATION, uri, description);
		setName(description);
		setURI(uri);
	}
	
	public String getName() {
		return getFirstRefersToReferenceOntologyAnnotation().getValueDescription();
	}
	
	@Override
	public String getFullName() {
		return getName() + " (" + getFirstRefersToReferenceOntologyAnnotation().getOntologyAbbreviation()+ ")";
	}
	
	@Override
	protected boolean isEquivalent(Object obj) {
		return ((ReferencePhysicalEntity)obj).getURI().toString().equals(referenceuri.toString());
	}
}
