package vprExplorer.modeltab.tables;

import java.util.ArrayList;

import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang3.tuple.Pair;

import vprExplorer.common.KBTableModel;

public class ModelAnnotationTable extends KBTableModel {
	private static final long serialVersionUID = 1L;

	public ModelAnnotationTable() {
		addRow(new String[]{"Model not loaded", ""}, new TableCellRenderer[]{heading, normal});
	}

	
	public ModelAnnotationTable(ArrayList<Pair<String,String>> data) {
		for (Pair<String,String> datapair : data) {
			addRow(new String[]{datapair.getLeft(), datapair.getRight()}, new TableCellRenderer[]{heading, normal});
		}
	}

	@Override
	public void updateTable() {
		
	}
}