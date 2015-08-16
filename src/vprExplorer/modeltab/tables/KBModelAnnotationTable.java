package vprExplorer.modeltab.tables;

import javax.swing.table.TableCellRenderer;

import semsimKB.annotation.CurationalMetadata.Metadata;
import semsimKB.utilities.descriptors.KBModelEditor;
import vprExplorer.common.KBTableModel;

public class KBModelAnnotationTable extends KBTableModel {
	private static final long serialVersionUID = 1L;

	public KBModelAnnotationTable() {
		addRow(new String[]{"Model not loaded", ""}, new TableCellRenderer[]{heading, normal});
	}		
	
	public KBModelAnnotationTable(KBModelEditor descriptor) {
		addRow(new String[]{"Model Name:", descriptor.getName()}, new TableCellRenderer[]{heading, new LabelRenderer(false, false, descriptor.getStatus().getColor())});
		addRow(new String[]{"URI:", descriptor.getURI().toString()}, dfltrowrenderer);
		for (Metadata meta : Metadata.values()) {
			addRow(new String[]{meta.name(), descriptor.getMetadataValue(meta)}, new TableCellRenderer[]{heading, new TextareaRenderer()});		
		}
				
	}

	@Override
	public void updateTable() {
		
	}	
}
