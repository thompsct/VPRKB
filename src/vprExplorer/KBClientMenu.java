package vprExplorer;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class KBClientMenu extends JMenuBar implements ActionListener{
	private static final long serialVersionUID = 1L;
	private GlobalActions gacts;
	private static JMenuItem quitMI;
	
	//Create menu
	public KBClientMenu(Settings sets, GlobalActions acts) {
		gacts = acts;
		setOpaque(true);
		setPreferredSize(new Dimension(sets.getScreenWidth(), 20));
			
		JMenu fileMenu = new JMenu("File");
		
					
		quitMI = new JMenuItem("Quit");
		quitMI.addActionListener(this);
			
		fileMenu.add(quitMI);
		add(fileMenu);
		
		
	}
	
	//Callback
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		//File Menu
		if (o == quitMI)  gacts.quit();  //Quit application
	}
}
