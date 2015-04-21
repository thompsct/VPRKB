package semsimKB.model.physical;

import java.net.URI;

import org.apache.commons.lang3.tuple.Pair;

import semsimKB.SemSimKBConstants;
import semsimKB.annotation.StructuralRelation;

public class DBCompositeEntity extends DBPhysicalComponent implements PhysicalEntity{
		private Pair<PhysicalEntity, PhysicalEntity> componententities = null;
		protected StructuralRelation relation  = StructuralRelation.PART_OF_RELATION;
		
		public DBCompositeEntity(Pair<PhysicalEntity,PhysicalEntity> entitiestoadd, StructuralRelation rel) {
			componententities = entitiestoadd;
			relation = rel;
			createName();
		}

		public DBCompositeEntity(URI uri, String label) {
			super(uri, label);
		}
		
		private void createName() {
				Pair<String, String> names =  getComponentNames();
				String[] pe1name = names.getLeft().split(" ");
				String[] pe2name = names.getRight().split(" ");

				int index = 0;
				for (int i= 0; i < pe2name.length; i++) {
					if (pe1name[pe1name.length-1].equalsIgnoreCase(pe2name[i])) {
						index = i+1;
						break;
					}
				}
				if (pe2name[index].equalsIgnoreCase("part")) index = index+2;
				else if (pe2name[index].equalsIgnoreCase("in")) index = index+1;
				
				String pe2 = "";
				for (int i = index; i < pe2name.length; i++) {
					pe2 = pe2 + pe2name[i] + " ";
				}
				String rel = " part of ";
				if (relation==StructuralRelation.CONTAINED_IN_RELATION) {
					rel = " in ";
				}
				setName(names.getLeft() + rel + pe2 );
		}
		
		public Pair<URI, URI> getComponentURIs() {
			return Pair.of(componententities.getLeft().getURI(), componententities.getRight().getURI());
		}
		
		public Pair<String, String> getComponentNames() {
			return Pair.of(componententities.getLeft().getFullName(), componententities.getRight().getFullName());
		}
		
		
		public boolean equals(DBCompositeEntity dbc) {
			return componentEntityListsMatch(dbc.componententities);
		}
		
		public StructuralRelation getRelation() {
				return relation;
		}

		public void setRelation(StructuralRelation r) {
			relation = r;
		}
		
		public boolean componentEntityListsMatch(Pair<PhysicalEntity, PhysicalEntity> components) {
			return (components.getLeft().getURI().equals(componententities.getLeft().getURI()) &&
					components.getRight().getURI().equals(componententities.getRight().getURI()));
			
		}
		
		public void setComponents(Pair<PhysicalEntity, PhysicalEntity> cmptpair) {
			if (componententities == null) {
				componententities = cmptpair;
			}
		}
		
		public final Pair<PhysicalEntity, PhysicalEntity> getComponents() {
			return componententities;
		}
		
		public String getFullName() {
			return getName();
		}
		
		@Override
		public  URI getClassURI() {
			return SemSimKBConstants.KB_COMPOSITE_CLASS_URI;
		}

}
