//Container for a model stored in the database
package semsimKB.model;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import semsimKB.SemSimKBConstants;
import semsimKB.annotation.Annotation;
import semsimKB.annotation.CurationalMetadata;
import semsimKB.annotation.CurationalMetadata.Metadata;
import semsimKB.model.data.KBDataComponent;

public class CompBioModel extends SemSimObject {
	protected Set<KBDataComponent> AssociatedDataSets = new HashSet<KBDataComponent>();
	protected CurationalMetadata metadata = new CurationalMetadata();
	
	public CompBioModel(URI uri, String name) {
		setName(name);
		setURI(uri);
	}
	public CompBioModel(ModelLite model) {
		setName(model.getName());
		setDescription(model.getDescription());
		setURI(URI.create(
				SemSimKBConstants.VPR_NAMESPACE + getName()));
		metadata.importMetadata(model.getCurationalMetadata(), true);
	}
	
	public Set<KBDataComponent> getDataSets() {
		return AssociatedDataSets;
	}
		
	public void addDataSet(KBDataComponent DataSet) {
		AssociatedDataSets.add(DataSet);
	}
	
	/**
	 * Add a SemSim {@link Annotation} to this object
	 * @param ann The {@link Annotation} to add
	 */
	public void setModelAnnotation(Metadata metaID, String value) {
		metadata.setAnnotationValue(metaID, value);
	}
	
	@Override
	public String getDescription() {
		return metadata.getAnnotationValue(Metadata.description);
	}
	@Override
	public void setDescription(String value) {
		metadata.setAnnotationValue(Metadata.description, value);
	}
	
	public CurationalMetadata getCurationalMetadata() {
		return metadata;
	}
	
	
	@Override
	public  URI getClassURI() {
		return SemSimKBConstants.KB_MODEL_URI;
	}
}
