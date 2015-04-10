package semsimKB.model.physical;

import java.net.URI;

import semsimKB.SemSimKBConstants;

public class DBPhysicalProcess extends DBPhysicalComponent {
	public DBPhysicalProcess() {
	}
	
	@Override
	public  URI getClassURI() {
		return SemSimKBConstants.KB_PROCESS_CLASS_URI;
	}
}
