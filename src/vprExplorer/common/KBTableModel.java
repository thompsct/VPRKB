package vprExplorer.common;

import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

public abstract class KBTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	protected String[] columnNames = new String[]{"",""};
	protected ArrayList<String[]> data = new ArrayList<String[]>();
	protected ArrayList<TableCellRenderer[]> renderers = new ArrayList<TableCellRenderer[]>();
	protected Font boldfont = new Font("arial", Font.BOLD, 12);
	
	protected TableCellRenderer heading = new LabelRenderer(true, false);
	protected TableCellRenderer normal = new LabelRenderer(false, false);
	
	public KBTableModel() {};	
	
	public void setTitles(String[] titles) {
		columnNames = titles;
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
	
	public void setValueAt(String value, int row, int column) {
		data.get(row)[column] =value;
		fireTableCellUpdated(row, column);
	}

	public void addRow(String[] obj, TableCellRenderer[] renderer) {
		data.add(obj);
		renderers.add(renderer);
		fireTableRowsInserted(getRowCount(), getRowCount());
	 }
	
	 public void removeRow(int row) {
		 data.remove(row);
		 renderers.remove(row);
		 fireTableRowsDeleted(getRowCount(), getRowCount());
	 }
	
	 public TableCellRenderer getCellRenderer(int row, int column) {
		 return renderers.get(row)[column];
	 }
	 
	public void clear() {
		data.clear();
	}
	
	public abstract void updateTable();
	
	protected class LabelRenderer extends JLabel implements TableCellRenderer {
		private static final long serialVersionUID = 1L;
		
		boolean bolded;
		boolean selectable;
		
		LabelRenderer() {}
		LabelRenderer(boolean bold, boolean selectable) {
			bolded = bold;
		}
		
		@Override
		public Component getTableCellRendererComponent(JTable arg0,
				Object value, boolean selected, boolean isfocus, int row, int column) {
			
			if (bolded) setFont(boldfont);
			setText((String)value);
			return this;
		}
	}
}
