//Class for requesting and pulling data from a simulated database
package vprExplorer.knowledgebaseinterface;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.tuple.Pair;

import semsimKB.definitions.StructuralRelation;
import semsimKB.model.CompBioModel;
import semsimKB.model.SemSimComponent;
import semsimKB.model.kbbuffer.KBCompositeObject;
import semsimKB.model.kbbuffer.KnowledgeBase;
import semsimKB.model.physical.DBCompositeEntity;
import semsimKB.model.physical.PhysicalEntity;
import semsimKB.model.physical.PhysicalProperty;
import semsimKB.model.physical.ReferencePhysicalEntity;
import semsimKB.reading.KBOWLreader;
import semsimKB.writing.KBOWLwriter;
import vprExplorer.buffer.ComponentStatus;

public class LocalKnowledgeBase extends KnowledgeBaseInterface {
	//Temporary local database for testing
	private KnowledgeBase localKB = new KnowledgeBase();
	private File kbfile = new File("VPRMasterOntology.owl");
	
	private HashMap<String, CompBioModel> namemodelmap = new HashMap<String, CompBioModel>();
	private HashMap<String, PhysicalProperty> nameppmap = new HashMap<String, PhysicalProperty>();
	private HashMap<String, ReferencePhysicalEntity> namerpemap = new HashMap<String, ReferencePhysicalEntity>();	
	public LocalKnowledgeBase(KnowledgeBase buff) {
		super(buff);
		KBOWLreader kbReader = new KBOWLreader();
		try {
			localKB = kbReader.readFromFile(kbfile);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		makeNameCpntMaps();
	}
	//Create local name map
	private void makeNameCpntMaps() {
		for (CompBioModel cbm : localKB.getModels()) {
			namemodelmap.put(cbm.getName(), cbm);
		}
		for (ReferencePhysicalEntity rpe : localKB.getReferenceEntities()) {
			namerpemap.put(rpe.getName(), rpe);
		}
		for (PhysicalProperty pp : localKB.getProperties()) {
			nameppmap.put(pp.getName(), pp);
		}
	}
	
	public boolean getElementwithName(SemSimComponent ele, boolean verifyonly) {
		return getElementwithName(ele.getName(), verifyonly);
	}
	
	@Override
	public boolean getElementwithName(String elename, boolean verifyonly) {
		CompBioModel cbm = namemodelmap.get(elename);
		if (cbm!=null) {
			if (!verifyonly) {
				buffer.addModel(cbm, ComponentStatus.EXACT_MATCH);
			}
			return true;
		}
		PhysicalProperty pp = nameppmap.get(elename);
		if (pp!=null) {
			if (!verifyonly) {
				buffer.addPhysicalProperty(pp, ComponentStatus.EXACT_MATCH);
			}
			return true;
		}
		ReferencePhysicalEntity rpe = namerpemap.get(elename);
		if (rpe!=null) {
			if (!verifyonly) {
				buffer.addReferencePhysicalEntity(rpe, ComponentStatus.EXACT_MATCH);
			}
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean getElementwithURI(SemSimComponent ele, boolean verifyonly) {
		return getElementwithURI(ele.getURI(), verifyonly);
	}
	
	public boolean getElementwithURI(URI eleuri, boolean verifyonly) {
		KBCompositeObject<DBCompositeEntity> dce = localKB.getKBCompositeObject(eleuri);
		if (dce!=null) {
			if (!verifyonly) {
				buffer.addComposite(dce);
			}
			return true;
		}
		CompBioModel cbm = localKB.getModelbyURI(eleuri);
		if (cbm!=null) {
			if (!verifyonly) {
				buffer.addModel(cbm, ComponentStatus.EXACT_MATCH);
			}
			return true;
		}
		PhysicalProperty pp = localKB.getPropertybyURI(eleuri);
		if (pp!=null) {
			if (!verifyonly) {
				buffer.addPhysicalProperty(pp, ComponentStatus.EXACT_MATCH);
			}
			return true;
		}
		ReferencePhysicalEntity rpe = localKB.getRefEntitybyURI(eleuri);
		if (rpe!=null) {
			if (!verifyonly) {
				buffer.addReferencePhysicalEntity(rpe, ComponentStatus.EXACT_MATCH);
			}
			return true;
		}
		
		return false;
	}
	
	@Override
	public DBCompositeEntity retrieveComposite(Pair<URI, URI> comps,
			StructuralRelation rel) {
		
		Pair<PhysicalEntity, PhysicalEntity> pepair =
				Pair.of(localKB.getPhysicalEntitybyURI(comps.getLeft()), localKB.getPhysicalEntitybyURI(comps.getRight()));
		if (pepair.getLeft()==null || pepair.getRight() == null) {
			return null;
		}

		for (DBCompositeEntity dce : localKB.getComposites()) {
			if (dce.componentEntityListsMatch(pepair)) {
				if (dce.getRelation() == rel) {
					addKBCompositeObject(dce);
					return dce;
				}
			}
		}
		return null;
	}
	
	private void addKBCompositeObject(DBCompositeEntity dbc) {
		ArrayList<ComponentStatus> pstats = new ArrayList<ComponentStatus>();
		
		for (PhysicalProperty pp : dbc.getPropertyList()) {
			pp = buffer.getPropertybyURI(pp.getURI());
			pstats.add(ComponentStatus.EXACT_MATCH);
		}
		KBCompositeObject<DBCompositeEntity> kbco = 
				new KBCompositeObject<DBCompositeEntity>(dbc, ComponentStatus.EXACT_MATCH, pstats);
		buffer.addComposite(kbco);
	}
	
	@Override
	public int pushChangestoDatabase(URI modeluri, ArrayList<KBCompositeObject<DBCompositeEntity>> composites) {
		//Check for and add models first
		for (CompBioModel cbm : buffer.getModels()) {
			if (buffer.getModelStatusbyURI(cbm.getURI()) == ComponentStatus.MISSING) {
				localKB.addModel(cbm, ComponentStatus.EXACT_MATCH);
			}	
		}
		
		for (PhysicalProperty pp : buffer.getProperties()) {
			if (buffer.getPropertyStatusbyURI(pp.getURI()) == ComponentStatus.MISSING) {
				localKB.addPhysicalProperty(pp, ComponentStatus.EXACT_MATCH);
			}	
		}
		
		for (ReferencePhysicalEntity rpe : buffer.getReferenceEntities()) {
			if (buffer.getRefEntityStatusbyURI(rpe.getURI()) == ComponentStatus.MISSING) {
				localKB.addReferencePhysicalEntity(rpe, ComponentStatus.EXACT_MATCH);
			}	
		}
			
		//Add Physical Composites
		for (KBCompositeObject<DBCompositeEntity> kdce : composites) {
			if (kdce.getStatus()== ComponentStatus.MISSING) {
				localKB.addComposite(kdce);		
				continue;
			}
		}
		writeKnowledgeBase();
		return 0;
	}
	
	public int writeKnowledgeBase() {
		if (!kbfile.canWrite()) System.out.println("Write Failed");
		KBOWLwriter kbWriter = new KBOWLwriter(buffer, kbfile);
		try {
			kbWriter.writeToFile();
			return 0;
		} 
		catch (Exception e) {
			e.printStackTrace();
			}
		return -1;
	}
}
