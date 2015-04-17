//Definition for the overall knowledge base
package semsimKB.model.kbbuffer;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import semsimKB.annotation.StructuralRelation;
import semsimKB.model.CompBioModel;
import semsimKB.model.physical.DBCompositeEntity;
import semsimKB.model.physical.DBPhysicalProcess;
import semsimKB.model.physical.PhysicalEntity;
import semsimKB.model.physical.PhysicalProperty;
import semsimKB.model.physical.ReferencePhysicalEntity;
import vprExplorer.buffer.ComponentStatus;

public class KnowledgeBase {
	protected SimpleDateFormat sdf;
	
	protected ArrayList<KBBufferObject<ReferencePhysicalEntity>> PhysicalReferenceEntities = new ArrayList<KBBufferObject<ReferencePhysicalEntity>>();
	protected ArrayList<KBBufferObject<PhysicalProperty>> PhysicalProperties = new  ArrayList<KBBufferObject<PhysicalProperty>>();
	protected ArrayList<KBBufferObject<CompBioModel>> BioModels = new ArrayList<KBBufferObject<CompBioModel>>();
	
	protected ArrayList<KBCompositeObject<DBCompositeEntity>> physicalCompositeEntities = new ArrayList<KBCompositeObject<DBCompositeEntity>>();
	protected ArrayList<KBCompositeObject<DBPhysicalProcess>>  PhysicalComponentProcesses = new ArrayList<KBCompositeObject<DBPhysicalProcess>>() ;
	
	//URI strings and their respective components
	private HashMap<URI, KBBufferObject<CompBioModel>> URIandCBMmap = new HashMap<URI, KBBufferObject<CompBioModel>>();	
	private HashMap<URI, KBBufferObject<PhysicalProperty>> URIandPPmap = new HashMap<URI, KBBufferObject<PhysicalProperty>>();	
	private HashMap<URI, KBBufferObject<ReferencePhysicalEntity>> URIandRPEmap = new  HashMap<URI, KBBufferObject<ReferencePhysicalEntity>>();
	private HashMap<URI, KBCompositeObject<DBCompositeEntity>> URIandDBCmap = new HashMap<URI, KBCompositeObject<DBCompositeEntity>>();	
	
	public KnowledgeBase() {}

	public void addComposite(KBCompositeObject<DBCompositeEntity> element) {
		if (kbHasComposite(element.getComponent())) return;
		physicalCompositeEntities.add(element);	
		URIandDBCmap.put(element.getURI(), element);
	}
	
	public void addPhysicalProperty(PhysicalProperty component, ComponentStatus status) {
		KBBufferObject<PhysicalProperty> kbo = new KBBufferObject<PhysicalProperty>(component, status);
		PhysicalProperties.add(kbo);
		URIandPPmap.put(component.getURI(), kbo);
	}
		
	public void addReferencePhysicalEntity(ReferencePhysicalEntity component, ComponentStatus status) {
		KBBufferObject<ReferencePhysicalEntity> kbo = new KBBufferObject<ReferencePhysicalEntity>(component, status);
		PhysicalReferenceEntities.add(kbo);
		URIandRPEmap.put(kbo.getURI(), kbo);
	}
	
	public ComponentStatus getRefEntityStatusbyURI(URI rpeuri) {
		return URIandRPEmap.get(rpeuri).getStatus();
	}
		
	public ReferencePhysicalEntity getRefEntitybyURI(URI rpeuri) {
		if (!hasReferenceEntity(rpeuri)) return null;
		return URIandRPEmap.get(rpeuri).getComponent();
	}
	
	public boolean hasReferenceEntity(ReferencePhysicalEntity component) {
		return URIandRPEmap.containsKey(component.getURI());
	}
	
	public boolean hasReferenceEntity(URI rpeuri) {
		return URIandRPEmap.containsKey(rpeuri);
	}
	public void addModel(CompBioModel component, ComponentStatus status) {
		KBBufferObject<CompBioModel> kbo = new KBBufferObject<CompBioModel>(component, status);
		BioModels.add(kbo);
		URIandCBMmap.put(component.getURI(), kbo);
	}
	
	public boolean hasProperty(PhysicalProperty component) {
		return URIandPPmap.containsKey(component.getURI());
	}
	
	public boolean hasProperty(URI ppuri) {
		return URIandPPmap.containsKey(ppuri);
	}
		

	
	public boolean hasModel(URI muri) {
		return URIandCBMmap.containsKey(muri);
	}
	
	public boolean hasModel(CompBioModel component) {
		return URIandCBMmap.containsKey(component.getURI());
	}
	
	public boolean hasKBComposite(URI curi) {
		return URIandDBCmap.containsKey(curi);
	}
	
	public boolean hasKBComposite(Pair<PhysicalEntity, PhysicalEntity> pepair, StructuralRelation sr) {
		for (DBCompositeEntity dbc : getComposites()) {
			if (dbc.componentEntityListsMatch(pepair)) return true;
		}
		return false;
	}
	
