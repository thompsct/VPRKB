package vprExplorer.common;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;

public abstract class KBListModel extends AbstractListModel<Object[]>{
	private static final long serialVersionUID = 1L;
	protected List<Object[]> data = new LinkedList<Object[]>();
	protected HashMap<Integer, Color> rowstocolor = new HashMap<Integer, Color>();
	protected HashSet<Integer> bolded = new HashSet<Integer>();
	
	static final Object[] listdflt = new Object[]{"No Entries Found"};

	public final void clearList() {
		clear();
		clearColors();
		addElement(listdflt);
		fireContentsChanged(this, 0, getSize());
	}
	
	public void clear() {
		data.clear();
		clearColors();
		fireContentsChanged(this, 0, getSize());
	}

	public void addElement(Object[] e) {
		data.add(e);
		fireContentsChanged(this, 0, getSize());
	}

	public Object get(int row) {
		return data.get(row)[0];
	}
	
	public List<Object[]> getAll() {
		return data;
	}
	
	public Object[] getElementAt(int row) {
		return data.get(row);
	}
	
	 public Object[] remove(int row) {
		 Object[] removeditem = data.get(row);
		 data.remove(row);
		 fireContentsChanged(this, 0, getSize());
		 return removeditem;
	 }
	
	 public void removeElementAt(int row) {
		 data.remove(row);
		 fireContentsChanged(this, 0, getSize());
	 }
	 
	 public int getSize() {
		return data.size();
	}
	 
	public final boolean isEmpty() {
		if (data.contains(listdflt)) return true;
		return false;
	}

	public abstract void DisplayComponents();
	
	public Color getRowColor(int row) {
		return rowstocolor.get(row);
	}
	
	public void setRowtoColor(int i, Color c) {
		rowstocolor.put(i, c);
	}
	
	public void clearColors() {
		rowstocolor.clear();
	}
	
	public boolean getBolded(int row) {
		return bolded.contains(row);
	}
	
	public void setRowtoBold(Integer ic) {
		bolded.add(ic);
	}
	
	public void clearBold() {
		bolded.clear();
	}
	
	public void removeBold(Integer ic) {
		bolded.remove(ic);
	}
	
	public Object lastElement() {
		
		return data.get(getSize()-1);
	}
	
	public int indexOf(Object lastElement) {
		for ( Object o : data) {
			if (o.toString().equals(lastElement))
			return data.indexOf(o);
		}
		return -1;
		
	}
	
}
