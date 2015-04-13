package vprExplorer.modeltab.lists;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang3.tuple.Triple;

import vprExplorer.buffer.ComponentStatus;
import vprExplorer.modeltab.AddModelWorkbench;
import vprExplorer.modeltab.AddModelWorkbench.WBEvent;

public class KBCompositeList extends JScrollPane implements Observer {
	private static final long serialVersionUID = 1L;
	AddModelWorkbench workbench;
	JList<Entry> kblist = new JList<Entry>();
	ArrayList<Entry> entries;
	
	public KBCompositeList(AddModelWorkbench bench) {
		workbench = bench;
		workbench.addObserver(this);
		
		kblist.setCellRenderer(new ColorRenderer());
		kblist.addListSelectionListener(new KBListSelectionListener(kblist));
		setMinimumSize(new Dimension(300, 100));
		this.setViewportView(kblist);
	}

	private void updateList() {
		kblist.setValueIsAdjusting(true);
		kblist.clearSelection();
		kblist.setListData(entries.toArray(new Entry[]{}));
		kblist.setValueIsAdjusting(false);
	}
	
	private void loadEntries() {
		entries = new ArrayList<Entry>();
		for (Triple<String, ComponentStatus, Boolean> triple : workbench.getAllKBComposites()) {
			entries.add(new Entry(triple.getLeft(), triple.getMiddle().getColor(), triple.getRight()));
		}
		updateList();
	}
	
	public void removeElement() {
		int index = kblist.getSelectedIndex();
		workbench.removeComponent(index);
		loadEntries();
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 == WBEvent.modelloaded || arg1 == WBEvent.bufferupdated) {
			loadEntries();
		}
	}
	
	private class Entry {
		String name;
		Color bgcolor;
		Boolean bolded;
		protected Entry(String n, Color c, boolean bold) {
			name = n; bgcolor = c; bolded = bold;
		}
	}
	
	private class ColorRenderer extends JLabel implements ListCellRenderer<Entry> {
		Font bolded = new Font("arial", Font.BOLD, 12);
		Font arial = new Font("arial", Font.PLAIN, 12);
		
		private static final long serialVersionUID = 1L;
		public ColorRenderer() {
		       setOpaque(true); //MUST do this for background to show up.
		       
		  }

		@Override
		public Component getListCellRendererComponent(
				JList<? extends Entry> list, Entry value, int index,
				boolean isSelected, boolean cellHasFocus) {
			if (list.isSelectedIndex(index)) setBackground(value.bgcolor);				 
			else setBackground(value.bgcolor);
			
			if (value.bolded) setFont(bolded);
			else setFont(arial);
			
			setText(value.name);
			return this;
		}

	}
	
	private class KBListSelectionListener implements ListSelectionListener {
		JList<Entry> target;
		KBListSelectionListener(JList<Entry> list) {
			target = list;
		}
		
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				workbench.getSelectedDBEntryInformation(target.getSelectedIndex());
			}
		}
	};
}
