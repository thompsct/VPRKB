package vprExplorer.knowledgebaseinterface;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.commons.lang3.tuple.Pair;

import semsimKB.SemSimKBConstants;
import semsimKB.annotation.StructuralRelation;
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
		
		if (type.matches(SemSimKBConstants.PHYSICAL_PROPERTY_CLASS_URI.toString())) {	 
			makeProperty(eleuri, label);
		}
		else if (type.matches(SemSimKBConstants.KB_PROCESS_CLASS_URI.toString())) {
			makeProcess(eleuri, label);
		}
		else if (type.matches(SemSimKBConstants.KB_MODEL_URI.toString())) {
			makeModel(eleuri, label);
		}			
		else if (type.matches(SemSimKBConstants.REFERENCE_PHYSICAL_ENTITY_CLASS_URI.toString())) {
			makeReferenceEntity(eleuri, label);
		}

		return true;
	}
	
	private void makeModel(URI uri, String label) {
		if (buffer.hasModel(uri)) return;
		buffer.addModel(new CompBioModel(uri, label), ComponentStatus.EXACT_MATCH);
		
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
		predicates.add("VPRKB:Has_Subcomponent");
		if (rel == SemSimKBConstants.PART_OF_RELATION) {
			predicates.add("ro:part_of");
		}
		else {
			predicates.add("ro:contained_in");
		}
		
		objects.add(comps.getLeft().toString());
		objects.add(comps.getRight().toString()); 
		
		sparql.selectDistinctwithMultiCriteria(predicates, objects);
		ArrayList<String> results = sparql.getResultLabels();
		if (results.isEmpty()) return null;
		
		DBCompositeEntity dbc = makeComposite(URI.create(results.get(0)));
		
		ArrayList<ComponentStatus> pstats = new ArrayList<ComponentStatus>();
		ArrayList<ArrayList<ComponentStatus>> pmstats = new ArrayList<ArrayList<ComponentStatus>>();
		
		for (int i=0; i<dbc.getPropertyCount(); i++) {
			if (buffer.hasProperty(dbc.getPhysicalProperty(i))) {
				pstats.add(ComponentStatus.EXACT_MATCH);
			}
			else {
				
				pstats.add(ComponentStatus.EXTERNAL_TO_MODEL);
			}
			pmstats.add(new ArrayList<ComponentStatus>());
			for (int j=0; j<dbc.getPropertyModelCount(i); j++) {
				pmstats.get(i).add(ComponentStatus.EXTERNAL_TO_MODEL);
			}
		}
		KBCompositeObject<DBCompositeEntity> kbco = 
				new KBCompositeObject<DBCompositeEntity>(dbc, ComponentStatus.EXACT_MATCH, pstats, pmstats);
		buffer.addComposite(kbco);
		return dbc;
	}
	
	private DBCompositeEntity makeComposite(URI eleuri) {
		String label = sparql.getSingModelProperty("rdfs:label", eleuri, true);
		
		DBCompositeEntity dbc = new DBCompositeEntity(eleuri);
		dbc.setName(label);
		URI pe1uri = URI.create(sparql.getSingModelProperty("VPRKB:Has_Subcomponent", dbc.getURI(), false));
		
		URI pe2uri = URI.create(sparql.getSingModelProperty("ro:part_of", dbc.getURI(), false));
		if (pe2uri==null) {
			pe2uri = URI.create(sparql.getSingModelProperty("ro:contained_in", dbc.getURI(), false));
			dbc.setRelation(SemSimKBConstants.PART_OF_RELATION);
		}
		else {
			dbc.setRelation(SemSimKBConstants.CONTAINED_IN_RELATION);
		}

		PhysicalEntity pe1 = buffer.getPhysicalEntitybyURI(pe1uri);
		PhysicalEntity pe2 = buffer.getPhysicalEntitybyURI(pe1uri);
		dbc.setComponents(Pair.of(pe1, pe2));
		
		LinkedList<String> results = sparql.getMultModelProperty("model-qualifiers:isDerivedFrom", dbc.getURI());
		
		for (String rslt : results) {
			URI muri = URI.create(rslt);
			getElementwithURI(muri, false);
			dbc.addCompBioModel(buffer.getModelbyURI(muri));
		}
		
		results = sparql.getComponentPropertyList(dbc.getURI());
		LinkedList<String> pmres = new LinkedList<String>();
		if (!results.isEmpty()) {
			for (String property : results) {
				URI ppuri = URI.create(property);
				pmres = sparql.getComponentPropModelMap(dbc.getURI(), URI.create(property));
				getElementwithURI(ppuri, false);
				PhysicalProperty pp = buffer.getPropertybyURI(ppuri);
				for (String mod : pmres) {
					CompBioModel model = buffer.getModelbyURI(URI.create(mod));
					dbc.addProperty(pp, model);
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
			else if (kbdbc.getPropertyStatus(i) == ComponentStatus.NEW_ASSOCIATED_PHYS_PROPERTY) {
					sparql.addModeltoAxiom(dbcuri, dbc.getPhysicalProperty(i).getURI(), muri);
			}
		}
	}
}
