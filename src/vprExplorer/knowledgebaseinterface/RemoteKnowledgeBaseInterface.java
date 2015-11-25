package vprExplorer.knowledgebaseinterface;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.commons.lang3.tuple.Pair;

import semsimKB.annotation.CurationalMetadata.Metadata;
import semsimKB.definitions.SemSimTypes;
import semsimKB.definitions.StructuralRelation;
import semsimKB.model.CompBioModel;
import semsimKB.model.SemSimComponent;
import semsimKB.model.kbbuffer.KBCompositeObject;
import semsimKB.model.kbbuffer.KnowledgeBase;
import semsimKB.model.physical.DBCompositeEntity;
import semsimKB.model.physical.PhysicalEntity;
import semsimKB.model.physical.PhysicalProperty;
import semsimKB.model.physical.ReferencePhysicalEntity;
import semsimKB.webservices.VPRSPARQLWrite;
import vprExplorer.Settings;
import vprExplorer.buffer.ComponentStatus;

public class RemoteKnowledgeBaseInterface extends KnowledgeBaseInterface {
	Settings globals;
	VPRSPARQLWrite sparql;
	ArrayList<SemSimComponent> searchresults;
	
	public RemoteKnowledgeBaseInterface(Settings global, KnowledgeBase buff) {
		super(buff);
		globals = global;
		sparql = new VPRSPARQLWrite(globals);
	}

	public ArrayList<String> getAllClassMemberNames(URI seluri) {
		sparql.selectDistinct("a", seluri.toString());		
		return sparql.getResultLabels();
	}
	
	@Override
	public boolean getElementwithName(SemSimComponent ele, boolean verifyonly) {
		return getElementwithName(ele.getName(), verifyonly);
	}

	@Override
	public boolean getElementwithName(String name, boolean verifyonly) {
		String result = sparql.selectObjectbyProperty("rdfs:label", name);
		if (result==null) return false;
		return getElementwithURI(URI.create(result), verifyonly);
	}

	@Override
	public boolean getElementwithURI(SemSimComponent ele, boolean verifyonly) {
		return getElementwithURI(ele.getURI(), verifyonly);
	}

	@Override
	public boolean getElementwithURI(URI eleuri, boolean verifyonly) {
		if (!sparql.isIndividual(eleuri)) return false;
		if (verifyonly) return true;
		sparql.describeGraph(eleuri);
		String type = sparql.getSingModelProperty("rdf:type",eleuri, false);
		
		String label = sparql.getSingModelProperty("rdfs:label", eleuri, true);
		
		if (type.matches(SemSimTypes.PHYSICAL_PROPERTY.getURIasString())) {	 
			makeProperty(eleuri, label);
		}
		else if (type.matches(SemSimTypes.KB_PHYSICAL_PROCESS.getURIasString())) {
			makeProcess(eleuri, label);
		}
		else if (type.matches(SemSimTypes.KB_MODEL.getURIasString())) {
			makeModel(eleuri, label);
		}			
		else if (type.matches(SemSimTypes.REFERENCE_PHYSICAL_ENTITY.getURIasString())) {
			makeReferenceEntity(eleuri, label);
		}

		return true;
	}
	
	private void makeModel(URI uri, String label) {
		if (buffer.hasModel(uri)) return;
		CompBioModel cbm = new CompBioModel(uri, label);
		for (Metadata m : Metadata.values()) {
			String value = sparql.getSingModelProperty(m.getSparqlCode(), uri, true);
			if (value!=null) {
				cbm.getCurationalMetadata().setAnnotationValue(m, value);
			}
		}
		buffer.addModel(cbm, ComponentStatus.EXACT_MATCH);
		
	}
	
	private void makeProperty(URI eleuri, String label) {		
		if (buffer.hasProperty(eleuri)) return;
		buffer.addPhysicalProperty(new PhysicalProperty(label, eleuri), ComponentStatus.EXACT_MATCH);
	}
	
	private void makeProcess(URI uri, String label) {
		
	}
	
	private void makeReferenceEntity(URI eleuri, String label) {
		buffer.addReferencePhysicalEntity(new ReferencePhysicalEntity(eleuri, label), ComponentStatus.EXACT_MATCH);
	}
	
	public DBCompositeEntity retrieveComposite(Pair<URI, URI> comps, StructuralRelation rel) {
		ArrayList<String> predicates = new ArrayList<String>();
		ArrayList<String> objects = new ArrayList<String>();
		predicates.add(StructuralRelation.INDEX_ENTITY_RELATION.getSparqlCode());
		if (rel!= null) {
			predicates.add(rel.getSparqlCode());
		}

		objects.add(comps.getLeft().toString());
		if (comps.getRight()!=null) {
			objects.add(comps.getRight().toString());
		}
		
		LinkedList<String> results = sparql.selectDistinctwithMultiCriteria(predicates, objects);
		if (results.isEmpty()) return null;
		URI rslturi = URI.create(results.get(0));
		if (buffer.hasKBComposite(rslturi)) return buffer.getCompositeEntitybyURI(rslturi); 

		DBCompositeEntity dbc = makeComposite(rslturi);
		
		ArrayList<ComponentStatus> pstats = new ArrayList<ComponentStatus>();
		
		for (int i=0; i<dbc.getPropertyCount(); i++) {
			if (buffer.hasProperty(dbc.getPhysicalProperty(i))) {
				pstats.add(ComponentStatus.EXACT_MATCH);
			}
			else {
				
				pstats.add(ComponentStatus.EXTERNAL_TO_MODEL);
			}
		}
		KBCompositeObject<DBCompositeEntity> kbco = 
				new KBCompositeObject<DBCompositeEntity>(dbc, ComponentStatus.EXACT_MATCH, pstats);
		buffer.addComposite(kbco);
		return dbc;
	}
	
