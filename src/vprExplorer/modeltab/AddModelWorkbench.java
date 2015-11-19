package vprExplorer.modeltab;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import semsimKB.annotation.CurationalMetadata;
import semsimKB.annotation.CurationalMetadata.Metadata;
import semsimKB.model.ModelLite;
import semsimKB.model.physical.CompositePhysicalEntity;
import semsimKB.reading.SemSimOWLreader;
import semsimKB.utilities.SemSimComponentComparator;
import semsimKB.utilities.SemSimObjectCollection;
import semsimKB.utilities.descriptors.CompositeEntityDescriptor;
import semsimKB.utilities.descriptors.KBCompositeEditor;
import semsimKB.utilities.descriptors.KBModelEditor;
import vprExplorer.Settings;
import vprExplorer.buffer.ComponentStatus;
import vprExplorer.buffer.KBBufferOperations;
public class AddModelWorkbench extends Observable {
	public enum WBEvent {modelloaded, bufferupdated, sscompslct, dbcompslct, MODELPUSHED};
	
	private Settings globals;
	private KBBufferOperations kbbuffer;
	
	
	private ModelLite curmodel;
	private ArrayList<CompositePhysicalEntity> modcomplist;
	private int curcompsel = -1;
	private KBModelEditor kbmodeleditor;
	private KBCompositeEditor kbcompeditor;
		
	//Set local knowledge base buffer to be used
	
	public AddModelWorkbench(Settings global) {
		globals = global;
	}
	
	public String loadSemSimModel(File owlfile) {
		SemSimOWLreader ssReader = new SemSimOWLreader();
		try {
			curmodel = ssReader.readFromFile(owlfile);
		}
		catch (Exception e) {
			e.printStackTrace();
			return "Failed to load OWL file";
		}
		loadModelComposites();
		createBuffer();
		setChanged();
		notifyObservers(WBEvent.modelloaded);
		return "Loaded: " + owlfile.getName();
	}
	
	private void createBuffer() {
		kbbuffer = new KBBufferOperations(globals, curmodel);
		kbbuffer.compareModeltoKB(modcomplist);
		kbmodeleditor = kbbuffer.createModelEditor();
	}
	
	private void loadModelComposites() {
		modcomplist = new ArrayList<CompositePhysicalEntity>(curmodel.getCompositePhysicalEntities());
		Collections.sort(modcomplist, new SemSimComponentComparator());
	}
	
	public ArrayList<String> getNamesofModelComposites() {
		return SemSimObjectCollection.getListofComponentNames(modcomplist);
	}
	
	//Add Object to buffer
	public void addSelectedComposite(int selection) {
		if (kbbuffer.modifyComposite(selection, modcomplist.get(selection))) {
			kbbuffer.compareBufferComposites(modcomplist);
			setChanged();
			notifyObservers(WBEvent.bufferupdated);
		}
	}
	
	
	//Add all composites
	public void addAllComposites() {
		int i =0;
		for (CompositePhysicalEntity cpe : modcomplist) {
			kbbuffer.modifyComposite(i, cpe);
			i++;
		}
		setChanged();
		notifyObservers(WBEvent.bufferupdated);
	}
	
	//Remove composite from buffer
	public void removeComponent(int index) {
		kbbuffer.removeBufferComponent(index);
	}
	
	public void pushBuffertoDatabase() {
		kbbuffer.pushBuffertoDatabase();
		createBuffer();
		setChanged();
		notifyObservers(WBEvent.MODELPUSHED);
	}
	//**************************SELECTIONS********************************//
	
	public void getSelectedSemSimEntryInformation(int index) {
		curcompsel = index;
		setChanged();
		notifyObservers(WBEvent.sscompslct);
	}
	
	
	public void getSelectedDBEntryInformation(int index) {
		kbcompeditor = kbbuffer.createCompositeEditor(index);
		setChanged();
		notifyObservers(WBEvent.dbcompslct);
	} 
	
	public ArrayList<Triple<String, ComponentStatus, Boolean>>  getAllKBComposites() {
		return kbbuffer.getAllBufferCompositesNamesandStatuses();
	}
	
	public Triple<String, ComponentStatus, Boolean> getEntry(int index) {	
		return Triple.of(kbbuffer.getCompositeName(index), kbbuffer.getCompositeStatus(index), kbbuffer.getCompositetobeModified(index));
	}
	
	public 	ArrayList<Pair<String, String>> describeSemSimModel() {
		CurationalMetadata metadata = curmodel.getCurationalMetadata();
		ArrayList<Pair<String, String>> metadatastrings = new ArrayList<Pair<String, String>>();
		for (Metadata m : Metadata.values()) {
			metadatastrings.add(Pair.of(m.toString(), metadata.getAnnotationValue(m)));
		}
		return metadatastrings;
	}
	
	public CompositeEntityDescriptor describeSemSimComposite() {
		return new CompositeEntityDescriptor(modcomplist.get(curcompsel));
	}
	
	public KBModelEditor getKBModelEditor() {
		return kbmodeleditor;
	}
	
	public KBCompositeEditor getKBCompositeEditor() {
		return kbcompeditor;
	}
}
