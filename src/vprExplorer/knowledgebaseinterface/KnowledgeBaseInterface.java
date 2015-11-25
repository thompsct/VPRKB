package vprExplorer.knowledgebaseinterface;

import java.net.URI;
import java.util.ArrayList;

import org.apache.commons.lang3.tuple.Pair;

import semsimKB.definitions.StructuralRelation;
import semsimKB.model.SemSimComponent;
import semsimKB.model.kbbuffer.KBCompositeObject;
import semsimKB.model.kbbuffer.KnowledgeBase;
import semsimKB.model.physical.DBCompositeEntity;

public abstract class KnowledgeBaseInterface {
	protected KnowledgeBase buffer;
	
	KnowledgeBaseInterface(KnowledgeBase buff) {
		buffer = buff;
	}
	
	public abstract boolean getElementwithName(SemSimComponent ele, boolean verifyonly);
	public abstract boolean getElementwithName(String ele, boolean verifyonly);
	
	public abstract boolean getElementwithURI(SemSimComponent ele, boolean verifyonly);
	public abstract boolean getElementwithURI(URI eleuri, boolean verifyonly);
	
	public abstract DBCompositeEntity retrieveComposite(Pair<URI, URI> comps, StructuralRelation rel);
	
	public abstract int pushChangestoDatabase(URI modeluri, ArrayList<KBCompositeObject<DBCompositeEntity>> composites);
}
