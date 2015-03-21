package vprExplorer.modeltab;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import vprExplorer.common.KBTable;

public class BioModelTablePane extends JPanel  {
	private static final long serialVersionUID = 1L;
	KBTable modelAnnTable, kbmodAnnTable;
	AddModelWorkbench callback;
	
	public BioModelTablePane(AddModelWorkbench cb){
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		callback = cb;
		setBorder(BorderFactory.createLineBorder(Color.black));
		modelAnnTable = new KBTable(callback.initSemSimModelTable(), true);
		kbmodAnnTable = new KBTable(callback.initKBModelTable(), true);
		
		add(new JScrollPane(modelAnnTable));
		add(new JScrollPane(kbmodAnnTable));
	}
}
