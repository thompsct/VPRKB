package semsimKB.annotation;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLLiteral;
import semsimKB.SemSimKBConstants;


public class CurationalMetadata {
	public static final String DCTERMS_NAMESPACE = "http://purl.org/dc/terms/";
	public static final String DCTERMS_NAMESPACE_11 = "http://purl.org/dc/elements/1.1/";
	private static String SEMSIM_NAMESPACE = SemSimKBConstants.SEMSIM_NAMESPACE;
	
	public static final URI MODEL_NAME_URI = URI.create(SEMSIM_NAMESPACE + "modelName");
	public static final URI MODEL_DESCRIPTION_URI = URI.create(SEMSIM_NAMESPACE + "ModelDescription");
	public static final URI MODEL_ID_URI = URI.create(SEMSIM_NAMESPACE + "modelId");
	public static final URI ANNOTATOR_NAME_URI = URI.create(SEMSIM_NAMESPACE + "AnnotatorName");
	public static final URI ANNOTATOR_CONTACT_INFO_URI = URI.create(SEMSIM_NAMESPACE + "AnnotatorContactInfo");
	public static final URI MODELER_NAME_URI = URI.create(SEMSIM_NAMESPACE + "ModelerName");
	public static final URI MODELER_CONTACT_INFO_URI = URI.create(SEMSIM_NAMESPACE + "ModelerInfo");

	public static final URI REFERENCE_PUBLICATION_PUBMED_ID_URI = URI.create(SEMSIM_NAMESPACE + "PubMedIDofReferencePublication");
	public static final URI REFERENCE_PUBLICATION_ABSTRACT_TEXT_URI = URI.create(SEMSIM_NAMESPACE + "ReferencePublicationAbstractText");
	public static final URI REFERENCE_PUBLICATION_CITATION_URI = URI.create(SEMSIM_NAMESPACE + "ReferencePublicationCitation");
	
	public static final URI MATLAB_URL_URI = URI.create(SEMSIM_NAMESPACE + "MatLabURL");
	public static final URI CELLML_URL_URI = URI.create(SEMSIM_NAMESPACE + "cellmlURL");
	public static final URI SBML_URL_URI = URI.create(SEMSIM_NAMESPACE + "sbmlURL");
	public static final URI JSIM_URL_URI = URI.create(SEMSIM_NAMESPACE + "jsimURL");
	
	// Model-level relations
	public static final SemSimRelation MODEL_ID_RELATION = new SemSimRelation("the ID of the model from which the SemSim model was generated", MODEL_ID_URI);
	public static final SemSimRelation MODEL_NAME_RELATION = new SemSimRelation("a human-readable name for the model", MODEL_NAME_URI);
	public static final SemSimRelation MODEL_DESCRIPTION_RELATION = new SemSimRelation("a free-text description of the model", MODEL_DESCRIPTION_URI);	
	public static final SemSimRelation ANNOTATOR_NAME_RELATION = new SemSimRelation("who to contact about the annotations in the model", ANNOTATOR_NAME_URI);	
	public static final SemSimRelation ANNOTATOR_CONTACT_RELATION = new SemSimRelation("email address of annotator", ANNOTATOR_CONTACT_INFO_URI);
	public static final SemSimRelation MODELER_NAME_RELATION = new SemSimRelation("who to contact about the model", MODELER_NAME_URI);	
	public static final SemSimRelation MODELER_CONTACT_RELATION = new SemSimRelation("email address of modeler", MODELER_CONTACT_INFO_URI);	
	
	public static final SemSimRelation REFERENCE_PUBLICATION_PUBMED_ID_RELATION = new SemSimRelation("the PubMed ID of the model's reference publication", REFERENCE_PUBLICATION_PUBMED_ID_URI);
	public static final SemSimRelation REFERENCE_PUBLICATION_ABSTRACT_TEXT_RELATION = new SemSimRelation("the abstract text of the model's reference publication", REFERENCE_PUBLICATION_ABSTRACT_TEXT_URI);
	public static final SemSimRelation REFERENCE_PUBLICATION_CITATION_RELATION = new SemSimRelation("the citation for the reference publication", REFERENCE_PUBLICATION_CITATION_URI);
	
