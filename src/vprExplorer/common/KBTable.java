package vprExplorer.common;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

public class KBTable extends JTable {
	private boolean ColorRenderEnabled;
	Font bolded = new Font("arial", Font.BOLD, 12);
	
	public KBTable(KBTableModel model, boolean colorRenderEnabled) {
		super(model);
		setColorRenderEnabled(colorRenderEnabled);
		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(false); 
		setBorder(BorderFactory.createLineBorder(Color.black));
		setAutoResizeMode(AUTO_RESIZE_LAST_COLUMN);
		putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		validate();
	}
	
	public KBTableModel getModel() {
		return (KBTableModel) super.getModel();
	}
	
	private static final long serialVersionUID = 1L;

	public TableCellRenderer getCellRenderer(int row, int column) {
		if (column==0) return new BoldFont();
		
		if (ColorRenderEnabled) {
				if ((getModel().hasRow(row))&& (column==1)) {
						return new ColorRenderer(true, getModel().rowstocolor.get(row));
					}
				}
		    	return super.getCellRenderer(row, column);
		}
		    
	public boolean isColorRenderEnabled() {
		return ColorRenderEnabled;
	}

	public void setColorRenderEnabled(boolean colorRenderEnabled) {
		ColorRenderEnabled = colorRenderEnabled;
	}

	private class ColorRenderer extends JLabel implements TableCellRenderer {

		private static final long serialVersionUID = 1L;
		private  Color newColor;
		public ColorRenderer(boolean isBordered, Color color) {
		       setOpaque(true); //MUST do this for background to show up.
		       newColor = color;
		  }

		@Override
        public Component getTableCellRendererComponent(JTable table,
                Object color, boolean isSelected, boolean hasFocus, int row,
                int column) {
			
            setBackground(newColor);
            setText(getValueAt(row, column).toString());
            return this;
        }
	}

	private class BoldFont extends JLabel implements TableCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			setText(getValueAt(row, column).toString());
			setFont(bolded);
			return this;
		}
	}
 
	
}
