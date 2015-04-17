package semsimKB.model.physical;

import java.net.URI;

import semsimKB.SemSimKBConstants;

public class PhysicalProperty extends PhysicalModelComponent{

	public PhysicalProperty() {}
	public PhysicalProperty(String name, URI uri) {
		setURI(uri);
		addReferenceOntologyAnnotation(SemSimKBConstants.REFERS_TO_RELATION, uri, uri.toString());
		setName(name);
	}
	
	@Override
	public  URI getClassURI() {
		return SemSimKBConstants.PHYSICAL_PROPERTY_CLASS_URI;
	}
}