	private DBCompositeEntity makeComposite(URI eleuri) {
		sparql.describeGraph(eleuri);
		String label = sparql.getSingModelProperty("rdfs:label", eleuri, true);
		
		DBCompositeEntity dbc = new DBCompositeEntity(eleuri , label);
		URI pe1uri = URI.create(sparql.getSingModelProperty(StructuralRelation.INDEX_ENTITY_RELATION.getSparqlCode(), eleuri, false));
		
		String pe2uri = sparql.getSingModelProperty(StructuralRelation.PART_OF_RELATION.getSparqlCode(), eleuri, false);
		if (pe2uri==null) {
			pe2uri = sparql.getSingModelProperty(StructuralRelation.CONTAINED_IN_RELATION.getSparqlCode(), eleuri, false);
			dbc.setRelation(StructuralRelation.PART_OF_RELATION);
		}
		else {
			dbc.setRelation(StructuralRelation.CONTAINED_IN_RELATION);
		}

		PhysicalEntity pe1 = buffer.getPhysicalEntitybyURI(pe1uri);
		PhysicalEntity pe2 = null;
		if (pe2uri!=null) {
			 pe2 = buffer.getPhysicalEntitybyURI(URI.create(pe2uri));
		}
		dbc.setComponents(Pair.of(pe1, pe2));
		
		LinkedList<String> results = sparql.getMultModelProperty("bqm:isDerivedFrom", dbc.getURI());
		
		for (String rslt : results) {
			URI muri = URI.create(rslt);
			getElementwithURI(muri, false);
			dbc.addCompBioModel(buffer.getModelbyURI(muri));
		}
		
		results = sparql.getComponentPropertyList(dbc.getURI());
		
		if (!results.isEmpty()) {
			LinkedList<String> pmres = new LinkedList<String>();
			for (String property : results) {
				URI ppuri = URI.create(property);
				pmres = sparql.getComponentPropModelMap(dbc.getURI(), URI.create(property));
				getElementwithURI(ppuri, false);
				PhysicalProperty pp = buffer.getPropertybyURI(ppuri);
				for (String mod : pmres) {
					getElementwithURI(URI.create(mod), false);
					CompBioModel model = buffer.getModelbyURI(URI.create(mod));
					int index = dbc.addProperty(pp, model);
					dbc.addModeltoProperty(index, model);
				}
			}
		}
		return dbc;
	}
	
	public int pushChangestoDatabase(URI muri, ArrayList<KBCompositeObject<DBCompositeEntity>> composites) {
		//Check for and add models first
		for (CompBioModel cbm : buffer.getModels()) {
			if (buffer.getModelStatusbyURI(cbm.getURI())==ComponentStatus.MISSING) {
				sparql.addIndividual(cbm);
			}	
		}
		for (PhysicalProperty pp : buffer.getProperties()) {
			if (buffer.getPropertyStatusbyURI(pp.getURI())==ComponentStatus.MISSING) {
				sparql.addIndividual(pp);
			}	
		}
		
		for (ReferencePhysicalEntity rpe : buffer.getReferenceEntities()) {
			if (buffer.getRefEntityStatusbyURI(rpe.getURI())==ComponentStatus.MISSING) {
				sparql.addIndividual(rpe);
			}	
		}
		//Add or modify Composites
		for (KBCompositeObject<DBCompositeEntity> kdbc : composites) {				
					switch (kdbc.getStatus()) {
					case EXACT_MATCH:
						continue;
					case EXTERNAL_TO_MODEL:
						continue;
					case MISSING:
						sparql.addIndividual(kdbc.getComponent());
						break;

					default:
						parsePropertyAssociations(muri, kdbc.getURI());
					}
			}
		
		return 0;
	}
	
	private void parsePropertyAssociations(URI muri, URI dbcuri) {
		KBCompositeObject<DBCompositeEntity> kbdbc = buffer.getKBCompositeObject(dbcuri);
		DBCompositeEntity dbc = kbdbc.getComponent();
		for (int i=0; i<kbdbc.getPropertyCount(); i++) {
			if (kbdbc.getPropertyStatus(i) == ComponentStatus.NEW_ASSOCIATED_PHYS_PROPERTY) {
				sparql.addPropertyModelLinks(dbc.getURI(), dbc.getPhysicalProperty(i).getURI(), muri);
			}
			else if (kbdbc.getPropertyStatus(i) == ComponentStatus.NEW_PROPERTY_MODEL_ASSOCIATION) {
					sparql.addModeltoAxiom(dbcuri, dbc.getPhysicalProperty(i).getURI(), muri);
			}
		}
	}
	
	public ArrayList<SemSimComponent> searchNames(String srchstring) {
		ArrayList<String> results = sparql.selectIndividualwithString(srchstring);

		searchresults = new ArrayList<SemSimComponent>();

		for (String rslt : results) {
			getElementwithURI(URI.create(rslt), false);
		}
		
		return searchresults;
	}
	
	public ArrayList<SemSimComponent> searchNames(String srchstring, URI type) {
		ArrayList<String> results = sparql.selectIndividualofTypewithString(srchstring, type.toString());

		searchresults = new ArrayList<SemSimComponent>();

		for (String rslt : results) {
			getElementwithURI(URI.create(rslt), false);
		}
		
		return searchresults;
	}
}
