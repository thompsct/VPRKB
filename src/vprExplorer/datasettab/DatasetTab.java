package vprExplorer.datasettab;

import java.awt.BorderLayout;
import javax.swing.JPanel;

import vprExplorer.Settings;

public class DatasetTab extends JPanel {

	private static final long serialVersionUID = 1L;
	private Settings settings;
	
	public DatasetTab(Settings global) {
		settings = global;
		setOpaque(false);
		setLayout(new BorderLayout());
		this.setVisible(true);
	}
}
