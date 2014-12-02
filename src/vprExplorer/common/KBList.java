package vprExplorer.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;


public class KBList extends JList<Object[]> {
	private boolean ColorRenderEnabled;
	Font bolded = new Font("arial", Font.BOLD, 12);
	Font arial = new Font("arial", Font.PLAIN, 12);
	
	public KBList(KBListModel model, boolean cre) {
		super(model);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setLayoutOrientation(JList.VERTICAL);
		ColorRenderEnabled = cre;
		setCellRenderer(new ColorRenderer());
		setSelectionBackground(new Color(170,170,170));

	}
	
	public KBListModel getModel() {
		return (KBListModel) super.getModel();
	}
	
	private static final long serialVersionUID = 1L;

	public ListCellRenderer<? super Object[]> getCellRenderer(int row) {
	   	return new ColorRenderer();
	}
		    
	
	public boolean isColorRenderEnabled() {
		return ColorRenderEnabled;
	}

	public void setColorRenderEnabled(boolean colorRenderEnabled) {
		ColorRenderEnabled = colorRenderEnabled;
	}
	
	public Object[] getSelectedValue() {
		int index = getSelectedIndex();
		return getModel().getElementAt(index);
	}
	
	private class ColorRenderer extends JLabel implements ListCellRenderer<Object[]> {

		private static final long serialVersionUID = 1L;
		public ColorRenderer() {
		       setOpaque(true); //MUST do this for background to show up.
		       
		  }
		
		@Override
		public Component getListCellRendererComponent(
				JList<? extends Object[]> list, Object[] value, int index,
				boolean isSelected, boolean cellHasFocus) {
			Color col = null;
			if (ColorRenderEnabled) col = getModel().getRowColor(index);	
			if (list.isSelectedIndex(index)) col=list.getSelectionBackground();				 
			setBackground(col);
			if (getModel().getBolded(index)) setFont(bolded);
				else setFont(arial);
            setText(((KBListModel)list.getModel()).get(index).toString());
                        
			return this;
		}

	}
}
