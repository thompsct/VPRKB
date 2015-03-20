package vprExplorer.modeltab;
//Methods for KB Model Adder User Interface
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import vprExplorer.Settings;

public class KBAddModelTab extends JPanel {

	private AddModelWorkbench callback;
	private static final long serialVersionUID = 1L;
	private ModelTabSidebar utilitiesTab = new ModelTabSidebar();
	private ModelTabRight adderTab;
	private Settings settings;
	
	public KBAddModelTab(Settings global) {
		settings = global;
		
		setOpaque(false);
		setLayout(new BorderLayout());
		
		callback = new AddModelWorkbench(settings);
		adderTab = new ModelTabRight();
		//Right Panel	
			
		this.add(utilitiesTab, BorderLayout.WEST);
		this.add(adderTab, BorderLayout.CENTER);
		validate();
		this.setVisible(true);
		
	}

	private class ModelTabRight extends JPanel{
		private static final long serialVersionUID = 1L;
		ComponentListsPane complistpanel;
		BioModelTablePane modview;
		CompAnnTablePane compview;
		
		public ModelTabRight() {
			super(new BorderLayout());
			setPreferredSize(settings.getScreenDimensions());
			Dimension subpanelsize = new Dimension(getPreferredSize().width, getPreferredSize().height/3);
			
			complistpanel = new ComponentListsPane(callback);	
			modview = new BioModelTablePane(subpanelsize, callback); 
			compview = new CompAnnTablePane(subpanelsize, callback);
			
		    add(complistpanel, BorderLayout.NORTH);
		    add(modview, BorderLayout.CENTER);
		    add(compview, BorderLayout.SOUTH);
		    
			setBackground(Color.WHITE);
			setBorder(BorderFactory.createLoweredBevelBorder());
			setVisible(true);
		}
	}	
	
	//Utilities Panel Layout - left side
	private class ModelTabSidebar extends JPanel implements ActionListener {
		private static final long serialVersionUID = 1L;
		JPanel openPanel = new JPanel();
		JLabel NSlabel = new JLabel("No Model Loaded");
		JButton load = new JButton("Load Model");
		
		public ModelTabSidebar() {
			super(new BorderLayout());
			//Search Panel
			openpanel();
			
			setVisible(true);
		}
		
		private void openpanel() {
			
			openPanel.setLayout(new GridBagLayout());
			openPanel.setPreferredSize(new Dimension(200, 200));
			
			GridBagConstraints gbl = new GridBagConstraints();
			
			//Load Model Status Label
			NSlabel.setVisible(true);
			gbl.fill = GridBagConstraints.HORIZONTAL;
			gbl.weightx = 0.5;
			gbl.gridx = 0;
			gbl.gridy = 1;
			gbl.anchor = GridBagConstraints.LINE_START;
			openPanel.add(NSlabel, gbl);
			
			//Open Button
			load.addActionListener(this);
			load.setVisible(true);
			gbl.fill = GridBagConstraints.HORIZONTAL;
			gbl.weightx = 0.5;
			gbl.gridx = 0;
			gbl.gridy = 0;
			gbl.anchor = GridBagConstraints.FIRST_LINE_END;
			openPanel.add(load, gbl);
					
			add(openPanel, BorderLayout.NORTH);
			setBorder(BorderFactory.createRaisedBevelBorder());
		}
		

		@Override
		public void actionPerformed(ActionEvent e) {
			Object obj = e.getSource();
			if (obj==load) {
				openNewModel();
			}
		}
		
		public void openNewModel() {
			final JFileChooser ofd = new JFileChooser();

			ofd.setFileFilter(new FileNameExtensionFilter("OWL file", "owl"));
			ofd.setAcceptAllFileFilterUsed(false);
			int returnVal = ofd.showOpenDialog(this);
			
			if (returnVal==JFileChooser.APPROVE_OPTION) {
				String result = callback.loadSemSimModel(ofd.getSelectedFile());
				NSlabel.setText(result);
			}
		}
	}
}


