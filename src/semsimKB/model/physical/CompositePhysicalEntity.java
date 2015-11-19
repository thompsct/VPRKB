package semsimKB.model.physical;

import java.util.ArrayList;

import semsimKB.annotation.StructuralRelation;
import semsimKB.model.SemSimTypes;

public class CompositePhysicalEntity extends PhysicalModelComponent implements Comparable<CompositePhysicalEntity>, PhysicalEntity{
	
	private ArrayList<ReferencePhysicalEntity> arrayListOfPhysicalEntities;
	private ArrayList<StructuralRelation> arrayListOfStructuralRelations;

	public CompositePhysicalEntity(ArrayList<ReferencePhysicalEntity> ents, ArrayList<StructuralRelation> rels){
		super(SemSimTypes.COMPOSITE_PHYSICAL_ENTITY);
		if(ents.size()-1 != rels.size()){
			System.err.println("Error constructing composite physical entity: " +
					"length of relations array (" + rels.size() + 
					") must be one less than entity array (" + ents.size() + ").");
		}
		else{
			setArrayListOfEntities(ents);
			setArrayListOfStructuralRelations(rels);
		}
		setName(makeName());
		setDescription(getName());
	}
	
	public String makeName(){
		String name = new String("");
		if(getArrayListOfEntities().size()>0) name = "";
		for(int x=0; x<getArrayListOfEntities().size(); x++){
			PhysicalEntity ent = getArrayListOfEntities().get(x);
			name = name + ent.getName();
			
			if(x<getArrayListOfEntities().size()-1)	name = name + " in ";

		}
		return name;
	}

	public void setArrayListOfEntities(ArrayList<ReferencePhysicalEntity> arrayListOfEntities) {
		this.arrayListOfPhysicalEntities = arrayListOfEntities;
	}

	public ArrayList<ReferencePhysicalEntity> getArrayListOfEntities() {
		return arrayListOfPhysicalEntities;
	}

	public void setArrayListOfStructuralRelations(
			ArrayList<StructuralRelation> arrayListOfStructuralRelations) {
		this.arrayListOfStructuralRelations = arrayListOfStructuralRelations;
	}

	public ArrayList<StructuralRelation> getArrayListOfStructuralRelations() {
		return arrayListOfStructuralRelations;
	}

	
	public int compareTo(CompositePhysicalEntity that) {
		if(this.arrayListOfPhysicalEntities.size()==that.arrayListOfPhysicalEntities.size() &&
				this.arrayListOfStructuralRelations.size()==that.arrayListOfStructuralRelations.size()){
			// Test physical entity equivalence
			for(int i=0;i<getArrayListOfEntities().size(); i++){
				if(this.getArrayListOfEntities().get(i)!=that.getArrayListOfEntities().get(i)){
					return 1;
				}
			}
			// Test structural relation equivalence
			for(int i=0; i<getArrayListOfStructuralRelations().size(); i++){
				if(this.getArrayListOfStructuralRelations().get(i)!=that.getArrayListOfStructuralRelations().get(i)){
					return 1;
				}
			}
			return 0;
		}
		// Else the arrays were different sizes
		return 1;
	}
	
	public String getFullName() {
		return getName();
	}
	
	@Override
	protected boolean isEquivalent(Object obj) {
		return compareTo((CompositePhysicalEntity)obj)==0;
	}
}
