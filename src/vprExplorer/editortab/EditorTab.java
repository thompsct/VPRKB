package vprExplorer.editortab;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import vprExplorer.Settings;
import vprExplorer.common.KBTree;

public class EditorTab extends JPanel {
	private static final long serialVersionUID = 1L;
	private Settings settings;
	
	public EditorTab(Settings global) {
		settings = global;
		
		setOpaque(false);
		setLayout(new BorderLayout());
		setVisible(true);
		add(new EditTabLeft(), BorderLayout.WEST);
		add(new EditTabRight(), BorderLayout.CENTER);
	}
	
	//Editor Panel Layout - left side
	private class EditTabLeft extends JPanel implements ActionListener, TreeSelectionListener {
		private static final long serialVersionUID = 1L;
		JButton searchbtn;
		protected JTree kbt; 
		public EditTabLeft() {
			super(new BorderLayout());
			setPreferredSize(new Dimension(settings.getScreenWidth()/5, settings.getScreenHeight()));
			//Search Panel
			searchPanel();
			treeView();
			setVisible(true);
			setBorder(BorderFactory.createEtchedBorder());
		}

		private void treeView() {
			kbt = new JTree(new KBTree(settings));
			kbt.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			kbt.addTreeSelectionListener(this);
			
			JScrollPane treeView = new JScrollPane(kbt);
			
			JPanel treePane = new JPanel();
			treePane.setPreferredSize(new Dimension(settings.getScreenWidth(), 5*settings.getScreenHeight()/6));
			treePane.setBorder(BorderFactory.createEtchedBorder());
			treePane.add(treeView);
			add(treePane, BorderLayout.CENTER);
		}

		private void searchPanel() {
			JPanel srchPane = new JPanel(new GridBagLayout());
			srchPane.setPreferredSize(new Dimension(settings.getScreenWidth(), settings.getScreenHeight()/6));
						
			GridBagConstraints gbl = new GridBagConstraints();
			//Search Button
			searchbtn = new JButton("Search");
			searchbtn.addActionListener(this);
			searchbtn.setVisible(true);
			gbl.fill = GridBagConstraints.HORIZONTAL;
			gbl.weightx = 0.5;
			gbl.gridx = 0;
			gbl.gridy = 0;
			gbl.anchor = GridBagConstraints.FIRST_LINE_END;
			srchPane.add(searchbtn, gbl);
			srchPane.setBorder(BorderFactory.createEtchedBorder());
			add(srchPane, BorderLayout.NORTH);

		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource().equals(searchbtn)) {				
				
			}
			
		}

		@Override
		public void valueChanged(TreeSelectionEvent arg0) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)kbt.getLastSelectedPathComponent();
			KBTree tmod = (KBTree)kbt.getModel();
			
			if (node.getParent()==tmod.getRoot()) {
				//tmod.loadLeaves(node);
			}
		}
	}
	
	//Editor Panel Layout - right side
	private class EditTabRight extends JPanel implements ActionListener {
		private static final long serialVersionUID = 1L;
		
		public EditTabRight() {
			super(new BorderLayout());
			//Search Panel
			setVisible(true);
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}
	
}
