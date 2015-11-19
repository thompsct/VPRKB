package vprExplorer.common;

import java.awt.Color;
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
	protected ArrayList<String[]> tooltips = new ArrayList<String[]>();
	protected ArrayList<PhysKBTableRenderer[]> renderers = new ArrayList<PhysKBTableRenderer[]>();
	protected Font boldfont = new Font("arial", Font.BOLD, 12);
	
	protected PhysKBTableRenderer heading = new LabelRenderer(true, false);
	protected PhysKBTableRenderer normal = new LabelRenderer(false, false);
	protected PhysKBTableRenderer[] dfltrowrenderer = new PhysKBTableRenderer[]{heading,normal};
	protected String[] blank = new String[]{"", ""};
	
	public KBTableModel() {};	
	
	public void setTitles(String[] titles) {
		columnNames = titles;
	}
	
	@Override
	public boolean isCellEditable(int row, int col)
    { return true; }
	
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
		((TextareaRenderer)renderers.get(row)[1]).setValue(value);
		data.get(row)[column] =value;
		fireTableCellUpdated(row, column);
	}

	public void addRow(String[] obj, PhysKBTableRenderer[] renderer) {
		data.add(obj);
		renderers.add(renderer);
		tooltips.add(blank);
		fireTableRowsInserted(getRowCount(), getRowCount());
	 }
	
	public void addRow(String[] obj, String[] tooltip, PhysKBTableRenderer[] renderer) {
		data.add(obj);
		renderers.add(renderer);
		tooltips.add(tooltip);
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
		tooltips.clear();
		renderers.clear();
	}
	
	public abstract void updateTable();
	
	protected interface PhysKBTableRenderer extends TableCellRenderer{
		boolean isEditable();
	}
	
	protected class LabelRenderer extends JLabel implements PhysKBTableRenderer {
		private static final long serialVersionUID = 1L;
		
		boolean bolded;
		boolean selectable;
		Color color = Color.lightGray;
		
		public LabelRenderer(boolean bold, boolean selectable) {
			setOpaque(true);
			bolded = bold;
		}
		public LabelRenderer(boolean bold, boolean selectable, Color col) {
			setOpaque(true);
			bolded = bold;
			color = col;
		}
		
		@Override
		public Component getTableCellRendererComponent(JTable arg0,
				Object value, boolean selected, boolean isfocus, int row, int column) {
			
			if (bolded) setFont(boldfont);
			setBackground(color);
			setText((String)value);
			if (tooltips.get(row)[column] != "") setToolTipText(tooltips.get(row)[column]);
			return this;
		}
		@Override
		public boolean isEditable() {
			return false;
		}
	}
	
	public interface DataSetter {
		public void setValue(String val);
	}
	
	public interface DataGetter {
		public String getValue();
	}
	
	protected class TextareaRenderer extends JLabel implements PhysKBTableRenderer {
		private static final long serialVersionUID = 1L;
		Color color = Color.white;
		DataSetter settarget;
		
		public TextareaRenderer(DataSetter t) {
			settarget = t;
		}
		
		@Override
		public Component getTableCellRendererComponent(JTable arg0,
				Object value, boolean selected, boolean isfocus, int row, int column) {
			if (isfocus) requestFocus(); 
			setText((String)value);
			return this;
		}

		public void setValue(String val) {
			settarget.setValue(val);
		}
		
		@Override
		public boolean isEditable() {
			return true;
		}
		
	}
}
