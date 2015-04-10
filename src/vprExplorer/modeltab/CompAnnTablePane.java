//Tables for component information in model adder tab
package vprExplorer.modeltab;

import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import vprExplorer.common.KBTable;
import vprExplorer.modeltab.tables.KBCompositeTableModel;
import vprExplorer.modeltab.tables.SemSimCompositeTableModel;

public class CompAnnTablePane extends JPanel implements Observer{
	private static final long serialVersionUID = 1L;
	private KBTable compAnnTable, kbcompAnnTable;
	private AddModelWorkbench workbench;

	//Composite table panel initialization
	public CompAnnTablePane(AddModelWorkbench cb) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		workbench = cb;
		workbench.addObserver(this);
		setBorder(BorderFactory.createLoweredBevelBorder());
		
		compAnnTable = new KBTable(new SemSimCompositeTableModel());				
		kbcompAnnTable = new KBTable(new KBCompositeTableModel());
		
		add(new JScrollPane(compAnnTable));
		add(new JScrollPane(kbcompAnnTable));
		validate();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
	
		
	}

	
}