	public static final SemSimRelation MATLAB_URL_RELATION = new SemSimRelation("the URL for the Matlab version of the model", MATLAB_URL_URI);
	public static final SemSimRelation CELLML_URL_RELATION = new SemSimRelation("the URL for the CellML version of the model", CELLML_URL_URI);
	public static final SemSimRelation SBML_URL_RELATION = new SemSimRelation("the URL for the SBML version of the model", SBML_URL_URI);
	public static final SemSimRelation JSIM_URL_RELATION = new SemSimRelation("the URL for the JSim version of the model", JSIM_URL_URI);
	
	private LinkedHashMap<Metadata, String> curationmap = new LinkedHashMap<Metadata, String>();
	
	public CurationalMetadata() {
		for (Metadata m : Metadata.values()) {
			curationmap.put(m, "");
		}
	}
	
	public void importMetadata(CurationalMetadata toimport, boolean overwrite) {
		for (Metadata m : Metadata.values()) {
			if (curationmap.get(m)=="" || overwrite) {
				curationmap.put(m, toimport.getAnnotationValue(m));
			}
		}
	}
	
	public enum Metadata {
		fullname("Full Name", MODEL_NAME_RELATION, "dc:title"),
		description("Description", MODEL_DESCRIPTION_RELATION, "dc:description"),
		annotatorauthor("Annotator Name", ANNOTATOR_NAME_RELATION, "SemSim:annotator"),
		annotatorcontact("Annotator Contact", ANNOTATOR_CONTACT_RELATION, "SemSim:annotatoremail"),
		modelauthor("Model Author", MODELER_NAME_RELATION, "dc:creator"),
		modelcontact("Model Contact", MODELER_CONTACT_RELATION, "SemSim:creatoremail"),
		sourcemodelid("Source Model ID", MODEL_ID_RELATION, "rdf:id"),
		cellmlurl("CellML URL",CELLML_URL_RELATION, "SemSim:cellmlloc"),
		matlaburl("Matlab URL",MATLAB_URL_RELATION, "SemSim:matlabloc"),
		mmlurl("JSim URL",JSIM_URL_RELATION, "SemSim:jsimloc"),
		sbmlurl("SBML URL",SBML_URL_RELATION, "SemSim:sbmlloc");
		
		private final String text;
		private final SemSimRelation relation;
		private final String sparqlcode;
		private Metadata(final String text, SemSimRelation rel, String spqlcode) {
		   this.text = text;
		   relation = rel;
		   sparqlcode = spqlcode;
		}
	
		@Override
		public String toString() {
		   return text;
		}
			 
		 protected SemSimRelation getRelation() {
			 return relation;
		 }
		 
		 public URI getURI() {
			 return relation.getURI();
		 }
		 
		 public String getSparqlCode() {
			 return sparqlcode;
		 }
	}
	
	public String getAnnotationName(Metadata item) {
		return item.toString();
	}
	
	public String getAnnotationValue(Metadata item) {
		return curationmap.get(item);
	}
	
	public void setAnnotationValue(Metadata item, String value) {
		curationmap.put(item, value);
	}
	
	public boolean hasAnnotationValue(Metadata item) {
		return !curationmap.get(item).isEmpty();
	}
	
	 public Annotation getAsAnnotation(Metadata item) {
		 return new Annotation(item.getRelation(), curationmap.get(item));
	 }
	
	public ArrayList<Annotation> getAnnotationList() {
		ArrayList<Annotation> list = new  ArrayList<Annotation>();
		for (Metadata m : Metadata.values()) {
			if (hasAnnotationValue(m)) {
				list.add(getAsAnnotation(m));
			}
		}
		return list;
	}
	
	public boolean isItemValueEqualto(Metadata item, String value) {
		return curationmap.get(item).equals(value);
	}

	public void setCurationalMetadata(Set<OWLAnnotation> list, Set<OWLAnnotation> removelist) {
		for (Metadata m : Metadata.values()) {
			for (OWLAnnotation a : list) {
				if (m.getURI().equals(a.getProperty().getIRI().toURI())) {
					setAnnotationValue(m, ((OWLLiteral)a.getValue()).getLiteral());
					removelist.add(a);
				}
			}
		}
	}
}