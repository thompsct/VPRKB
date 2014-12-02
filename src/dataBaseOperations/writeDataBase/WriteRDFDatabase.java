package dataBaseOperations.writeDataBase;

import java.net.URI;
import java.util.HashMap;

import semsimKB.SemSimKBConstants;
import semsimKB.SemSimKBConstants.kbcomponentstatus;
import semsimKB.model.SemSimComponent;
import semsimKB.model.physical.DBPhysicalComponent;
import vprExplorer.Globals;
import vprKB.webserverices.VPRSPARQLWrite;
import vprKBExplorer.Buffer.KBBufferOperations;

public class WriteRDFDatabase {
	Globals globals;
	VPRSPARQLWrite kbw = new VPRSPARQLWrite(globals);
	
	public WriteRDFDatabase(Globals global) {
		globals=global;
	}
	
	public int writeBuffertoDatabase(KBBufferOperations kbbuffer) {
		//Check for and add models first
		for (SemSimComponent cbm : kbbuffer.getComponentSet(SemSimKBConstants.KB_COMPUTATIONAL_BIOMODEL_URI)) {
			switch(kbbuffer.getComponentStatus(cbm)) {
				case MISSING: 
					kbw.addIndividual(cbm, SemSimKBConstants.KB_COMPUTATIONAL_BIOMODEL_URI);
					break;
				default: 
					break;
			}	
		}
		URI muri = kbbuffer.getCurrentModelURI();
		//Add or modify Physical Components
		for (SemSimComponent ssc : kbbuffer.getComponentSet(SemSimKBConstants.PHYSICAL_MODEL_COMPONENT_CLASS_URI)) {
			kbcomponentstatus stat = kbbuffer.getComponentStatus(ssc);
			if (stat==kbcomponentstatus.EXACT_MATCH) continue;
			switch(stat) {
					case MISSING: 
						kbw.addIndividual(ssc, ssc.getClassURI());
						break;
					case NEW_INSTANCE_MODEL:
						kbw.addModeltoIndividual(ssc.getURI(), muri);
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
								kbw.addPropertyModelLinks(dbpc);
								break;
							case NEW_INSTANCE_PHYS_PROPERTY:
								kbw.addModeltoAxiom(dbpc.getURI(), key, kbbuffer.getCurrentModelURI());
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
