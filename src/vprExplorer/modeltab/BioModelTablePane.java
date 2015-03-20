package vprExplorer.modeltab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import vprExplorer.common.KBTable;

public class BioModelTablePane extends JPanel  {
	private static final long serialVersionUID = 1L;
	KBTable modelAnnTable, kbmodAnnTable;
	AddModelWorkbench callback;
	
	public BioModelTablePane(Dimension prefsize, AddModelWorkbench cb){
		super(new BorderLayout());
		setPreferredSize(prefsize);
		
		Dimension tableDim = new Dimension(prefsize.width/2,prefsize.height);
		callback = cb;
		setBorder(BorderFactory.createLineBorder(Color.black));
		
		modelAnnTable = new KBTable(callback.initSemSimModelTable(), tableDim, true);
		kbmodAnnTable = new KBTable(callback.initKBModelTable(), tableDim, true);
		
		add(modelAnnTable, BorderLayout.WEST);
		add(kbmodAnnTable, BorderLayout.EAST);
	}
}
