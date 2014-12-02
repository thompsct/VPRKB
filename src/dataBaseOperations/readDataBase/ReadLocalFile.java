//Class for requesting and pulling data from database
package dataBaseOperations.readDataBase;

import java.net.URI;

import semsim.model.physical.PhysicalModelComponent;
import semsimKB.model.KnowledgeBase;
import semsimKB.model.SemSimComponent;
import vprExplorer.common.fileio;

public class ReadLocalFile extends KBReader {
	//Temporary local database for testing
	private KnowledgeBase tempKB;
	
	public ReadLocalFile() {
		fileio dbfile = new fileio("VPRMasterOntology.owl", null);
		tempKB = dbfile.loadKnowledgeBase();
	}
	
	public SemSimComponent getElementwithName(SemSimComponent ele) {
		String modelelename = ele.getName();
		return getElementwithName(modelelename);
	}
	
	@Override
	public SemSimComponent getElementwithName(String elename) {
		SemSimComponent eletortn = tempKB.getCompBioModelbyName(elename);
		if (eletortn!=null) return eletortn; 
		eletortn =tempKB.getPhysicalComponentbyName(elename);
		if (eletortn!=null) return eletortn; 
		return null;
	}
		
	public KnowledgeBase getKB() {
		return tempKB;
	}
	
	@Override
	public SemSimComponent getElementwithURI(SemSimComponent ele) {
		return getElementwithURI(ele.getURI());
	}
	
	public SemSimComponent getElementwithURI(URI eleuri) {
		if (tempKB.getModelbyURI(eleuri) != null) 
			return tempKB.getModelbyURI(eleuri);
		if (tempKB.getPropertybyURI(eleuri) != null) 
			return tempKB.getPropertybyURI(eleuri);
		if (tempKB.getRefEntitybyURI(eleuri) != null) 
			return tempKB.getRefEntitybyURI(eleuri);
		if (tempKB.getComponentbyURI(eleuri) != null) 
			return tempKB.getComponentbyURI(eleuri);
		else return null;
	}

}
