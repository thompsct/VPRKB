package vprKBEditorViews;

import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class KBModelEditor extends JScrollPane {
	private static final long serialVersionUID = 1L;
	protected Dimension panedim;
	JPanel BasicInfo, Genealogy, DetailedInfo, URLs;
	
	public KBModelEditor(Dimension dim) {
		panedim = dim;
		setOpaque(false);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setVisible(true);
	}
	
	public void modelBasicInfo() {
		BasicInfo = new JPanel(new GridBagLayout());
		
	}
	
	public void modelGenealogy() {
		Genealogy = new JPanel(new GridBagLayout());
	}
	
	public void modelURLs() {
		URLs = new JPanel(new GridBagLayout());
	}
}
