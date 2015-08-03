package vprExplorer.utilities;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import semsimKB.webservices.vprSPARQL;
import vprExplorer.GlobalActions;
import vprExplorer.Settings;

public class ServiceSelectionDialog extends CommonDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	JButton okbut, cclbut;
	JRadioButton kbserver, localser, rdffile;
	JTextField uname;
	Settings settings;
	GlobalActions gacts;
	
	public ServiceSelectionDialog(Settings sets, GlobalActions acts) {
		super("Select RDF Service");
		
		settings = sets;
		gacts = acts;
		
		setLayout(new GridLayout(0,2));
		setUndecorated(true);
		setResizable(false);
		kbserver = new JRadioButton("KB Server");
		localser = new JRadioButton("Local Server");
		rdffile = new JRadioButton("Local File");
		
		ButtonGroup service = new ButtonGroup();		

		for (JRadioButton rb : new JRadioButton[]{kbserver, localser,rdffile}) {
			service.add(rb);
			rb.addActionListener(this);
		}
		add(kbserver);
		uname = new JTextField();
		uname.setEnabled(false);
		uname.setMinimumSize(new Dimension(30,20));
		add(uname);
		add(localser);
		add(new JPanel());
		add(rdffile);
		add(new JPanel());
		
		okbut = new JButton("Connect");
		okbut.setEnabled(false);
		okbut.addActionListener(this);
		
		cclbut = new JButton("Quit");
		cclbut.addActionListener(this);
		add(okbut);
		add(cclbut);
		
		showDialog();
	}

	private void onOKButton() {
		if (kbserver.isSelected()) {
			if (uname.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Please enter a user name.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			settings.setUser(uname.getText());
			settings.setService(Settings.service._REMOTE);
			while (true) {
				new PasswordDialog(settings);
				if (settings.getPassword().length==0) {
					return;
				}
				
				int verify = new vprSPARQL(settings).verifyCredentials();
				if (verify==0) break;
			}
			
			uname.setEnabled(true);
		}
		else if(localser.isSelected()) {
			settings.setService(Settings.service._LOCAL_SERVER);
		}
		else {
			settings.setService(Settings.service._FILE);
		}
		dispose();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object obj = arg0.getSource();

		if (obj == cclbut) {
			gacts.quit();
		}
		else if (obj==okbut){
			onOKButton();
		}
		if (obj==kbserver) {
			uname.setEnabled(true);
			okbut.setEnabled(true);
		}
		
		else if (obj==localser) {
			uname.setEnabled(false);
			okbut.setEnabled(true);
		}
		else if (obj==rdffile) {
			uname.setEnabled(false);
			okbut.setEnabled(true);
		}
	}
}
