package vprExplorer.buffer;

//This class provides a local buffer for information pulled from the database or
//information being edited or added before being pushed to the database

import java.util.ArrayList;
import java.util.Date;
import java.net.URI;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import semsimKB.SemSimKBConstants;
import semsimKB.SemSimKBConstants.modelAnnotations;
import semsimKB.annotation.StructuralRelation;
import semsimKB.model.CompBioModel;
import semsimKB.model.ModelLite;
import semsimKB.model.kbbuffer.KBCompositeObject;
import semsimKB.model.kbbuffer.KnowledgeBase;
import semsimKB.model.physical.CompositePhysicalEntity;
import semsimKB.model.physical.DBCompositeEntity;
import semsimKB.model.physical.PhysicalEntity;
import semsimKB.model.physical.PhysicalProperty;
import semsimKB.model.physical.ReferencePhysicalEntity;
import vprExplorer.Settings;
import vprExplorer.knowledgebaseinterface.KnowledgeBaseInterface;
import vprExplorer.knowledgebaseinterface.LocalKnowledgeBase;
import vprExplorer.knowledgebaseinterface.RemoteKnowledgeBaseInterface;

public class KBBufferOperations {
	private KnowledgeBase buffer = new KnowledgeBase();
	private KnowledgeBaseInterface kbinterface;
	private ArrayList<KBCompositeObject<DBCompositeEntity>> comppelist = new ArrayList<KBCompositeObject<DBCompositeEntity>>();
	private ArrayList<KBCompositeObject<DBCompositeEntity>> modifylist = new ArrayList<KBCompositeObject<DBCompositeEntity>>();
	
	protected ModelLite model;
	CompBioModel localdbmodel;
	
	private int dbcint = 0;
	private Date date = new Date();
		
	public KBBufferOperations(Settings sets, ModelLite curmod) {
		model = curmod;
		if (sets.getService()==Settings.service._FILE) {
			kbinterface = new LocalKnowledgeBase(buffer);
		}
		else kbinterface = new RemoteKnowledgeBaseInterface(sets, buffer);
	}
	
	//Load all reference components from the model
	public void compareModeltoKB(ArrayList<CompositePhysicalEntity> cpes) {
		//Check if model already in KB
		URI moduri = URI.create(SemSimKBConstants.VPR_NAMESPACE + model.getName());
		if (kbinterface.getElementwithURI(moduri, false)) {
			localdbmodel = new CompBioModel(model);
			localdbmodel.setURI(moduri);
			buffer.addModel(localdbmodel, ComponentStatus.MISSING);
		}
		
		compareComposites(cpes);
	}
	
	private void compareComposites(ArrayList<CompositePhysicalEntity> cpes) {
		for (CompositePhysicalEntity cpe : cpes) {
			ArrayList<PhysicalEntity> cents = cpe.getArrayListOfEntities();
			ArrayList<StructuralRelation> rels = cpe.getArrayListOfStructuralRelations();
			KBCompositeObject<DBCompositeEntity> dbc = checkforComposite(cents, rels);
			comppelist.add(dbc);
			if (dbc!=null) {
				dbc.addProperties(cpe.getPhysicalProperties(), buffer.getModelbyURI(localdbmodel.getURI()));
			
				compareComponents(dbc, cpe);
			}
		}
	}
	
	private KBCompositeObject<DBCompositeEntity> checkforComposite(ArrayList<PhysicalEntity> cpes, ArrayList<StructuralRelation> rels) {
		ArrayList<PhysicalEntity> dbcs = new ArrayList<PhysicalEntity>();
		
		for (int i = 0; i < cpes.size()-1; i++) {
			DBCompositeEntity dbc = kbinterface.retrieveComposite(Pair.of(cpes.get(i).getURI(), cpes.get(i+1).getURI()), rels.get(i));
			if (dbc !=null) {
				dbcs.add(dbc);
			}
		}
		if (dbcs.size() > 2) {
			ArrayList<StructuralRelation> dbcrels = new ArrayList<StructuralRelation>();
			for (int i = 1; i<rels.size(); i++) {
				dbcrels.add(rels.get(i));
			}
			checkforComposite(dbcs, dbcrels);
		}
		else {
			DBCompositeEntity dbc = kbinterface.retrieveComposite(Pair.of(cpes.get(0).getURI(), cpes.get(1).getURI()), rels.get(0));
			if (dbc == null) return null;
			return buffer.getKBCompositeObject(dbc.getURI());
		}
		return null;
	}
	
	public void modifyComposite(int index, CompositePhysicalEntity cpe) {
		if (comppelist.get(index)!= null) {
			if (buffer.getCompositeEntityStatusbyURI(comppelist.get(index).getURI()) == ComponentStatus.MISSING) return;
			compareComponents(comppelist.get(index), cpe);
		}
		else {
			comppelist.set(index, parseComposite(cpe.getArrayListOfEntities(), cpe.getArrayListOfStructuralRelations()));
		}
	}
	
