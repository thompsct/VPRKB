package vprExplorer.common;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import dataBaseOperations.readDataBase.ReadRDFDatabase;
import semsimKB.SemSimKBConstants;
import vprExplorer.Globals;

public class KBTree extends DefaultTreeModel {
	private static final long serialVersionUID = 1L;
	Globals globals;
	DefaultMutableTreeNode modelNode, dataSetNode, processNode,	compEntNode, 
		refEntNode, ppNode;
	    
	HashMap<DefaultMutableTreeNode, URI> classmap = new HashMap<DefaultMutableTreeNode, URI>();
	HashMap<DefaultMutableTreeNode, URI> urimap = new HashMap<DefaultMutableTreeNode, URI>();
	
	public KBTree(Globals global) {
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
	    
	    classmap.put(modelNode, SemSimKBConstants.KB_COMPUTATIONAL_BIOMODEL_URI);
		classmap.put(dataSetNode, SemSimKBConstants.KB_DATASET_CLASS_URI);
		classmap.put(processNode, SemSimKBConstants.KB_PHYSICAL_PROCESS_CLASS_URI);
		classmap.put(compEntNode, SemSimKBConstants.KB_PHYSICAL_ENTITY_CLASS_URI);
		classmap.put(refEntNode, SemSimKBConstants.REFERENCE_PHYSICAL_ENTITY_CLASS_URI);
		classmap.put(ppNode, SemSimKBConstants.PHYSICAL_PROPERTY_CLASS_URI);
	}
	
	public void loadLeaves(DefaultMutableTreeNode sel) {
		URI seluri = classmap.get(sel);
		ReadRDFDatabase query = new ReadRDFDatabase(globals);
		
		LinkedList<String> rslts = query.getAllClassMemberNames(seluri);
		
		for (String s : rslts) {
			insertNodeInto(new DefaultMutableTreeNode(s), sel, sel.getChildCount());
		}
		this.reload(sel);
	}
	
}
