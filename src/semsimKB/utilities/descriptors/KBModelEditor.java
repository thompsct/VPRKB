package semsimKB.utilities.descriptors;

import java.net.URI;

import semsimKB.model.CompBioModel;
import vprExplorer.buffer.ComponentStatus;

public class KBModelEditor {
	String name;
	URI uri;
	ComponentStatus status;
	
	public KBModelEditor(CompBioModel model, ComponentStatus stat) {
		name = model.getName();
		uri = model.getURI();
		status = stat;
	}
	
	public String getName() {
		return name;
	}
	
	public final URI getURI() {
		return uri;
	}
	
	public ComponentStatus getStatus() {
		return status;
	}
}
