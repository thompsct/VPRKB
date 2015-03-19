package vprExplorer;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import vprExplorer.utilities.CommonDialog;
import vprExplorer.utilities.ServiceSelectionDialog;
import java.io.*;
import java.util.Observable;
import java.util.Observer;

public class VPRKBInterface extends JFrame implements Observer{
	private static final long serialVersionUID = 1L;
	private Settings globals = new Settings();
	private GlobalActions gacts = new GlobalActions();
	
	//main function
	public static void main(String[] args) throws IOException,
		FileNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		//Log.setCmdLogging(); 
		System.out.println("Loading VPR KB Interface...");	
		
		 SwingUtilities.invokeLater(new Runnable() {
		     public void run() {
		    	 try {
					createAndShowInterface();
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
		     }
		 });   
	}
	
	public static void createAndShowInterface() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.put("nimbusOrange", new Color(51,98,140));
		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	    for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
	        if ("Nimbus".equals(info.getName())) {
	            UIManager.setLookAndFeel(info.getClassName());
	            break;
	        }
		}
	    
	    JFrame frame = new VPRKBInterface();
	    CommonDialog.setFrame(frame);
	    
	    frame.setVisible(true);
	}
	
	//Main application frame
	public VPRKBInterface() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				quit();
			}
		});
		new ServiceSelectionDialog(globals, gacts);
		setTitle("Virtual Physiological Rat Knowledge Base Client");
		gacts.addObserver(this);
		
		setJMenuBar(new KBClientMenu(globals, gacts));

		KBClientGUI client = new KBClientGUI(globals, gacts);
		setContentPane(client);
		
		pack();
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		setVisible(true);
		
		
		System.out.println("...Loaded.");
	}

	//quit routine
	public static void quit() {
		System.exit(0);
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 ==GlobalActions.appactions.QUIT) {
			try {
				quit();
			} catch (HeadlessException e) {
				e.printStackTrace();
			}
		}
		
	}
}
