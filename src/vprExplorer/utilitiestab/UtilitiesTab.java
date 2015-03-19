package vprExplorer.utilitiestab;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import semsimKB.webservices.VPRSPARQLWrite;
import vprExplorer.Settings;

public class UtilitiesTab extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton purge = new JButton("Purge KB");
	private Settings settings;
	
	private VPRSPARQLWrite sparql = new VPRSPARQLWrite(settings);
	
	public UtilitiesTab(Settings global) {
		settings = global;
		setOpaque(false);
		setLayout(new BorderLayout());
		this.setVisible(true);
		
		purge.addActionListener(this);
		add(purge);
		
	}
	
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();

		if (obj==purge) {
			sparql.purgeKB();
		}
	}
}
