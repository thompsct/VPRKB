//Container for a model stored in the database
package semsimKB.model;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import semsimKB.SemSimKBConstants;
import semsimKB.model.data.KBDataComponent;

public class CompBioModel extends SemSimComponent {
	protected Map<URI, String> ModelURL = new HashMap<URI, String>();
	protected Set<KBDataComponent> AssociatedDataSets = new HashSet<KBDataComponent>();
	
	public CompBioModel(URI uri, String name) {
		setName(name);
		setURI(uri);
	}
	public CompBioModel(ModelLite model) {
		setName(model.getName());
		setDescription(model.getDescription());
		setURI(URI.create(
				SemSimKBConstants.VPR_NAMESPACE + getName()));
	}

	public Map<URI, String> getModelURLList() {
		return ModelURL;
	}
	
	public String getModelURL(URI repos) {
		String url = ModelURL.get(repos);
		if (url==null) url="";
		
		return url;
	}
	
	public void addModelURL(URI repos, String URL) {
		ModelURL.put(repos, URL);
	}
	
	public void removeModelURL(String repos) {
		ModelURL.remove(repos);
	}
	
	public void replaceModelURL(URI repos, String URL) {
		ModelURL.remove(repos);
		ModelURL.put(repos, URL);
	}
	
	public Set<KBDataComponent> getDataSets() {
		return AssociatedDataSets;
	}
		
	public void addDataSet(KBDataComponent DataSet) {
		AssociatedDataSets.add(DataSet);
	}
	
	@Override
	public  URI getClassURI() {
		return SemSimKBConstants.KB_MODEL_URI;
	}
}
