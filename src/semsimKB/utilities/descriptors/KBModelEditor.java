package semsimKB.utilities.descriptors;

import java.net.URI;

import semsimKB.annotation.CurationalMetadata;
import semsimKB.model.CompBioModel;
import vprExplorer.buffer.ComponentStatus;

public class KBModelEditor {
	String name;
	CurationalMetadata metadata;
	URI uri;
	ComponentStatus status;
	
	public KBModelEditor(CompBioModel model, ComponentStatus stat) {
		name = model.getName();
		uri = model.getURI();
		status = stat;
		metadata = model.getCurationalMetadata();
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
