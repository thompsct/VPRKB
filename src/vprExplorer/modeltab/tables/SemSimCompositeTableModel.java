package vprExplorer.modeltab.tables;

import javax.swing.table.TableCellRenderer;

import semsimKB.utilities.descriptors.CompositeEntityDescriptor;
import vprExplorer.common.KBTableModel;

public class SemSimCompositeTableModel extends KBTableModel {
	private static final long serialVersionUID = 1L;

	public SemSimCompositeTableModel() {
		addRow(new String[]{"No composite selected", ""}, new TableCellRenderer[]{heading, heading});
	}
	
	public SemSimCompositeTableModel(CompositeEntityDescriptor describeSemSimComposite) {
		int i = 0;
		for (String[] row : describeSemSimComposite.getDescription()) {
			addRow(row,describeSemSimComposite.getAdditionalInformation(i), dfltrowrenderer);
			i++;
		}
	}

	@Override
	public void updateTable() {
		
	}

}
