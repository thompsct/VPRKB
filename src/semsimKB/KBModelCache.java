package semsimKB;

import java.util.HashMap;
import java.util.Map;

import semsimKB.model.ModelLite;

public class KBModelCache {
	
	// Local cache of semsim models
	private Map<String,ModelLite> filePathsAndKBModels = new HashMap<String, ModelLite>();
	
	public ModelLite getCachedKBModelFromFilePath(String filepath){
		if(filePathsAndKBModels.containsKey(filepath))
			return filePathsAndKBModels.get(filepath);
		else return null;
	}
	
	public void cacheKBModelAndAssociatedFilePath(ModelLite KBModel, String filepath){
		filePathsAndKBModels.put(filepath, KBModel);
		System.out.println();
	}
	
	public Map<String,ModelLite> getFilePathToKBModelMap(){
		return filePathsAndKBModels;
	}
}
