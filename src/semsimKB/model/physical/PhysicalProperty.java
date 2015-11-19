package semsimKB.model.physical;

import java.net.URI;

import semsimKB.SemSimKBConstants;
import semsimKB.model.SemSimTypes;

public class PhysicalProperty extends PhysicalModelComponent{

	public PhysicalProperty() {
		super(SemSimTypes.PHYSICAL_PROPERTY);
	}
	public PhysicalProperty(String name, URI uri) {
		super(SemSimTypes.PHYSICAL_PROPERTY);
		setURI(uri);
		addReferenceOntologyAnnotation(SemSimKBConstants.HAS_PHYSICAL_DEFINITION_RELATION, uri, uri.toString());
		setName(name);
	}
	
	@Override
	protected boolean isEquivalent(Object obj) {
		return ((PhysicalProperty)obj).getURI().compareTo(referenceuri)==0;
	}
}
