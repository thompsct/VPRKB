package vprExplorer.modeltab.tables;

import javax.swing.table.TableCellRenderer;

import semsimKB.utilities.descriptors.KBModelDescriptor;
import vprExplorer.common.KBTableModel;

public class KBModelAnnotationTable extends KBTableModel {
	
	
	private static final long serialVersionUID = 1L;

	public KBModelAnnotationTable() {
		addRow(new String[]{"Model not loaded", ""}, new TableCellRenderer[]{heading, normal});
	}		
	
	public KBModelAnnotationTable(KBModelDescriptor descriptor) {
		addRow(new String[]{"Model Name:", ""}, new TableCellRenderer[]{heading, normal});
		addRow(new String[]{"URI:", ""}, new TableCellRenderer[]{heading, normal});
		addRow(new String[]{"Author:", ""}, new TableCellRenderer[]{heading, normal});
		addRow(new String[]{"Description:", ""}, new TableCellRenderer[]{heading, normal});
		
		addRow(new String[]{"Matlab URL:", ""}, new TableCellRenderer[]{heading, normal});
		addRow(new String[]{"JSim URL:", ""}, new TableCellRenderer[]{heading, normal});
		addRow(new String[]{"CellML URL:", ""}, new TableCellRenderer[]{heading, normal});				
	}

	@Override
	public void updateTable() {
		
	}	
}
