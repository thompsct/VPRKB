package semsimKB.utilities.descriptors;

import java.net.URI;
import java.util.ArrayList;

import org.apache.commons.lang3.tuple.Pair;

import semsimKB.annotation.StructuralRelation;
import semsimKB.model.CompBioModel;
import semsimKB.model.kbbuffer.KBCompositeObject;
import semsimKB.model.physical.DBCompositeEntity;
import semsimKB.model.physical.PhysicalProperty;
import vprExplorer.buffer.ComponentStatus;

public class KBCompositeEditor {
	String name;
	URI uri;
	String description;
	ComponentStatus status;
	ArrayList<String[]> components = new ArrayList<String[]>();
	ArrayList<String> compuris = new ArrayList<String>();
	ArrayList<PropertyModelGroup> pmglist = new ArrayList<PropertyModelGroup>();
	Pair<ComponentStatus, ComponentStatus> cpntstats;
	
	
	public KBCompositeEditor(KBCompositeObject<DBCompositeEntity> kdbc, Pair<ComponentStatus, ComponentStatus> componentstatuses) {
		DBCompositeEntity dbc = kdbc.getComponent();
		name = dbc.getName();
		uri = dbc.getURI();
		description = dbc.getDescription();
		cpntstats = componentstatuses;
		status = kdbc.getStatus();
				
		components.add(new String[]{StructuralRelation.SUBCOMPONENT_RELATION.getShortDescription(), dbc.getComponentFullNames().getLeft()});
		compuris.add(dbc.getComponentURIs().getLeft().toString());
		if (dbc.getRelation()!=null) {
			components.add(new String[]{dbc.getRelation().getShortDescription(), dbc.getComponentNames().getRight()});
			compuris.add(dbc.getComponentURIs().getRight().toString());
		}
		int i = 0;
		for (PhysicalProperty pp : dbc.getPropertyList()) {
			PropertyModelGroup pmg = new PropertyModelGroup(pp, kdbc.getPropertyStatus(i));
			
			for (CompBioModel cbm : dbc.getPPModelAssociationList(i)) {
				pmg.addModel(cbm);
			}
			pmglist.add(pmg);
			i++;
		}
	}
	public String getName() {
		return name;
	}
	
	public final URI getURI() {
		return uri;
	}
	
	public final ArrayList<String[]> getComponents() {
		return components;
	}
	
	public final ArrayList<Pair<String, ComponentStatus>> getProperties() {
		ArrayList<Pair<String, ComponentStatus>> pmgs = new ArrayList<Pair<String, ComponentStatus>>();
		for (PropertyModelGroup pmg : pmglist) {
			pmgs.add(pmg.propstat);
		}
		return pmgs;
	}

	public final ArrayList<String> getAssociatedModels(int index) {
		return pmglist.get(index).modstat;
	}
	
	public ComponentStatus getFirstComponentStatus() {
		return cpntstats.getLeft();
	}
	
	public ComponentStatus getSecondComponentStatus() {
		return cpntstats.getRight();
	}
	
	public ComponentStatus getStatus() {
		return status;
	}
	
	public String getAdditionalInformation(int index) {
		return compuris.get(index);
	}
	
	private class PropertyModelGroup {
		Pair<String, ComponentStatus> propstat;
		ArrayList<String> modstat = new ArrayList<String>();
		
		protected PropertyModelGroup(PhysicalProperty pp, ComponentStatus status) {
			propstat = Pair.of(pp.getName(), status);
		}
		
		protected void addModel(CompBioModel model) {
			modstat.add(model.getName());
		}
	};
}
