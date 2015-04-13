package vprExplorer.modeltab;
//Methods for KB Model Adder User Interface
import java.awt.*;

import javax.swing.*;

import vprExplorer.Settings;

public class KBAddModelTab extends JPanel {

	private AddModelWorkbench callback;
	private static final long serialVersionUID = 1L;
	private Settings settings;
	
	public KBAddModelTab(Settings global) {
		settings = global;
		
		setOpaque(false);
		setLayout(new BorderLayout());
		
		callback = new AddModelWorkbench(settings);

		modelTabSidebar();
		mainModelPanel();
		
		validate();
		setVisible(true);	
	}

	public void mainModelPanel() {
		JPanel mainpanel = new JPanel();
		mainpanel.setLayout(new BoxLayout(mainpanel, BoxLayout.Y_AXIS));
		mainpanel.setPreferredSize(settings.getScreenDimensions());

		ComponentListsPane complistpanel = new ComponentListsPane(callback);	
		SemSimModelTablePane modview = new SemSimModelTablePane(callback); 
		CompAnnTablePane compview = new CompAnnTablePane(callback);
		
		mainpanel.add(complistpanel);
		mainpanel.add(modview);
		mainpanel.add(compview);
	    
	    mainpanel.setBackground(Color.WHITE);
	    mainpanel.setBorder(BorderFactory.createLoweredBevelBorder());
	    mainpanel.validate();
		add(mainpanel, BorderLayout.CENTER);
	}

	public void modelTabSidebar() {
		JPanel sidebar = new JPanel(new BorderLayout());
		sidebar.setBorder(BorderFactory.createRaisedBevelBorder());
		
		sidebar.add(new OpenModelPanel(callback));
		add(sidebar, BorderLayout.WEST);
	}
}


