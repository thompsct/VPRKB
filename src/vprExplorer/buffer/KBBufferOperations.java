package vprExplorer.buffer;

//This class provides a local buffer for information pulled from the database or
//information being edited or added before being pushed to the database

import java.util.ArrayList;
import java.util.Date;
import java.net.URI;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import semsimKB.SemSimKBConstants;
import semsimKB.annotation.StructuralRelation;
import semsimKB.model.CompBioModel;
import semsimKB.model.ModelLite;
import semsimKB.model.SemSimTypes;
import semsimKB.model.kbbuffer.KBCompositeObject;
import semsimKB.model.kbbuffer.KnowledgeBase;
import semsimKB.model.physical.CompositePhysicalEntity;
import semsimKB.model.physical.DBCompositeEntity;
import semsimKB.model.physical.PhysicalEntity;
import semsimKB.model.physical.PhysicalProperty;
import semsimKB.model.physical.ReferencePhysicalEntity;
import semsimKB.utilities.descriptors.KBCompositeEditor;
import semsimKB.utilities.descriptors.KBModelEditor;
import vprExplorer.Settings;
import vprExplorer.knowledgebaseinterface.KnowledgeBaseInterface;
import vprExplorer.knowledgebaseinterface.LocalKnowledgeBase;
import vprExplorer.knowledgebaseinterface.RemoteKnowledgeBaseInterface;

public class KBBufferOperations {
	private KnowledgeBase buffer = new KnowledgeBase();
	private KnowledgeBaseInterface kbinterface;
	private ArrayList<KBCompositeObject<DBCompositeEntity>> comppelist;
	private ArrayList<KBCompositeObject<DBCompositeEntity>> modifylist = new ArrayList<KBCompositeObject<DBCompositeEntity>>();
	
	protected ModelLite model;
	private CompBioModel localdbmodel;
	
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
		if (!kbinterface.getElementwithURI(moduri, false)) {
			localdbmodel = new CompBioModel(model);
			localdbmodel.setURI(moduri);
			buffer.addModel(localdbmodel, ComponentStatus.MISSING);
		}
		else {
			localdbmodel = buffer.getModelbyURI(moduri);
		}
		for (PhysicalProperty pp : model.getPhysicalProperties()) {
			if (kbinterface.getElementwithURI(pp.getURI(), true)) {
				buffer.addPhysicalProperty(pp, ComponentStatus.EXACT_MATCH);
			}
		}
		for (ReferencePhysicalEntity rpe : model.getReferenceEntities()) {
			if (kbinterface.getElementwithURI(rpe.getURI(), true)) {
				buffer.addReferencePhysicalEntity(rpe, ComponentStatus.EXACT_MATCH);
			}
		}
		
