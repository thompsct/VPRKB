package vprExplorer.common;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import java.awt.Color;

public class KBTable extends JTable {
	KBTableModel tablemodel;
	
	public KBTable(KBTableModel model) {
		super(model);
		tablemodel = model;
		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(false); 
		setBorder(BorderFactory.createLineBorder(Color.black));
		setAutoResizeMode(AUTO_RESIZE_LAST_COLUMN);
		putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		validate();
	}
	
	private static final long serialVersionUID = 1L;

	public TableCellRenderer getCellRenderer(int row, int column) {
		return tablemodel.getCellRenderer(row, column);
	}

}
