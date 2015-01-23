package semsimKB.model.physical;

import java.net.URI;
import java.util.LinkedList;

import semsim.model.physical.PhysicalEntity;
import semsim.model.physical.PhysicalModelComponent;
import semsimKB.SemSimKBConstants;
import semsimKB.SemSimKBConstants.kbcomptype;
import semsimKB.annotation.StructuralRelation;

public class DBCompositeEntity extends DBPhysicalComponent {
		protected LinkedList<StructuralRelation> rels;
		protected SemSimKBConstants.kbcomptype type;
		
		public DBCompositeEntity(PhysicalModelComponent entitytoAdd, kbcomptype t) {
			super(entitytoAdd);
			
			rels=new LinkedList<StructuralRelation>();
			type = t;
			if (type.equals(kbcomptype.SINGULAR)) {
				addComponent(entitytoAdd);
				setURI(entitytoAdd.getURI());
			}
			else {
				CompositePhysicalEntity cpe = (CompositePhysicalEntity)entitytoAdd;
				LinkedList<PhysicalEntity> complist = new LinkedList<PhysicalEntity>();
				for (StructuralRelation rel : cpe.getArrayListOfStructuralRelations() ) {
					rels.add(rel);
				}
				for (PhysicalEntity rpe : cpe.getArrayListOfEntities()) {
					addComponent(rpe.getURI());	
					complist.add(rpe);
				}
				//Create name from components
				String name = (String)SemSimKBConstants.VPR_NAMESPACE.subSequence(0, SemSimKBConstants.VPR_NAMESPACE.length()-1);
				if (type.equals(kbcomptype.COMPLEX)) {
					
					String first = complist.getFirst().getName();
					String last = complist.getLast().getName();
					String toignore = ((CompositePhysicalEntity)complist.getFirst()).getArrayListOfEntities().get(1).getName();
					cpe.setName(first + last.replace(toignore, ""));
					setName(cpe.getName());
				}
					
				name = name + '/' + getName().replace(' ', '_');
				setURI(URI.create(name));
			}
		}

		
		public DBCompositeEntity(URI uri, kbcomptype t) {
			super(uri);
			type = t;
			components=new LinkedList<URI>();
			rels=new LinkedList<StructuralRelation>();
		}
				
		public SemSimKBConstants.kbcomptype getType() { 
			return type;
		}
		
		public StructuralRelation getRelation(int i) {
				return rels.get(i);
		}
		
		public LinkedList<StructuralRelation> getRelations() {
			return rels;
		}
		
		public void addRelation(StructuralRelation rel) {
			rels.add(rel);
		}

		@Override
		public  URI getClassURI() {
			return SemSimKBConstants.KB_PHYSICAL_ENTITY_CLASS_URI;
		}

}
