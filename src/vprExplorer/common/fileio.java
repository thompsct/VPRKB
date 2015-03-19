package vprExplorer.common;
//Read/write for models and any local knowledge base
import java.awt.Container;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.*;

import semsimKB.model.KnowledgeBase;
import semsimKB.model.ModelLite;
import semsimKB.reading.KBOWLreader;
import semsimKB.reading.SemSimOWLreader;
import semsimKB.writing.KBOWLwriter;

public class fileio {
	private File owlfile;
	JLabel log;
	public fileio(String path,JLabel logger) {owlfile = new File(path); log=logger;}
	public fileio(JLabel logger) {log = logger;}
	
	public boolean chooseNewModel(Container frame) {
		final JFileChooser ofd = new JFileChooser();

		ofd.setFileFilter(new FileNameExtensionFilter("OWL file", "owl"));
		int returnVal = ofd.showOpenDialog(frame);
		
		if (returnVal==JFileChooser.APPROVE_OPTION) {
			owlfile = ofd.getSelectedFile();
			return true;
		}
		return false;
	}
	
	public ModelLite loadSemSimModel() {
		ModelLite semsimmodel = new ModelLite();
		SemSimOWLreader ssReader = new SemSimOWLreader();
		try {
			ssReader.readFromFile(owlfile, semsimmodel);
		} 
		catch (Exception e) {
			e.printStackTrace();
			if (!log.equals(null)) log.setText("Failed to load OWL file");
			return null;
		}
		if (log!=null) log.setText("Loaded: " + owlfile.getName());
		return semsimmodel;
	}
	
	public KnowledgeBase loadKnowledgeBase() {
		KnowledgeBase kb;
		KBOWLreader kbReader = new KBOWLreader();
		try {
			kb = kbReader.readFromFile(owlfile);
			return kb;
		} 
		catch (Exception e) {
			e.printStackTrace();
			}
		return kb=null;
	}
	
	public int writeKnowledgeBase(KnowledgeBase kbbuffer) {
		if (!owlfile.canWrite()) System.out.println("Write Failed");
		KBOWLwriter kbWriter = new KBOWLwriter(kbbuffer, owlfile);
		try {
			kbWriter.writeToFile();
			return 0;
		} 
		catch (Exception e) {
			e.printStackTrace();
			}
		return -1;
	}
}
