package vprExplorer.common;

import java.net.URI;
import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import semsimKB.definitions.SemSimTypes;
import vprExplorer.Settings;

public class KBTree extends DefaultTreeModel {
	private static final long serialVersionUID = 1L;
	Settings globals;
	DefaultMutableTreeNode modelNode, dataSetNode, processNode,	compEntNode, 
		refEntNode, ppNode;
	    
	HashMap<DefaultMutableTreeNode, URI> classmap = new HashMap<DefaultMutableTreeNode, URI>();
	HashMap<DefaultMutableTreeNode, URI> urimap = new HashMap<DefaultMutableTreeNode, URI>();
	
	public KBTree(Settings global) {
		super(null);
		globals = global;
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("VPR KB");
		setRoot(top);
		createNodes(top);
	}
	
	private void createNodes(DefaultMutableTreeNode top) {
	    modelNode = new DefaultMutableTreeNode("Models");
	    top.add(modelNode);
	    
	    dataSetNode = new DefaultMutableTreeNode("Datasets");
	    top.add(dataSetNode);
	    
	    processNode = new DefaultMutableTreeNode("Physical Processes");
	    top.add(processNode);
	    
	    compEntNode = new DefaultMutableTreeNode("Composite Entities");
	    top.add(compEntNode);
	    
	    refEntNode = new DefaultMutableTreeNode("Reference Entities");
	    top.add(refEntNode);
	    
	    ppNode = new DefaultMutableTreeNode("Physical Properties");
	    top.add(ppNode);
	    
	    classmap.put(modelNode, SemSimTypes.KB_MODEL.getURI());
		classmap.put(dataSetNode, SemSimTypes.KB_DATASET.getURI());
		classmap.put(processNode, SemSimTypes.KB_PHYSICAL_PROCESS.getURI());
		classmap.put(compEntNode, SemSimTypes.KB_COMPOSITE_ENTITY.getURI());
		classmap.put(refEntNode, SemSimTypes.REFERENCE_PHYSICAL_ENTITY.getURI());
		classmap.put(ppNode, SemSimTypes.PHYSICAL_PROPERTY.getURI());
	}
	
//	public void loadLeaves(DefaultMutableTreeNode sel) {
//		URI seluri = classmap.get(sel);
//		RemoteKnowledgeBaseInterface query = new RemoteKnowledgeBaseInterface(globals);
//		
//		LinkedList<String> rslts = query.getAllClassMemberNames(seluri);
//		
//		for (String s : rslts) {
//			insertNodeInto(new DefaultMutableTreeNode(s), sel, sel.getChildCount());
//		}
//		this.reload(sel);
//	}
	
}
