package dataBaseOperations.writeDataBase;

import java.net.URI;
import java.util.HashMap;

import semsimKB.SemSimKBConstants;
import semsimKB.SemSimKBConstants.kbcomponentstatus;
import semsimKB.model.KnowledgeBase;
import semsimKB.model.SemSimComponent;
import semsimKB.model.physical.DBPhysicalComponent;
import vprExplorer.buffer.KBBufferOperations;
import vprExplorer.common.fileio;

public class WriteLocalFile {
	KnowledgeBase localKB;
	fileio dbfile = new fileio("VPRMasterOntology.owl", null);
	
	public WriteLocalFile(KnowledgeBase lKB) {localKB = lKB; }
	
	public int pushChangestoDatabase(KBBufferOperations kbbuffer) {
		writeBuffertoDatabase(kbbuffer);
		dbfile.writeKnowledgeBase(localKB);
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
