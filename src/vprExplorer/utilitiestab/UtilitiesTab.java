package vprExplorer.utilitiestab;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import semsimKB.webservices.VPRSPARQLWrite;
import vprExplorer.Globals;

public class UtilitiesTab extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	protected Dimension panedim;
	protected JButton purge = new JButton("Purge KB");
	Globals globals;
	
	VPRSPARQLWrite sparql = new VPRSPARQLWrite(globals);
	
	public UtilitiesTab(Dimension dim, Globals global) {
		globals = global;
		panedim = dim;
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
