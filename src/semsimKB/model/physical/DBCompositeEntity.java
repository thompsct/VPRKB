package semsimKB.model.physical;

import java.net.URI;
import java.util.ArrayList;

import org.apache.commons.lang3.tuple.Pair;

import semsimKB.SemSimKBConstants;
import semsimKB.annotation.StructuralRelation;

public class DBCompositeEntity extends DBPhysicalComponent implements PhysicalEntity{
		private Pair<PhysicalEntity, PhysicalEntity> componententities = null;
		protected StructuralRelation relation  = SemSimKBConstants.PART_OF_RELATION;
		
		public DBCompositeEntity(Pair<PhysicalEntity,PhysicalEntity> entitiestoadd, StructuralRelation rel) {
			componententities = entitiestoadd;
			relation = rel;
			createName();
		}

		public DBCompositeEntity(URI uri) {
			super(uri);
		}
		
		private void createName() {
				Pair<String, String> names =  getComponentNames();
				String[] pe1name = names.getLeft().split(" ");
				ArrayList<String> pe2name = new ArrayList<String>();
				for (String s : names.getRight().split(" ")) {
						pe2name.add(s);
				}
				
				for (int i = 1; i<pe1name.length; i++) {
					if (pe1name[pe1name.length-i].equals(pe2name.get(i))) {
						pe2name.remove(i);
					}
					else break;
				}
				String pe2 = "";
				for (String s : pe2name) {
					pe2 = pe2 + " " + s;
				}
				String rel = " part of";
				if (getRelation()==SemSimKBConstants.CONTAINED_IN_RELATION) {
					rel = " contained in";
				}
				setName(names.getLeft() + rel + pe2);
		}
		
		public Pair<URI, URI> getComponentURIs() {
			return Pair.of(componententities.getLeft().getURI(), componententities.getRight().getURI());
		}
		
		public Pair<String, String> getComponentNames() {
			return Pair.of(componententities.getLeft().getName(), componententities.getRight().getName());
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

		@Override
		public  URI getClassURI() {
			return SemSimKBConstants.KB_COMPOSITE_CLASS_URI;
		}

}