	private KBCompositeObject<DBCompositeEntity> parseComposite(ArrayList<PhysicalEntity> cpes, ArrayList<StructuralRelation> rels) {
		for (PhysicalEntity pe : cpes) {
			if (pe.getURI()==SemSimKBConstants.REFERENCE_PHYSICAL_ENTITY_CLASS_URI) {
				addReferenceEntitytoBuffer((ReferencePhysicalEntity) pe);
			}
		}
		if (cpes.size() > 2) {
			ArrayList<PhysicalEntity> dbcs = new ArrayList<PhysicalEntity>();

			for (int i = 0; i < cpes.size()-1; i++) {
				DBCompositeEntity dbc = kbinterface.retrieveComposite(Pair.of(cpes.get(i).getURI(), cpes.get(i+1).getURI()), rels.get(i));
				if (dbc ==null) {
					dbc = createComposite(Pair.of(cpes.get(i), cpes.get(i+1)), rels.get(i)).getComponent();
				}
				dbcs.add(dbc);
			}

			ArrayList<StructuralRelation> dbcrels = new ArrayList<StructuralRelation>();
			for (int i = 1; i<rels.size(); i++) {
				dbcrels.add(rels.get(i));
			}
			return parseComposite(dbcs, dbcrels);

		}
		return createComposite(Pair.of(cpes.get(0), cpes.get(1)), rels.get(0));
	}
	
	private KBCompositeObject<DBCompositeEntity> createComposite(Pair<PhysicalEntity, PhysicalEntity> cpes,StructuralRelation rel) {
		DBCompositeEntity dbc = new DBCompositeEntity(cpes, rel);
		dbc.setURI(createURIforComponent());
		dbc.setRelation(rel);
		
		KBCompositeObject<DBCompositeEntity> kdbc = new KBCompositeObject<DBCompositeEntity>(dbc, ComponentStatus.MISSING);
		buffer.addComposite(kdbc);
		modifylist.add(kdbc);
		return kdbc;
	}
	
	//Compare similarity of composite properties
	private void compareComponents(KBCompositeObject<DBCompositeEntity> kbelement, CompositePhysicalEntity pmcElement) {
		DBCompositeEntity dbc = kbelement.getComponent();
		ComponentStatus status = ComponentStatus.EXACT_MATCH;
		//Compare database composite entity to model instance
		for (PhysicalProperty pp : pmcElement.getPhysicalProperties()) {
			if (!dbc.containsProperty(pp)) {
				addPropertytoBuffer(pp);
				status = ComponentStatus.NEW_ASSOCIATED_PHYS_PROPERTY;
				break;
			}
			if (!dbc.getPropertyModelList(pp).contains(localdbmodel)) {
				status = ComponentStatus.NEW_PROPERTY_MODEL_ASSOCIATION;
			}
		}
		kbelement.changeStatus(status);
	}
	
	private void addPropertytoBuffer(PhysicalProperty pp){
		if (!kbinterface.getElementwithURI(pp, true)) {
			buffer.addPhysicalProperty(pp, ComponentStatus.MISSING);
		}
	}
	
	private void addReferenceEntitytoBuffer(ReferencePhysicalEntity rpe){
		if (!kbinterface.getElementwithURI(rpe, true)) {
			buffer.addReferencePhysicalEntity(rpe, ComponentStatus.MISSING);
		}
	}
	
	//Add composite to KB or change existing component
	public void changeComposite(KBCompositeObject<DBCompositeEntity> kbelement, CompositePhysicalEntity pmcElement) {
		DBCompositeEntity dbc = kbelement.getComponent();
		//Compare database composite entity to model instance
		for (PhysicalProperty pp : pmcElement.getPhysicalProperties()) {
			if (!dbc.containsProperty(pp)) {
				dbc.addProperty(pp, localdbmodel);
				continue;
			}
			dbc.addModeltoProperty(pp, localdbmodel);
		}
		modifylist.add(kbelement);
	}
	
	private URI createURIforComponent() {
		Date newdate = new Date();
		String name = SemSimKBConstants.VPR_NAMESPACE + newdate.getTime();
		if (!newdate.after(date)) {
			name = name + String.valueOf(dbcint);
		}
		else {
			date = newdate;
			dbcint =0;
		}
		return URI.create(name);
		
	}
	
	public boolean removeBufferComponent(int index) {
		return (buffer.removeComposite(index));
	}
	
	public void addModelEdit(modelAnnotations field, String edit) {
		CompBioModel cbm = buffer.getModelbyURI(model.getURI());
		switch (field) {
			case CELLML_MODEL_URL_URI:
				cbm.addModelURL(SemSimKBConstants.CELLML_MODEL_URL_URI, edit);
				break;
			case JSIM_MODEL_URL_URI:
				cbm.addModelURL(SemSimKBConstants.JSIM_MODEL_URL_URI, edit);
				break;
			case MATLAB_MODEL_URL_URI:
				cbm.addModelURL(SemSimKBConstants.MATLAB_MODEL_URL_URI, edit);
				break;
			default:
				break;
		}
	}
	
	public ArrayList<Triple<String, ComponentStatus, Boolean>> getAllBufferCompositesNamesandStatuses() {
		
		ArrayList<Triple<String, ComponentStatus, Boolean>> namestatlist = new ArrayList<Triple<String, ComponentStatus, Boolean>>();

		for ( KBCompositeObject<DBCompositeEntity> kdce : buffer.getAllKBCompositeObjects()) {
			namestatlist.add(Triple.of(kdce.getName(), kdce.getStatus(), modifylist.contains(kdce)));
		}
		return namestatlist;
	}
	
	public String getCompositeName(int index) {
		return comppelist.get(index).getName();
	}
	
	public ComponentStatus getCompositeStatus(int index) {
		return comppelist.get(index).getStatus();
	}
	
	public Boolean getCompositetobeModified(int index) {		
		return modifylist.contains(comppelist.get(index));
	}
	
	public void pushBuffertoDatabase() {
		kbinterface.pushChangestoDatabase(localdbmodel.getURI(), modifylist);
	}
}
