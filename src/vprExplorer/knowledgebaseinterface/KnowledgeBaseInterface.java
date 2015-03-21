package vprExplorer.knowledgebaseinterface;

import java.net.URI;

import semsimKB.model.SemSimComponent;
import vprExplorer.buffer.KBBufferOperations;

public abstract class KnowledgeBaseInterface {
	public abstract SemSimComponent getElementwithName(SemSimComponent ele);
	public abstract SemSimComponent getElementwithName(String ele);
	
	public abstract SemSimComponent getElementwithURI(SemSimComponent ele);
	public abstract SemSimComponent getElementwithURI(URI eleuri);
	
	public abstract int pushChangestoDatabase(KBBufferOperations kbbuffer);
}
