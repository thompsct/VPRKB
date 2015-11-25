package semsimKB;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import semsimKB.annotation.ReferenceOntologyAnnotation;
import semsimKB.definitions.RDFNamespace;
import semsimKB.owl.SemSimOWLFactory;

//Class for holding reference terms and data required for SemGen - intended to replace SemSimConstants class
public class SemSimLibrary {
	public static final double SEMSIM_VERSION = 0.2;
	public static final IRI SEMSIM_VERSION_IRI = IRI.create(RDFNamespace.SEMSIM_NAMESPACE + "SemSimVersion");
	private OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	public OWLOntology OPB;
	
	private Hashtable<String, String[]> OPBClassesForUnitsTable = new Hashtable<String, String[]>();

	// Hashtable that contains a hashtable of base units and exponents, and OPB classes
	// Is creating a hashtable of hashtables frown upon?	
	private static Set<String> OPBproperties = new HashSet<String>();
	private static Set<String> OPBflowProperties = new HashSet<String>();
	private static Set<String> OPBprocessProperties = new HashSet<String>();
	//private static Set<String> OPBdynamicalProperties = new HashSet<String>();
	private static Set<String> OPBamountProperties = new HashSet<String>();
	private static Set<String> OPBforceProperties = new HashSet<String>();
	private static Set<String> OPBstateProperties = new HashSet<String>();
	
	public SemSimLibrary() {
		loadLibrary();
	}
	
	private void loadLibrary() {
		try {
			OPBClassesForUnitsTable = ResourcesManager.createHashtableFromFile("cfg/OPBClassesForUnits.txt");
		} catch (FileNotFoundException e3) {e3.printStackTrace();}	
		

		// Load the local copy of the OPB and the SemSim base ontology, and other config files into memory
		try {
			manager.loadOntologyFromOntologyDocument(new File("cfg/SemSimBase.owl"));
		} catch (OWLOntologyCreationException e3) {
			e3.printStackTrace();
		}

		try {
			OPBproperties = SemSimOWLFactory.getAllSubclasses(OPB, RDFNamespace.OPB_NAMESPACE.getNamespace() + "OPB_00147", false);
			OPBflowProperties = SemSimOWLFactory.getAllSubclasses(OPB, RDFNamespace.OPB_NAMESPACE.getNamespace() + "OPB_00573", false);
			OPBprocessProperties = SemSimOWLFactory.getAllSubclasses(OPB, RDFNamespace.OPB_NAMESPACE.getNamespace() + "OPB01151", false);
			//OPBdynamicalProperties = SemSimOWLFactory.getAllSubclasses(OPB, RDFNamespace.OPB_NAMESPACE + "OPB_00568", false);
			OPBamountProperties = SemSimOWLFactory.getAllSubclasses(OPB, RDFNamespace.OPB_NAMESPACE.getNamespace() + "OPB_00135", false);
			OPBforceProperties = SemSimOWLFactory.getAllSubclasses(OPB, RDFNamespace.OPB_NAMESPACE.getNamespace() + "OPB_00574", false);
			OPBstateProperties = SemSimOWLFactory.getAllSubclasses(OPB, RDFNamespace.OPB_NAMESPACE.getNamespace() + "OPB_00569", false);
		} catch (OWLException e2) {e2.printStackTrace();}
	}
	
	public String[] getOPBUnitRefTerm(String unit) {
		return OPBClassesForUnitsTable.get(unit);
	}

	public OWLOntology getLoadedOntology(String onturi) throws OWLOntologyCreationException {
		OWLOntology localont = SemSimOWLFactory.getOntologyIfPreviouslyLoaded(IRI.create(onturi), manager);
		if (localont == null) {
			localont = manager.loadOntologyFromOntologyDocument(IRI.create(onturi));
		}
		return localont;
	}
	
	public Set<String> getOPBsubclasses(String parentclass) throws OWLException {
		Set<String> subclassset = SemSimOWLFactory.getAllSubclasses(OPB, RDFNamespace.OPB_NAMESPACE.getNamespace() + parentclass, false);
		return subclassset;
	}
	
	public OWLDataFactory makeOWLFactory() {
		return manager.getOWLDataFactory() ;
	}
	
	public boolean OPBhasProperty(String s) {
		return OPBproperties.contains(s);
	}
	
	public boolean OPBhasFlowProperty(ReferenceOntologyAnnotation roa) {
		return OPBflowProperties.contains(roa.getReferenceURI().toString());
	}
	
	public boolean OPBhasFlowProperty(String s) {
		return OPBflowProperties.contains(s);
	}
	
	public boolean OPBhasFlowProperty(URI u) {
		return OPBflowProperties.contains(u.toString());
	}
	
	public boolean OPBhasForceProperty(ReferenceOntologyAnnotation roa) {
		return OPBforceProperties.contains(roa.getReferenceURI().toString());
	}
	
	public boolean OPBhasAmountProperty(ReferenceOntologyAnnotation roa) {
		return OPBamountProperties.contains(roa.getReferenceURI().toString());
	}
	
	public boolean OPBhasStateProperty(ReferenceOntologyAnnotation roa) {
		return OPBstateProperties.contains(roa.getReferenceURI().toString());
	}
	
	public boolean OPBhasProcessProperty(ReferenceOntologyAnnotation roa) {
		return OPBprocessProperties.contains(roa.getReferenceURI().toString());
	}
	
	public boolean OPBhasProcessProperty(String s) {
		return OPBprocessProperties.contains(s);
	}
	
	public boolean OPBhasProcessProperty(URI u) {
		return OPBprocessProperties.contains(u.toString());
	}

}
