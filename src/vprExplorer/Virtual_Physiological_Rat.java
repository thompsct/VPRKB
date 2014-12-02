package vprExplorer;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.*;

import javax.swing.*;

import org.apache.jena.atlas.logging.Log;

import vprKBDataSetTab.DatasetTab;
import vprKBEditorTab.EditorTab;
import vprKBModelTab.VPRKBAddModelPane;
import vprKBUtilitiesTab.UtilitiesTab;

import java.io.*;

public class Virtual_Physiological_Rat extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	public static JMenu fileMenu, optionsMenu, helpMenu;
	public static JMenuItem newMI, closeMI, quitMI;
	public static JMenuBar MenuBar;
	public static JTabbedPane TabPane;
	public JDialog SelectD;
	static Globals globals = new Globals();
	
	public static int initxpos = 0;
	public static int initypos = 0;
	public static GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	public static int screenwidth = gd.getDisplayMode().getWidth();
	public static int screenheight = gd.getDisplayMode().getHeight();
	public static Dimension screenSize = new Dimension(screenwidth, screenheight);
	public static int numtabs = 0;

	//main function
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException,
		FileNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		//Log.setCmdLogging(); 
		frame = new Virtual_Physiological_Rat();
				
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.dispose();
				quit();
			}
		});
	}
//Main application frame
	public Virtual_Physiological_Rat() {	
		System.out.println("Loading VPR KB Interface...");	
		
		TabPane = new JTabbedPane(); // a specialized layered pane
		TabPane.setOpaque(true);
		TabPane.setBackground(Color.white);		
		TabPane.setSize(screenSize);
		
		setContentPane(TabPane);	
		
		StoreMethodDialog smd = new StoreMethodDialog(frame);
		smd.setBounds(3*screenwidth/7, screenheight/4, screenwidth/6, screenheight/7);
		smd.setAlwaysOnTop(true);
		smd.setModalityType(ModalityType.APPLICATION_MODAL);
		smd.setVisible(true);	
		
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
	
	//Open Ontology Explorer
	public static int KBInterface() {
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
		Globals.service sel;
		
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
				sel = Globals.service._REMOTE;
				pwrd.setEnabled(true);
				okbut.setEnabled(true);
			}
			
			else if (obj==localser) {
				sel = Globals.service._LOCAL_SERVER;
				pwrd.setEnabled(false);
				okbut.setEnabled(true);
			}
			else if (obj==rdffile) {
				sel = Globals.service._FILE;
				pwrd.setEnabled(false);
				okbut.setEnabled(true);
			}
		}
	}
}
