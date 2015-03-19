package vprExplorer.utilities;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;

import vprExplorer.GlobalActions;
import vprExplorer.Settings;

public class ServiceSelectionDialog extends CommonDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	JButton okbut, cclbut;
	JRadioButton kbserver, localser, rdffile;
	JPasswordField pwrd;
	Settings settings;
	GlobalActions gacts;
	
	public ServiceSelectionDialog(Settings sets, GlobalActions acts) {
		super("Select RDF Service");
		
		settings = sets;
		gacts = acts;
		
		//setBounds(3*screenwidth/7, screenheight/4, screenwidth/6, screenheight/7);
		setLayout(new GridLayout(0,2));
		
		kbserver = new JRadioButton("KB Server");
		localser = new JRadioButton("Local Server");
		rdffile = new JRadioButton("Local File");
		
		ButtonGroup service = new ButtonGroup();		

		for (JRadioButton rb : new JRadioButton[]{kbserver, localser,rdffile}) {
			service.add(rb);
			rb.addActionListener(this);
		}
		add(kbserver);
		pwrd = new JPasswordField();
		pwrd.setEnabled(false);
		add(pwrd);
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

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object obj = arg0.getSource();

		if (obj == cclbut) {
			gacts.quit();
		}
		else if (obj==okbut){
			dispose();
		}
		if (obj==kbserver) {
			settings.setService(Settings.service._REMOTE);
			pwrd.setEnabled(true);
			okbut.setEnabled(true);
		}
		
		else if (obj==localser) {
			settings.setService(Settings.service._LOCAL_SERVER);
			pwrd.setEnabled(false);
			okbut.setEnabled(true);
		}
		else if (obj==rdffile) {
			settings.setService(Settings.service._FILE);
			pwrd.setEnabled(false);
			okbut.setEnabled(true);
		}
	}
}
