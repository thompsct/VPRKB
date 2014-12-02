package vprExplorer.common;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class KBTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	protected String[] columnNames = {"", ""};
	protected List<Object[]> data;
	public HashMap<Integer, Color> rowstocolor = new HashMap<Integer, Color>();
	
	public KBTableModel(String[] colnames, List<Object[]> table) {
		columnNames = colnames;
		data.addAll(table);
	}
	
	public KBTableModel() {
		data = Collections.synchronizedList(new ArrayList<Object[]>());
	};
		
	public void setTitles(String[] titles) {
		columnNames = titles;
	}
	
	public boolean hasRow(int row) {
		return rowstocolor.containsKey(row);
	}
	
	public Color getRowColor(int row) {
		return rowstocolor.get(row);
	}
	
	public void setRowtoColor(int i, Color c) {
		rowstocolor.put(i, c);
	}
	
	public void clearColors() {
		rowstocolor.clear();
	}
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
	
	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		return data.get(arg0)[arg1];
	}
	
	public void  setValueAt(String value, int arg0, int arg1) {
		data.get(arg0)[arg1] =value;
		fireTableCellUpdated(arg0, arg1);
	}
	
	public boolean isCellEditable(int row, int col) { return false; }
	 
	public void addRow(Object[] obj) {
		data.add(obj);
		fireTableRowsInserted(getRowCount(), getRowCount());
	 }
	 public void removeRow(int row) {
		 data.remove(row);
		 fireTableRowsDeleted(getRowCount(), getRowCount());
	 }
	 
	public void clear() {
		while (getRowCount() > 0) removeRow(0);
	}
	
	 
}
