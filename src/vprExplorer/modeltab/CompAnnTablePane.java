//Tables for component information in model adder tab
package vprExplorer.modeltab;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import vprExplorer.common.KBTable;
import vprExplorer.common.KBTableModel;

public class CompAnnTablePane extends JPanel {
	private static final long serialVersionUID = 1L;
	private KBTable compAnnTable, kbcompAnnTable;
	KBTableModel compTableObj, kbcompTableObj;
	private AddModelWorkbench callback;

	//Composite table panel initialization
	public CompAnnTablePane(AddModelWorkbench cb) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		callback = cb;

		setBorder(BorderFactory.createLoweredBevelBorder());
		
		compAnnTable = new KBTable(callback.initSemSimCompTable(), false);				
		kbcompAnnTable = new KBTable(callback.initKBComponentTable(), true);
		
		add(new JScrollPane(compAnnTable));
		add(new JScrollPane(kbcompAnnTable));
		validate();
	}
}
