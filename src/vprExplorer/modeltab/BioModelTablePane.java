package vprExplorer.modeltab;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import vprExplorer.common.KBTable;
import vprExplorer.modeltab.tables.KBModelAnnotationTable;
import vprExplorer.modeltab.tables.ModelAnnotationTable;

public class BioModelTablePane extends JPanel  {
	private static final long serialVersionUID = 1L;
	KBTable modelAnnTable, kbmodAnnTable;
	AddModelWorkbench callback;
	
	public BioModelTablePane(AddModelWorkbench cb){
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		callback = cb;
		setBorder(BorderFactory.createLineBorder(Color.black));
		modelAnnTable = new KBTable(new ModelAnnotationTable());
		kbmodAnnTable = new KBTable(new KBModelAnnotationTable());
		
		add(new JScrollPane(modelAnnTable));
		add(new JScrollPane(kbmodAnnTable));
	}

		public void updateTable(int selection) {	

	}
	
}
