package vprExplorer.modeltab.tables;

import semsimKB.annotation.CurationalMetadata.Metadata;
import semsimKB.utilities.descriptors.KBModelEditor;
import vprExplorer.common.KBTableModel;

public class KBModelAnnotationTable extends KBTableModel {
	private static final long serialVersionUID = 1L;
	KBModelEditor descriptor = null;

	public KBModelAnnotationTable() {
		addRow(new String[]{"Model not loaded", ""}, new PhysKBTableRenderer[]{heading, normal});
	}		
	
	public KBModelAnnotationTable(KBModelEditor desc) {
		descriptor = desc;
		setTable();
	}
	
	protected void setTable() {
		addRow(new String[]{"Model Name:", descriptor.getName()}, new PhysKBTableRenderer[]{heading, new LabelRenderer(false, false, descriptor.getStatus().getColor())});
		addRow(new String[]{"URI:", descriptor.getURI().toString()}, dfltrowrenderer);
		for (Metadata meta : Metadata.values()) {
			DataSetter tar = (String txt) -> descriptor.setMetaDataValue(meta, txt);
			addRow(new String[]{meta.name(), descriptor.getMetadataValue(meta)}, new PhysKBTableRenderer[]{heading, new TextareaRenderer(tar)});		
		}
	}

	@Override
	public void updateTable() {
		clear();
		setTable();
		fireTableDataChanged();
	}	
}
