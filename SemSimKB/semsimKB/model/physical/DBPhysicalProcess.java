package semsimKB.model.physical;

import java.net.URI;

import semsim.model.physical.PhysicalProcess;
import semsimKB.SemSimKBConstants;

public class DBPhysicalProcess extends DBPhysicalComponent {
	protected PhysicalProcess physprocess;
	public DBPhysicalProcess(PhysicalProcess newprocess) {
		super(newprocess);
		physprocess = newprocess; 
	}
	
	public PhysicalProcess getPhysicalProcess() {
		return physprocess;
	}
	
	public void setProcess(PhysicalProcess process) {
		physprocess = process;
	}
	@Override
	public  URI getClassURI() {
		return SemSimKBConstants.KB_PHYSICAL_PROCESS_CLASS_URI;
	}
}
