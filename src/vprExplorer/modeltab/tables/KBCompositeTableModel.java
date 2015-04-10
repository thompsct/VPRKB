package vprExplorer.modeltab.tables;

import javax.swing.table.TableCellRenderer;

import semsimKB.utilities.descriptors.KBCompositeDescriptor;
import vprExplorer.common.KBTableModel;

public class KBCompositeTableModel extends KBTableModel {
	private static final long serialVersionUID = 1L;

	public KBCompositeTableModel() {
		addRow(new String[]{"No composite selected", ""}, new TableCellRenderer[]{heading, heading});
	}
	
	public KBCompositeTableModel(KBCompositeDescriptor descriptor) {
		
	}
	
	@Override
	public void updateTable() {
	
		
	}

}
