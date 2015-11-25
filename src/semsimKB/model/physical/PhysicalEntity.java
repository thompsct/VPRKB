package semsimKB.model.physical;

import java.net.URI;

import semsimKB.definitions.SemSimTypes;

public interface PhysicalEntity {
	
	public String getName();
	public URI getURI();
	public String getFullName();
	public SemSimTypes getType();
	public URI getClassURI();
}
