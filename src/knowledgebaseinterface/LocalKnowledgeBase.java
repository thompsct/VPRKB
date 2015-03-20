//Class for requesting and pulling data from database
package knowledgebaseinterface;

import java.io.File;
import java.net.URI;
import java.util.HashMap;

import semsimKB.SemSimKBConstants;
import semsimKB.SemSimKBConstants.kbcomponentstatus;
import semsimKB.model.KnowledgeBase;
import semsimKB.model.SemSimComponent;
import semsimKB.model.physical.DBPhysicalComponent;
import semsimKB.reading.KBOWLreader;
import semsimKB.writing.KBOWLwriter;
import vprExplorer.buffer.KBBufferOperations;

public class LocalKnowledgeBase extends KnowledgeBaseInterface {
	//Temporary local database for testing
	private KnowledgeBase localKB;
	private File kbfile = new File("VPRMasterOntology.owl");
	
	public LocalKnowledgeBase() {
		KBOWLreader kbReader = new KBOWLreader();
		try {
			localKB = kbReader.readFromFile(kbfile);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public SemSimComponent getElementwithName(SemSimComponent ele) {
		return getElementwithName(ele.getName());
	}
	
	@Override
	public SemSimComponent getElementwithName(String elename) {
		SemSimComponent eletortn = localKB.getCompBioModelbyName(elename);
		if (eletortn!=null) return eletortn; 
		eletortn =localKB.getPhysicalComponentbyName(elename);
		if (eletortn!=null) return eletortn; 
		return null;
	}
		
	public KnowledgeBase getKB() {
		return localKB;
	}
	
	@Override
	public SemSimComponent getElementwithURI(SemSimComponent ele) {
		return getElementwithURI(ele.getURI());
	}
	
	public SemSimComponent getElementwithURI(URI eleuri) {
		if (localKB.getModelbyURI(eleuri) != null) 
			return localKB.getModelbyURI(eleuri);
		if (localKB.getPropertybyURI(eleuri) != null) 
			return localKB.getPropertybyURI(eleuri);
		if (localKB.getRefEntitybyURI(eleuri) != null) 
			return localKB.getRefEntitybyURI(eleuri);
		if (localKB.getComponentbyURI(eleuri) != null) 
			return localKB.getComponentbyURI(eleuri);
		else return null;
	}
	
	public int writeKnowledgeBase(KnowledgeBase kbbuffer) {
		if (!kbfile.canWrite()) System.out.println("Write Failed");
		KBOWLwriter kbWriter = new KBOWLwriter(kbbuffer, kbfile);
		try {
			kbWriter.writeToFile();
			return 0;
		} 
		catch (Exception e) {
			e.printStackTrace();
			}
		return -1;
	}
	
	public int pushChangestoDatabase(KBBufferOperations kbbuffer) {
		writeBuffertoDatabase(kbbuffer);
		writeKnowledgeBase(localKB);
		return 0;
	}
	
	public int writeBuffertoDatabase(KBBufferOperations kbbuffer) {
		//Check for and add models first
		for (SemSimComponent cbm : kbbuffer.getComponentSet(SemSimKBConstants.KB_COMPUTATIONAL_BIOMODEL_URI)) {
			switch(kbbuffer.getComponentStatus(cbm)) {
				case MISSING: 
					localKB.addComponent(cbm);
					break;
				default: 
					break;
			}	
		}
		//Add Physical Components
		for (SemSimComponent ssc : kbbuffer.getComponentSet(SemSimKBConstants.PHYSICAL_MODEL_COMPONENT_CLASS_URI)) {
			switch(kbbuffer.getComponentStatus(ssc)) {
				case MISSING: 
					localKB.addComponent(ssc);
					break;
				case NEW_INSTANCE_MODEL:
					DBPhysicalComponent dbpc = (DBPhysicalComponent)ssc;
					((DBPhysicalComponent) localKB.getComponent(dbpc)).setBioCompModelsbyURIs(dbpc.getBioCompModels());
					break;
				default:
					break;
			}	
			if  (ssc.getClassURI().equals(SemSimKBConstants.KB_PHYSICAL_ENTITY_CLASS_URI) 
					|| ssc.getClassURI().equals(SemSimKBConstants.KB_PHYSICAL_PROCESS_CLASS_URI) ) {
				
				DBPhysicalComponent dbpc = (DBPhysicalComponent)ssc;
				HashMap<URI, kbcomponentstatus> smap = kbbuffer.getPhysCompStatusList(dbpc);
				if (smap!=null) {
					for (URI key : smap.keySet()) {
						switch (kbbuffer.getPhysComptStatus(dbpc, key)) {
							case NEW_ASSOCIATED_PHYS_PROPERTY:
								((DBPhysicalComponent)localKB.getComponentbyURI(dbpc.getURI())).addProperty(key, kbbuffer.getCurrentModelURI());
								break;
							case NEW_INSTANCE_PHYS_PROPERTY:
								((DBPhysicalComponent)localKB.getComponentbyURI(dbpc.getURI())).addModeltoProperty(key, kbbuffer.getCurrentModelURI());
								break;
							default:
								break;	
						}
					}
				}
			}
		}
		
		return 0;
	}
}
