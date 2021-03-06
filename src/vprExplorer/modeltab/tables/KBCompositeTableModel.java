package vprExplorer.modeltab.tables;

import org.apache.commons.lang3.tuple.Pair;

import semsimKB.utilities.descriptors.KBCompositeEditor;
import vprExplorer.buffer.ComponentStatus;
import vprExplorer.common.KBTableModel;

public class KBCompositeTableModel extends KBTableModel {
	private static final long serialVersionUID = 1L;

	public KBCompositeTableModel() {
		addRow(new String[]{"No composite selected", ""}, new PhysKBTableRenderer[]{heading, heading});
	}
	
	public KBCompositeTableModel(KBCompositeEditor descriptor) {
		addRow(new String[]{"Name:", descriptor.getName()}, new PhysKBTableRenderer[]{heading, new LabelRenderer(false, false, descriptor.getStatus().getColor())});
		addRow(new String[]{"URI:", descriptor.getURI().toString()}, dfltrowrenderer);
		
		addRow(descriptor.getComponents().get(0), new String[]{"", descriptor.getAdditionalInformation(0)},
				new PhysKBTableRenderer[]{heading, new LabelRenderer(false, false, descriptor.getFirstComponentStatus().getColor())});
		if (descriptor.getSecondComponentStatus()!=null) {
			addRow(descriptor.getComponents().get(1), new String[]{"", descriptor.getAdditionalInformation(1)}, 
				new PhysKBTableRenderer[]{heading, new LabelRenderer(false, false, descriptor.getSecondComponentStatus().getColor())});
		}
		
		for (int i = 0; i < descriptor.getProperties().size(); i++) {
			Pair<String, ComponentStatus> ppstat = descriptor.getProperties().get(i);
			addRow(new String[]{"Property Association:", ppstat.getLeft()}, 
					new PhysKBTableRenderer[]{heading, new LabelRenderer(false, false, ppstat.getRight().getColor())});
			for (String s :descriptor.getAssociatedModels(i)) {
				addRow(new String[]{"", s}, dfltrowrenderer);
			}
		}
	}
	
	@Override
	public void updateTable() {
	
		
	}

}
