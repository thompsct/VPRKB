package vprExplorer;

import java.awt.Color;
import javax.swing.JTabbedPane;

import vprExplorer.datasettab.DatasetTab;
import vprExplorer.editortab.EditorTab;
import vprExplorer.modeltab.KBAddModelTab;
import vprExplorer.utilitiestab.UtilitiesTab;

public class KBClientGUI extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	private Settings settings;
	private GlobalActions gacts;
	
	public KBClientGUI(Settings sets, GlobalActions acts) {
		settings = sets;
		acts = gacts;
		setOpaque(true);
		setBackground(Color.white);
		
		initialize();
	}

	private void initialize() {
		addTab("Upload Model", new KBAddModelTab(settings));
		addTab("Edit Database", new EditorTab(settings));
		addTab("Datasets", new DatasetTab(settings));
		addTab("Utilities", new UtilitiesTab(settings));
		
		setSelectedComponent(getComponentAt(0));
		validate();
	}
}
