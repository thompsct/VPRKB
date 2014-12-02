package dataBaseOperations.readDataBase;

import java.net.URI;

import semsimKB.model.SemSimComponent;

public abstract class KBReader {
	public abstract SemSimComponent getElementwithName(SemSimComponent ele);
	public abstract SemSimComponent getElementwithName(String ele);
	
	public abstract SemSimComponent getElementwithURI(SemSimComponent ele);
	public abstract SemSimComponent getElementwithURI(URI eleuri);
}