	public boolean kbHasComposite(DBCompositeEntity comp) {
		boolean inkb = false;
		for (KBCompositeObject<DBCompositeEntity> kce: physicalCompositeEntities) {
			inkb = kce.equals(comp); 
			if (inkb) break;
		}
		return inkb;
	}
	
	public boolean removeComposite(int index) {
		if (physicalCompositeEntities.get(index).getStatus()!=ComponentStatus.MISSING) {
			return false;
		}
		URI objuri = physicalCompositeEntities.get(index).getURI();

		physicalCompositeEntities.remove(index);
		URIandDBCmap.remove(objuri);
		return true;
	}
	
	public ArrayList<String> getListofCompositeEntityNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (KBCompositeObject<DBCompositeEntity> pce : physicalCompositeEntities) {
			names.add(pce.getName());
		}
		
		return names;
	}
	
	public boolean isKBCompositeModifiable(int index) {
		return physicalCompositeEntities.get(index).isModifiable();
	}
	
	public ArrayList<CompBioModel> getModels() {
		ArrayList<CompBioModel> models = new ArrayList<CompBioModel>();
		for (KBBufferObject<CompBioModel> obj : BioModels) {
			models.add(obj.getComponent());
		}
		return models;
	}
	
	public ArrayList<ReferencePhysicalEntity> getReferenceEntities() {
		ArrayList<ReferencePhysicalEntity> rpes = new ArrayList<ReferencePhysicalEntity>();
		for (KBBufferObject<ReferencePhysicalEntity> obj : PhysicalReferenceEntities) {
			rpes.add(obj.getComponent());
		}
		return rpes;
	}
	
	public ArrayList<PhysicalProperty> getProperties() {
		ArrayList<PhysicalProperty> properties = new ArrayList<PhysicalProperty>();
		for (KBBufferObject<PhysicalProperty> obj : PhysicalProperties) {
			properties.add(obj.getComponent());
		}
		return properties;
	}
	
	public ArrayList<DBCompositeEntity> getComposites() {
		ArrayList<DBCompositeEntity> comps = new ArrayList<DBCompositeEntity>();
		for (KBBufferObject<DBCompositeEntity> obj : physicalCompositeEntities) {
			comps.add(obj.getComponent());
		}
		return comps;
	}
	
	public PhysicalEntity getPhysicalEntitybyURI(URI uri) {
		PhysicalEntity pe = this.getRefEntitybyURI(uri);
		if (pe==null) {
			pe = this.getCompositeEntitybyURI(uri);
		}
		return pe;
	}
	
	public ComponentStatus getModelStatusbyURI(URI moduri) {
		return URIandCBMmap.get(moduri).getStatus();
	}
	
	public ComponentStatus getPropertyStatusbyURI(URI ppuri) {
		return URIandPPmap.get(ppuri).getStatus();
	}
	
	public ComponentStatus getCompositeEntityStatusbyURI(URI cpeuri) {
		return URIandDBCmap.get(cpeuri).getStatus();
	}
	
	public ComponentStatus getPhysicalEntityStatusbyURI(URI uri) {
		if (URIandRPEmap.containsKey(uri)){
			return URIandRPEmap.get(uri).getStatus();
		}
		return URIandDBCmap.get(uri).getStatus();
	}
	
	public CompBioModel getModelbyURI(URI moduri) {
		if (!hasModel(moduri)) return null;
		return URIandCBMmap.get(moduri).getComponent();
	}
	
	public PhysicalProperty getPropertybyURI(URI ppuri) {
		if (!hasProperty(ppuri)) return null;
		return URIandPPmap.get(ppuri).getComponent();
	}

	public DBCompositeEntity getCompositeEntitybyURI(URI cpeuri) {
		if (!hasKBComposite(cpeuri)) return null;
		return URIandDBCmap.get(cpeuri).getComponent();
	}
	
	public KBCompositeObject<DBCompositeEntity> getCompositeEntity(int cpeuri) {
		return physicalCompositeEntities.get(cpeuri);
	}
	
	public KBCompositeObject<DBCompositeEntity> getKBCompositeObject(URI cpeuri) {
		return URIandDBCmap.get(cpeuri);
	}
	
	public DBCompositeEntity getKBComposite(Pair<PhysicalEntity, PhysicalEntity> pepair, StructuralRelation sr) {
		for (DBCompositeEntity dbc : getComposites()) {
			if (dbc.componentEntityListsMatch(pepair)) return dbc;
		}
		return null;
	}
	
	public ArrayList<KBCompositeObject<DBCompositeEntity>> getAllKBCompositeObjects() {
		return physicalCompositeEntities;
	}
	
	public Set<URI> getCompositeURIs() {
		return URIandDBCmap.keySet();
	}
	
	public int getIndexofComposite(URI uri) {
		return physicalCompositeEntities.indexOf(URIandDBCmap.get(uri));
	}
}

