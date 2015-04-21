package semsimKB.model.physical;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import semsimKB.Annotatable;
import semsimKB.SemSimKBConstants;
import semsimKB.annotation.Annotation;
import semsimKB.annotation.ReferenceOntologyAnnotation;
import semsimKB.annotation.SemSimRelation;
import semsimKB.model.SemSimComponent;

public abstract class PhysicalModelComponent extends SemSimComponent implements Annotatable, Cloneable{
	private Set<Annotation> annotations = new HashSet<Annotation>();
	protected Set<PhysicalProperty> physicalProperties = new HashSet<PhysicalProperty>();
	
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
	
	public void addReferenceOntologyAnnotation(SemSimRelation relation, URI uri, String description){
		addAnnotation(new ReferenceOntologyAnnotation(relation, uri, description));
	}

	public Set<ReferenceOntologyAnnotation> getReferenceOntologyAnnotations(SemSimRelation relation) {
		Set<ReferenceOntologyAnnotation> raos = new HashSet<ReferenceOntologyAnnotation>();
		for(Annotation ann : getAnnotations()){
			if(ann instanceof ReferenceOntologyAnnotation && ann.getRelation()==relation){
				raos.add((ReferenceOntologyAnnotation)ann);
			}
		}
		return raos;
	}
	
	
	public ReferenceOntologyAnnotation getFirstRefersToReferenceOntologyAnnotation(){
		if(!getReferenceOntologyAnnotations(SemSimKBConstants.REFERS_TO_RELATION).isEmpty()){
			return getReferenceOntologyAnnotations(SemSimKBConstants.REFERS_TO_RELATION).toArray(new ReferenceOntologyAnnotation[]{})[0];
		}
		return null;
	}
	
	public ReferenceOntologyAnnotation getRefersToReferenceOntologyAnnotationByURI(URI uri){
		for(ReferenceOntologyAnnotation ann : getReferenceOntologyAnnotations(SemSimKBConstants.REFERS_TO_RELATION)){
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
}
