package vprExplorer.modeltab;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableModel;

import vprExplorer.common.KBTable;
import vprExplorer.modeltab.AddModelWorkbench.WBEvent;
import vprExplorer.modeltab.tables.KBModelAnnotationTable;
import vprExplorer.modeltab.tables.ModelAnnotationTable;

public class SemSimModelTablePane extends JPanel implements Observer  {
	private static final long serialVersionUID = 1L;
	KBTable modelAnnTable, kbmodAnnTable;
	AddModelWorkbench workbench;
	
	public SemSimModelTablePane(AddModelWorkbench cb){
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		workbench = cb;
		
		workbench.addObserver(this);
		
		setBorder(BorderFactory.createLineBorder(Color.black));
		modelAnnTable = new KBTable(new ModelAnnotationTable());
		kbmodAnnTable = new KBTable(new KBModelAnnotationTable());
		
		add(new JScrollPane(modelAnnTable));
		add(new JScrollPane(kbmodAnnTable));
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 == WBEvent.modelloaded) {
			TableModel model = new ModelAnnotationTable(workbench.describeSemSimModel());
			modelAnnTable.setModel(model);
			kbmodAnnTable.setModel(new KBModelAnnotationTable(workbench.getKBModelEditor()));
		}
		if (arg1 == WBEvent.MODELPUSHED) {
			kbmodAnnTable.setModel(new KBModelAnnotationTable(workbench.getKBModelEditor()));
		}
	}
}
