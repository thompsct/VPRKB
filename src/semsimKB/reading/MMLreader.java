package semsimKB.reading;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.semanticweb.owlapi.model.OWLException;

import JSim.aserver.ASModel;
import JSim.aserver.ASServer;
import JSim.data.NamedVal;
import JSim.util.UtilIO;
import JSim.util.Xcept;

import semsimKB.model.ModelLite;

public class MMLreader {

	public int srcType = -1;
	protected ModelLite KBModel;
	protected Document doc;
	public String jsimBuildDir;

	public MMLreader(String jsimBuildDir){
		this.jsimBuildDir = jsimBuildDir;
		 System.setProperty("jsim.home", jsimBuildDir);
	}
	
	public ModelLite readFromFile(File file) throws JDOMException, IOException, Xcept, InterruptedException, OWLException {
		
		String fname = file.getName();
		if (fname.endsWith(".mod")) srcType = ASModel.TEXT_MML;
	    if (fname.endsWith(".xml")) srcType = ASModel.TEXT_XML;
	    if (fname.endsWith(".sbml")) srcType = ASModel.TEXT_SBML;
	    if (fname.endsWith(".cellml")) srcType = ASModel.TEXT_CELLML;
	    if(fname.endsWith(".xml.origin")) srcType = ASModel.TEXT_SBML;
	    if (srcType < 0) throw new Xcept("Unknown file suffix: " + fname);
		
		KBModel = new ModelLite();
	    String srcText = UtilIO.readText(file);
	    int destType = ASModel.TEXT_XMML;
	    
	    // create server
	    NamedVal.NList soptions = new NamedVal.NList();
	    if (jsimBuildDir != null) soptions.setVal("buildDir", jsimBuildDir);
	    ASServer server = ASServer.create(soptions, null, null);	
	    String options = "sbml";
	
	    // translate
	    String xmlstring = null;
	    try {xmlstring = server.translateModelText(srcType, destType, srcText, options);} 
	    catch (Exception e) {
	    	KBModel.addError("XMML parsing error - could not compile " + file.getName());
	    	e.printStackTrace();
	    	return KBModel;
	    }
	    
		SAXBuilder builder = new SAXBuilder();
		doc = new Document();
		InputStream is = new ByteArrayInputStream(xmlstring.getBytes("UTF-8"));
		doc = builder.build(is);
		
		if(doc.hasRootElement()){
				KBModel = new ModelLite();
				KBModel.addError("XMML did not have a root element named 'model' - please use JSim version 2.05 or higher");
				}
		// Otherwise the model didn't compile
		else KBModel.addError("Conversion to SemSim failed because model did not compile");
		return KBModel;
	}
}