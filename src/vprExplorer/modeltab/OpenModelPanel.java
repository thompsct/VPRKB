package vprExplorer.modeltab;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class OpenModelPanel extends JPanel implements ActionListener{
		private static final long serialVersionUID = 1L;
		JLabel NSlabel = new JLabel("No Model Loaded");
		JButton load = new JButton("Load Model");
		AddModelWorkbench workbench;
		
		public OpenModelPanel(AddModelWorkbench bench) {
			super(new BorderLayout());
			workbench = bench;
			setLayout(new GridBagLayout());
			setPreferredSize(new Dimension(200, 200));
			
			GridBagConstraints gbl = new GridBagConstraints();
			
			//Load Model Status Label
			NSlabel.setVisible(true);
			gbl.fill = GridBagConstraints.HORIZONTAL;
			gbl.weightx = 0.5;
			gbl.gridx = 0;
			gbl.gridy = 1;
			gbl.anchor = GridBagConstraints.LINE_START;
			add(NSlabel, gbl);
			
			//Open Button
			load.addActionListener(this);
			load.setVisible(true);
			gbl.fill = GridBagConstraints.HORIZONTAL;
			gbl.weightx = 0.5;
			gbl.gridx = 0;
			gbl.gridy = 0;
			gbl.anchor = GridBagConstraints.FIRST_LINE_END;
			add(load, gbl);	
		}
		

		@Override
		public void actionPerformed(ActionEvent e) {
			Object obj = e.getSource();
			if (obj==load) {
				openNewModel();
			}
		}
		
		public void openNewModel() {
			JFileChooser ofd = new JFileChooser();

			ofd.setFileFilter(new FileNameExtensionFilter("OWL file", "owl"));
			ofd.setAcceptAllFileFilterUsed(false);
			int returnVal = ofd.showOpenDialog(this);
			
			if (returnVal==JFileChooser.APPROVE_OPTION) {
				String result = workbench.loadSemSimModel(ofd.getSelectedFile());
				NSlabel.setText(result);
			}
		}
}
