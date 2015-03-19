package vprExplorer.datasettab;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import vprExplorer.Settings;

public class DatasetTab extends JPanel {

	private static final long serialVersionUID = 1L;
	protected Dimension panedim;
	Settings globals;
	
	public DatasetTab(Dimension dim, Settings global) {
		globals = global;
		panedim = dim;
		setOpaque(false);
		setLayout(new BorderLayout());
		this.setVisible(true);
	}
}
