package semsimKB.model.physical;

import java.net.URI;

import semsimKB.definitions.SemSimTypes;
import semsimKB.definitions.SemSimRelation.KBRelations;

public class ReferencePhysicalEntity extends PhysicalModelComponent implements PhysicalEntity {
	
	public ReferencePhysicalEntity(URI uri, String description){
		super(SemSimTypes.REFERENCE_PHYSICAL_ENTITY);
		addReferenceOntologyAnnotation(KBRelations.HAS_PHYSICAL_DEFINITION, uri, description);
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
