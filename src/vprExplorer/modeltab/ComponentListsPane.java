package vprExplorer.modeltab;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import vprExplorer.common.KBList;
import vprExplorer.common.KBListModel;
import vprExplorer.modeltab.VPRKBModelCallBack.KBModelList;

public class ComponentListsPane extends JPanel implements ActionListener {
	private static final long serialVersionUID = -1356526802629136570L;
	
	VPRKBModelCallBack callback;
	public KBList modelComposites, vprDB;
	public JButton NewEntry, removeElement, confirm, addComposite, addAll;
	JScrollPane modelcompScroller, kbcompScroller;
	
	public ComponentListsPane(VPRKBModelCallBack call) {
		super(new GridBagLayout());
		callback = call;
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
		addComposite = new JButton("Add Element");
		addAll = new JButton("Add All");
		removeElement = new JButton("Remove Element");
		confirm = new JButton("Push Changes to Database");
		
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
		modelComposites = new KBList(callback.initSemSimListModel(), false);
		modelComposites.addListSelectionListener(selectionlistener);
		modelComposites.getModel().addListDataListener(datalistener);
		
		modelcompScroller = new JScrollPane(modelComposites);
	    modelcompScroller.setPreferredSize(new Dimension(300, 200));

	    gbl.fill = GridBagConstraints.HORIZONTAL;
		gbl.weightx = 0.333;
		gbl.gridx =0;
		gbl.gridy = 1;
		gbl.anchor = GridBagConstraints.LINE_START;
		add(modelcompScroller, gbl);

		vprDB = new KBList(callback.initKBListModel(), true);
		vprDB.addListSelectionListener(selectionlistener);
		vprDB.getModel().addListDataListener(datalistener);
		
	    kbcompScroller = new JScrollPane(vprDB);
	    kbcompScroller.setPreferredSize(new Dimension(300, 200));
	    gbl.fill = GridBagConstraints.HORIZONTAL;
		gbl.weightx = 0.333;
		gbl.gridx = 2;
		gbl.gridy = 1;
		gbl.anchor = GridBagConstraints.FIRST_LINE_END;
		add(kbcompScroller, gbl);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		KBModelList lstmodel = (KBModelList) vprDB.getModel();
		//Add Button
		vprDB.setValueIsAdjusting(true);
		if (obj==addComposite) {
			int index = callback.addButton(modelComposites.getSelectedValue()[0].toString());
			if (index == -2) return;
			vprDB.clearSelection();	
			if (index == -1) {
				vprDB.setSelectedIndex(lstmodel.indexOf(lstmodel.lastElement()));
			} else {
				vprDB.setSelectedIndex(0);
			}
		}
		if (obj==addAll) {
			callback.addAllButton();
		}
		if (obj==removeElement) {	
			if (vprDB.isSelectionEmpty()) return;
			String rEle = vprDB.getSelectedValue()[0].toString();
			int index = vprDB.getSelectedIndex();
			vprDB.clearSelection();
			callback.removefromKBButton(rEle, index);
			
		}
		if (obj==confirm) {
			vprDB.clearSelection();
			callback.pushtoDBButton();
			vprDB.setSelectedIndex(lstmodel.indexOf(lstmodel.lastElement()));
		}
		vprDB.setValueIsAdjusting(false);	
	}
	
	private ListSelectionListener selectionlistener = new ListSelectionListener() {

		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
					if (e.getSource().equals(modelComposites) && (modelComposites.getSelectedIndex()!=-1)) {
						String selection = modelComposites.getSelectedValue()[0].toString();
						callback.dispSelectedModCompositeInfo(selection);

					}
					if (e.getSource().equals(vprDB) && (vprDB.getSelectedIndex()!=-1)) {
						callback.dispSelectedKBComponentInfo(vprDB.getSelectedValue()[0].toString());
					}
				}
		}
		
	};
	
	private ListDataListener datalistener = new ListDataListener() {

		@Override
		public void contentsChanged(ListDataEvent e) {
			KBListModel model;
			if (e.getSource().equals(modelComposites.getModel())) {
				model = modelComposites.getModel();
				boolean enable = !model.isEmpty();
				addComposite.setEnabled(enable); 
				addAll.setEnabled(enable); 
			}
			if (e.getSource().equals(vprDB.getModel())) {
				model = vprDB.getModel();
				boolean enable = !model.isEmpty();
				confirm.setEnabled(enable); 
				removeElement.setEnabled(enable); 			
			}
		}

		@Override
		public void intervalAdded(ListDataEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void intervalRemoved(ListDataEvent arg0) {
			// TODO Auto-generated method stub
		}
		
	};
	
	
	
}
