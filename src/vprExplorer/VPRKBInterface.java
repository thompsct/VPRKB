package vprExplorer;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.*;

import javax.swing.*;

import vprExplorer.datasettab.DatasetTab;
import vprExplorer.editortab.EditorTab;
import vprExplorer.modeltab.VPRKBAddModelPane;
import vprExplorer.utilitiestab.UtilitiesTab;

import java.io.*;

public class VPRKBInterface extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	public static JMenu fileMenu, optionsMenu, helpMenu;
	public static JMenuItem newMI, closeMI, quitMI;
	public static JMenuBar MenuBar;
	public static JTabbedPane TabPane;
	public JDialog SelectD;
	static Settings globals = new Settings();
	
	public static int initxpos = 0;
	public static int initypos = 0;
	public static GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	public static int screenwidth = gd.getDisplayMode().getWidth();
	public static int screenheight = gd.getDisplayMode().getHeight();
	public static Dimension screenSize = new Dimension(screenwidth, screenheight);
	public static int numtabs = 0;

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
					// TODO Auto-generated catch block
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

		TabPane = new JTabbedPane(); // a specialized layered pane
		TabPane.setOpaque(true);
		TabPane.setBackground(Color.white);		
		TabPane.setSize(screenSize);
		
		setContentPane(TabPane);	
		
		StoreMethodDialog smd = new StoreMethodDialog(this);
		smd.setBounds(3*screenwidth/7, screenheight/4, screenwidth/6, screenheight/7);
		smd.setAlwaysOnTop(true);
		smd.setModalityType(ModalityType.APPLICATION_MODAL);	
		
		menu();
	
		setTitle("Virtual Physiological Rat");
		setPreferredSize(screenSize);
		setLocation(initxpos, initypos);
		
		KBInterface();
		pack();
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		setVisible(true);

		System.out.println("...Loaded.");
	}
	
	//Create menu
	public void menu() {
		MenuBar = new JMenuBar();
		MenuBar.setOpaque(true);
		MenuBar.setPreferredSize(new Dimension(screenwidth, 20));
			
		fileMenu = new JMenu("File");
		MenuBar.add(fileMenu);
			
		closeMI = new JMenuItem("Close");
		closeMI.addActionListener(this);
			
		quitMI = new JMenuItem("Quit");
		quitMI.addActionListener(this);
			
		fileMenu.add(closeMI);
		fileMenu.add(quitMI);
	
		setJMenuBar(MenuBar);
	}
	
	//Callback
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		//File Menu
		if (o == quitMI)  quit();  //Quit application
	}
	
	//quit routine
	public static void quit() {
		System.exit(0);
	}
	
	//Open Knowledge Base Client
	public int KBInterface() {
		Dimension panedim = new Dimension(3*TabPane.getWidth()/4, TabPane.getHeight());
		
		VPRKBAddModelPane addmodel = new VPRKBAddModelPane(panedim, globals);
		numtabs ++;
		EditorTab editdb = new EditorTab(panedim, globals);
		numtabs ++;
		UtilitiesTab dbutils = new UtilitiesTab(panedim, globals);
		numtabs ++;
		DatasetTab dataset = new DatasetTab(panedim, globals);
		numtabs ++;
		TabPane.addTab("Upload Model", addmodel);
		TabPane.addTab("Edit Database", editdb);
		TabPane.addTab("Datasets", dataset);
		TabPane.addTab("Utilities", dbutils);
		
		TabPane.setSelectedComponent(TabPane.getComponentAt(0));
		
		return 0;
	}
	
	protected class StoreMethodDialog extends JDialog implements ActionListener {
		private static final long serialVersionUID = 1L;
		JButton okbut, cclbut;
		ButtonGroup service;
		JRadioButton kbserver, localser, rdffile;
		JPasswordField pwrd;
		Settings.service sel;
		
		public StoreMethodDialog(Frame owner) {
			super(owner);

			setTitle("Select RDF Service");
			setLayout(new GridLayout(0,2));
			setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
			
			kbserver = new JRadioButton("KB Server");
			localser = new JRadioButton("Local Server");
			rdffile = new JRadioButton("Local File");
			
			service = new ButtonGroup();		

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
						
			//service.setSelected(kbserver, true);
			
			okbut = new JButton("Connect");
			okbut.setEnabled(false);
			okbut.addActionListener(this);
			cclbut = new JButton("Quit");
			cclbut.addActionListener(this);
			add(okbut);
			add(cclbut);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Object obj = arg0.getSource();

			if (obj == cclbut) {
				quit();
			}
			else if (obj==okbut){
				globals.setService(sel);
				this.dispose();
			}
			if (obj==kbserver) {
				sel = Settings.service._REMOTE;
				pwrd.setEnabled(true);
				okbut.setEnabled(true);
			}
			
			else if (obj==localser) {
				sel = Settings.service._LOCAL_SERVER;
				pwrd.setEnabled(false);
				okbut.setEnabled(true);
			}
			else if (obj==rdffile) {
				sel = Settings.service._FILE;
				pwrd.setEnabled(false);
				okbut.setEnabled(true);
			}
		}
	}
}
