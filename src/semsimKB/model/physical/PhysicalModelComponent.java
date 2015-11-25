package semsimKB.model.physical;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import semsimKB.Annotatable;
import semsimKB.annotation.Annotation;
import semsimKB.annotation.ReferenceOntologyAnnotation;
import semsimKB.definitions.SemSimRelation;
import semsimKB.definitions.SemSimTypes;
import semsimKB.model.SemSimComponent;

public abstract class PhysicalModelComponent extends SemSimComponent implements Annotatable {
	private Set<Annotation> annotations = new HashSet<Annotation>();
	protected Set<PhysicalProperty> physicalProperties = new HashSet<PhysicalProperty>();
	
	protected PhysicalModelComponent(SemSimTypes kbtype) {
		super(kbtype);
	}
	
	// Required by annotable interface:
	public Set<Annotation> getAnnotations() {
		return annotations;
	}
	
	public void setAnnotations(Set<Annotation> annset){
		annotations.clear();
		annotations.addAll(annset);
	}

	public void addAnnotation(Annotation ann) {
		annotations.add(ann);
	}
	
	public void addReferenceOntologyAnnotation(SemSimRelation.KBRelations relation, URI uri, String description){
		addAnnotation(new ReferenceOntologyAnnotation(relation, uri, description));
	}

	public Set<ReferenceOntologyAnnotation> getReferenceOntologyAnnotations(SemSimRelation.KBRelations relation) {
		Set<ReferenceOntologyAnnotation> raos = new HashSet<ReferenceOntologyAnnotation>();
		for(Annotation ann : getAnnotations()){
			if(ann instanceof ReferenceOntologyAnnotation && ann.getRelation()==relation){
				raos.add((ReferenceOntologyAnnotation)ann);
			}
		}
		return raos;
	}
	
	
	public ReferenceOntologyAnnotation getFirstRefersToReferenceOntologyAnnotation(){
		if(!getReferenceOntologyAnnotations(SemSimRelation.KBRelations.HAS_PHYSICAL_DEFINITION).isEmpty()){
			return getReferenceOntologyAnnotations(SemSimRelation.KBRelations.HAS_PHYSICAL_DEFINITION).toArray(new ReferenceOntologyAnnotation[]{})[0];
		}
		return null;
	}
	
	public ReferenceOntologyAnnotation getRefersToReferenceOntologyAnnotationByURI(URI uri){
		for(ReferenceOntologyAnnotation ann : getReferenceOntologyAnnotations(SemSimRelation.KBRelations.HAS_PHYSICAL_DEFINITION)){
			if(ann.getReferenceURI().compareTo(uri)==0){
				return ann;
			}
		}
		return null;
	}
	
	public Boolean isAnnotated(){
		return !getAnnotations().isEmpty();
	}
	
	public Boolean hasRefersToAnnotation(){
		return getFirstRefersToReferenceOntologyAnnotation()!=null;
	}

	public void removeAllReferenceAnnotations() {
		Set<Annotation> newset = new HashSet<Annotation>();
		for(Annotation ann : this.getAnnotations()){
			if(!(ann instanceof ReferenceOntologyAnnotation)){
				newset.add(ann);
			}
		}
		annotations.clear();
		annotations.addAll(newset);
	}
	
	public void addPhysicalProperty(PhysicalProperty propertytoAdd) {
		physicalProperties.add(propertytoAdd);
	}
	
	public Set<PhysicalProperty> getPhysicalProperties() {
		return physicalProperties;
	}
	
	public PhysicalModelComponent clone() throws CloneNotSupportedException {
        return (PhysicalModelComponent) super.clone();
	}
	
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (obj==this) return true;
		if (getClass()==obj.getClass()) return isEquivalent(obj);
		return false;
	}
	
	protected abstract boolean isEquivalent(Object obj);
}
