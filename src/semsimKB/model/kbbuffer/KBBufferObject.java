package semsimKB.model.kbbuffer;

import java.net.URI;

import semsimKB.model.SemSimObject;
import vprExplorer.buffer.ComponentStatus;

public class KBBufferObject<T extends SemSimObject> {
	protected T object;
	protected ComponentStatus status;

	public KBBufferObject(T obj, ComponentStatus stat) {
		object = obj; status = stat;
	}
	
	public String getName() {
		return object.getName();
	}
	
	public T getComponent() {
		return object;
	}
	
	public ComponentStatus getStatus() {
		return status;
	}
	
	public void changeStatus(ComponentStatus stat) {
		status = stat;
	}
	
	public boolean equals(T other) {
		return object == other;
	}
	
	public boolean isModifiable() {
		return ((status==ComponentStatus.EXACT_MATCH) || (status==ComponentStatus.EXTERNAL_TO_MODEL));

	}
	
	public URI getURI() {
		return object.getURI();
	}
}
