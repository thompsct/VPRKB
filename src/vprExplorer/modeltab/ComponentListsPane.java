package vprExplorer.modeltab;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import vprExplorer.modeltab.AddModelWorkbench.WBEvent;
import vprExplorer.modeltab.lists.KBCompositeList;

public class ComponentListsPane extends JPanel implements ActionListener, Observer {
	private static final long serialVersionUID = -1356526802629136570L;
	
	private AddModelWorkbench workbench;
	private JList<String> modelComposites = new JList<String>();
	private KBCompositeList kblist;
	private JButton removeElement = new JButton("Remove Element"); 
	private JButton confirm = new JButton("Push Changes to Database");
	private JButton addComposite = new JButton("Add Element");
	private JButton addAll = new JButton("Add All");

	public ComponentListsPane(AddModelWorkbench call) {
		super(new GridBagLayout());
		workbench = call;
		workbench.addObserver(this);
		GridBagConstraints gbl = new GridBagConstraints();
		
		//Model List Label
		gbl.fill = GridBagConstraints.HORIZONTAL;
		gbl.weightx = 0.5;
		gbl.gridx = 0;
		gbl.gridy = 0;
		gbl.anchor = GridBagConstraints.FIRST_LINE_START;
		add(new JLabel("Model Composite List"), gbl);
		//DB Composite List

		gbl.fill = GridBagConstraints.HORIZONTAL;
		gbl.weightx = 0.5;
		gbl.gridx = 2;
		gbl.gridy = 0;
		gbl.anchor = GridBagConstraints.FIRST_LINE_END;
		add(new JLabel("KB Composite List"), gbl);
		
		//Actions for adding elements of a model to the KB
		JPanel modelActions = new JPanel();
		gbl.weightx = 0.333;
		gbl.gridx =0;
		gbl.gridy =2;
		gbl.anchor = GridBagConstraints.LAST_LINE_START;
		
		modelActions.setLayout(new BoxLayout(modelActions, BoxLayout.LINE_AXIS));
		
		JButton[] btnlist = {addComposite, addAll, removeElement,confirm};
		for (JButton btn : btnlist) {
			btn.addActionListener(this);
			btn.setEnabled(false);
		}
		
		modelActions.add(addComposite);
		modelActions.add(addAll);
		add(modelActions, gbl);
		
		//Actions for undoing or confirming changes
		JPanel kbActions = new JPanel();
		gbl.weightx = 0.333;
		gbl.gridx =2;
		gbl.gridy =2;
		gbl.anchor = GridBagConstraints.LAST_LINE_START;
		kbActions.setLayout(new BoxLayout(kbActions, BoxLayout.LINE_AXIS));	

		kbActions.add(removeElement);
		kbActions.add(confirm);
		add(kbActions, gbl);
	    
		//Lists of Model and KB elements
		modelComposites.addListSelectionListener(new ModelListSelectionListener(modelComposites));
		
		JScrollPane modelcompScroller = new JScrollPane(modelComposites);
	    modelcompScroller.setMinimumSize(new Dimension(300, 100));

	    gbl.fill = GridBagConstraints.HORIZONTAL;
		gbl.weightx = 0.333;
		gbl.gridx =0;
		gbl.gridy = 1;
		gbl.anchor = GridBagConstraints.LINE_START;
		add(modelcompScroller, gbl);

		kblist = new KBCompositeList(workbench);
		
	    gbl.fill = GridBagConstraints.HORIZONTAL;
		gbl.weightx = 0.333;
		gbl.gridx = 2;
		gbl.gridy = 1;
		gbl.anchor = GridBagConstraints.FIRST_LINE_END;
		add(kblist, gbl);
		validate();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		//Add Button
		if (obj==addComposite) {
			workbench.addSelectedComposite(modelComposites.getSelectedIndex());
		}
		if (obj==addAll) {
			workbench.addAllComposites();
		}
		if (obj==removeElement) {	
			workbench.removeComponent(modelComposites.getSelectedIndex());
		}
		if (obj==confirm) {
			workbench.pushBuffertoDatabase();
		}
	}
	
	private class ModelListSelectionListener implements ListSelectionListener {
		JList<String> target;
		ModelListSelectionListener(JList<String> list) {
			target = list;
		}
		
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {		
				addComposite.setEnabled(true);
				if (target.getSelectedIndex() != -1) {
					workbench.getSelectedSemSimEntryInformation(target.getSelectedIndex());
					
				}
			}
		}		
	};

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 == WBEvent.modelloaded) {
			modelComposites.setValueIsAdjusting(true);
			modelComposites.clearSelection();
			modelComposites.removeAll();
			String[] namelist = workbench.getNamesofModelComposites().toArray(new String[]{});
			modelComposites.setListData(namelist);
			modelComposites.setValueIsAdjusting(false);
			addAll.setEnabled(true);
		}
		if (arg1 == WBEvent.bufferupdated) {
			confirm.setEnabled(true);
		}

	}
	
	
	
}