		compareComposites(cpes);
	}
	
	public void compareComposites(ArrayList<CompositePhysicalEntity> cpes) {
		 comppelist = new ArrayList<KBCompositeObject<DBCompositeEntity>>();
		for (CompositePhysicalEntity cpe : cpes) {
			ArrayList<PhysicalEntity> cents = new ArrayList<PhysicalEntity>();
			for (ReferencePhysicalEntity rpe : cpe.getArrayListOfEntities()) {
				cents.add(rpe);
			}
			ArrayList<StructuralRelation> rels = cpe.getArrayListOfStructuralRelations();
			
			KBCompositeObject<DBCompositeEntity> dbc = checkforComposite(cents, rels);
			
			comppelist.add(dbc);
			if (dbc!=null) {
				buffer.addComposite(dbc);
				compareCompositeAssociations(dbc, cpe);
			}
		}
	}
	private KBCompositeObject<DBCompositeEntity> checkforComposite(ArrayList<PhysicalEntity> cpes, ArrayList<StructuralRelation> rels) {
		if (cpes.size() > 2) {
			ArrayList<PhysicalEntity> dbcs = new ArrayList<PhysicalEntity>();
			for (int i = 0; i < cpes.size()-1; i++) {
				DBCompositeEntity dbc = kbinterface.retrieveComposite(Pair.of(cpes.get(i).getURI(), cpes.get(i+1).getURI()), rels.get(i));
				if (dbc !=null) {
					dbcs.add(dbc);
				}
				else return null;
			}
			ArrayList<StructuralRelation> dbcrels = new ArrayList<StructuralRelation>();
			for (int i = 1; i<rels.size(); i++) {
				dbcrels.add(rels.get(i));
			}
			return checkforComposite(dbcs, dbcrels);
		}
		else {
			URI peuri1 = cpes.get(0).getURI();
			URI peuri2 = null;
			StructuralRelation rel = null;
			if (!rels.isEmpty()) {
				rel = rels.get(0);
				peuri2 = cpes.get(1).getURI();
			}
			DBCompositeEntity dbc = kbinterface.retrieveComposite(Pair.of(peuri1, peuri2), rel);
			if (dbc == null) return null;
			return buffer.getKBCompositeObject(dbc.getURI());
		}
	}
	
	public void compareBufferComposites(ArrayList<CompositePhysicalEntity> cpes) {
		 comppelist = new ArrayList<KBCompositeObject<DBCompositeEntity>>();
		for (CompositePhysicalEntity cpe : cpes) {
			ArrayList<PhysicalEntity> cents = new ArrayList<PhysicalEntity>();
			for (ReferencePhysicalEntity rpe : cpe.getArrayListOfEntities()) {
				cents.add(rpe);
			}
			ArrayList<StructuralRelation> rels = cpe.getArrayListOfStructuralRelations();
			KBCompositeObject<DBCompositeEntity> dbc = checkforCompositeinBuffer(cents, rels);
			
			comppelist.add(dbc);
		}
	}

	private KBCompositeObject<DBCompositeEntity> checkforCompositeinBuffer(ArrayList<PhysicalEntity> cpes, ArrayList<StructuralRelation> rels) {
		if (cpes.size() > 2) {
			ArrayList<PhysicalEntity> dbcs = new ArrayList<PhysicalEntity>();
			for (int i = 0; i < cpes.size()-1; i++) {
				DBCompositeEntity dbc = buffer.getKBComposite(Pair.of(cpes.get(i), cpes.get(i+1)), rels.get(i));
				if (dbc !=null) {
					dbcs.add(dbc);
				}
				else return null;
			}
			ArrayList<StructuralRelation> dbcrels = new ArrayList<StructuralRelation>();
			for (int i = 1; i<rels.size(); i++) {
				dbcrels.add(rels.get(i));
			}
			return checkforComposite(dbcs, dbcrels);
		}
		else {
			DBCompositeEntity dbc;
			if (!rels.isEmpty()) {
				dbc = buffer.getKBComposite(Pair.of(cpes.get(0), cpes.get(1)), rels.get(0));
			}
			else {
				dbc = buffer.getKBComposite(Pair.of(cpes.get(0), null), null);
			}
			if (dbc == null) return null;
			return buffer.getKBCompositeObject(dbc.getURI());
		}
	}
	
	//Compare similarity of composite properties
	private void compareCompositeAssociations(KBCompositeObject<DBCompositeEntity> kbelement, CompositePhysicalEntity pmcElement) {
		if (kbelement.getStatus() == ComponentStatus.MISSING) return;
		DBCompositeEntity dbc = kbelement.getComponent();
		ComponentStatus status = ComponentStatus.EXACT_MATCH;
		//Compare database composite entity to model instance
		int i = 0;
		for (PhysicalProperty pp : pmcElement.getPhysicalProperties()) {
			if (!dbc.containsProperty(pp)) {
				status = ComponentStatus.NEW_ASSOCIATED_PHYS_PROPERTY;
				break;
			}
			if (!dbc.getPropertyModelList(i).contains(localdbmodel)) {
				status = ComponentStatus.NEW_PROPERTY_MODEL_ASSOCIATION;
			}
			i++;
		}
		kbelement.changeStatus(status);
	}
	
	public boolean modifyComposite(int index, CompositePhysicalEntity cpe) {
		KBCompositeObject<DBCompositeEntity> kdce = comppelist.get(index);
		//If the SemSim composite already has a corresponding kb composite in the buffer,
		//check if it is eligible to be modified
		if (kdce!= null) {
			if (kdce.getStatus() == ComponentStatus.MISSING ||
					kdce.getStatus() == ComponentStatus.EXACT_MATCH ||
							kdce.getStatus() == ComponentStatus.EXTERNAL_TO_MODEL)  {
				return false;
			}
			
			importCPEAssociations(kdce, cpe);
			modifylist.add(kdce);
		}
		else {
			ArrayList<PhysicalEntity> pes = new ArrayList<PhysicalEntity>();
			for (ReferencePhysicalEntity rpe : cpe.getArrayListOfEntities()) {
				pes.add(rpe);
			}
			//If singular
			if (cpe.getArrayListOfStructuralRelations().isEmpty()) {
				if (pes.get(0).getType()==SemSimTypes.REFERENCE_PHYSICAL_ENTITY) {
					addReferenceEntitytoBuffer((ReferencePhysicalEntity) pes.get(0));
				}
				kdce = createComposite(Pair.of(pes.get(0), null), null);
			}
			else {
				kdce = parseComposite(pes, cpe.getArrayListOfStructuralRelations());
			}
			importCPEAssociations(kdce, cpe);
		}
		return true;
	}
	
	private KBCompositeObject<DBCompositeEntity> parseComposite(ArrayList<PhysicalEntity> cpes, ArrayList<StructuralRelation> rels) {
		//Verify Reference Entities exist in buffer
		for (PhysicalEntity pe : cpes) {
			if (pe.getType()==SemSimTypes.REFERENCE_PHYSICAL_ENTITY) {
				addReferenceEntitytoBuffer((ReferencePhysicalEntity) pe);
			}
		}
		//If the number of subcomponents exceeds 2, create two composites for every three.
		if (cpes.size() > 2) {
			ArrayList<PhysicalEntity> dbcs = new ArrayList<PhysicalEntity>();

			for (int i = 0; i < cpes.size()-1; i++) {
				DBCompositeEntity dbc = buffer.getKBComposite(Pair.of(cpes.get(i), cpes.get(i+1)), rels.get(i));
				if (dbc ==null) {
					dbc = createComposite(Pair.of(cpes.get(i), cpes.get(i+1)), rels.get(i)).getComponent();
				}
				dbcs.add(dbc);
			}

			ArrayList<StructuralRelation> dbcrels = new ArrayList<StructuralRelation>();
			for (int i = 1; i<rels.size(); i++) {
				dbcrels.add(rels.get(i));
			}
			//Parse the new list
			return parseComposite(dbcs, dbcrels);

		}
		//If there are only two physical entities, build composite and return

		return createComposite(Pair.of(cpes.get(0), cpes.get(1)), rels.get(0));
	}
	
	private KBCompositeObject<DBCompositeEntity> createComposite(Pair<PhysicalEntity, PhysicalEntity> cpes,StructuralRelation rel) {
		//Verify that the composite does not already exist, if it does, return the existing object.
		DBCompositeEntity dbc = buffer.getKBComposite(cpes, rel);
		if (dbc!=null) return buffer.getKBCompositeObject(dbc.getURI());
		
		dbc = new DBCompositeEntity(cpes, rel);
		dbc.setURI(createURIforComponent());
		if (rel!=null) {
			dbc.setRelation(rel);
		}
		
		
		KBCompositeObject<DBCompositeEntity> kdbc = new KBCompositeObject<DBCompositeEntity>(dbc, ComponentStatus.MISSING);
		buffer.addComposite(kdbc);
		modifylist.add(kdbc);
		return kdbc;
	}
	

	//Compare similarity of composite properties
	private void importCPEAssociations(KBCompositeObject<DBCompositeEntity> kbelement, CompositePhysicalEntity pmcElement) {
		DBCompositeEntity dbc = kbelement.getComponent();
		//Compare database composite entity to model instance
		for (PhysicalProperty pp : pmcElement.getPhysicalProperties()) {
			if (!dbc.containsProperty(pp)) {
				addPropertytoBuffer(pp);
			}
			kbelement.addProperty(pp, localdbmodel);
		}
	}
	
	private void addPropertytoBuffer(PhysicalProperty pp){
		if (!buffer.hasProperty(pp)) {
			buffer.addPhysicalProperty(pp, ComponentStatus.MISSING);
		}
	}
	
	private void addReferenceEntitytoBuffer(ReferencePhysicalEntity rpe){
		if (!buffer.hasReferenceEntity(rpe)) {
			buffer.addReferencePhysicalEntity(rpe, ComponentStatus.MISSING);
		}
	}
	
	private URI createURIforComponent() {
		Date newdate = new Date();
		String name = SemSimKBConstants.VPR_NAMESPACE + newdate.getTime();
		if (!newdate.after(date)) {
			name = name + String.valueOf(dbcint);
			dbcint++;
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
	
	public KBModelEditor createModelEditor() {
		return new KBModelEditor(localdbmodel, buffer.getModelStatusbyURI(localdbmodel.getURI()));
		
	}
	
	public KBCompositeEditor createCompositeEditor(int index) {
		KBCompositeObject<DBCompositeEntity> kdbc = buffer.getCompositeEntity(index);
		Pair<URI, URI> pps = kdbc.getComponent().getComponentURIs();
		return new KBCompositeEditor(kdbc, 
				Pair.of(buffer.getPhysicalEntityStatusbyURI(pps.getLeft()), buffer.getPhysicalEntityStatusbyURI(pps.getRight())));
	}
}
