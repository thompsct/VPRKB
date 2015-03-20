//Tables for component information in model adder tab
package vprExplorer.modeltab;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import vprExplorer.common.KBTable;
import vprExplorer.common.KBTableModel;

public class CompAnnTablePane extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private KBTable compAnnTable, kbcompAnnTable;
	KBTableModel compTableObj, kbcompTableObj;
	private AddModelWorkbench callback;

	//Composite table panel initialization
	public CompAnnTablePane(Dimension prefsize, AddModelWorkbench cb) {
		super(new BorderLayout());
		callback = cb;
		setPreferredSize(prefsize);
		setBorder(BorderFactory.createLoweredBevelBorder());
		
		Dimension tableDim = new Dimension(getPreferredSize().width/2,getPreferredSize().height);
		
		compAnnTable = new KBTable(callback.initSemSimCompTable(), tableDim, false);				
		kbcompAnnTable = new KBTable(callback.initKBComponentTable(), tableDim, true);
		
		add(compAnnTable, BorderLayout.WEST);
		add(kbcompAnnTable, BorderLayout.EAST);
	}
}
