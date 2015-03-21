package vprExplorer.knowledgebaseinterface;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;

import semsimKB.SemSimKBConstants;
import semsimKB.SemSimKBConstants.kbcomponentstatus;
import semsimKB.model.CompBioModel;
import semsimKB.model.SemSimComponent;
import semsimKB.model.physical.DBCompositeEntity;
import semsimKB.model.physical.DBPhysicalComponent;
import semsimKB.model.physical.DBPhysicalProcess;
import semsimKB.model.physical.PhysicalProperty;
import semsimKB.model.physical.ReferencePhysicalEntity;
import semsimKB.webservices.VPRSPARQLWrite;
import vprExplorer.Settings;
import vprExplorer.buffer.KBBufferOperations;

public class RemoteKnowledgeBaseInterface extends KnowledgeBaseInterface {
	Settings globals;
	VPRSPARQLWrite sparql;
	
	public RemoteKnowledgeBaseInterface(Settings global) {
		globals = global;
		sparql = new VPRSPARQLWrite(globals);
	}

	public LinkedList<String> getAllClassMemberNames(URI seluri) {
		sparql.selectDistinct("a", seluri.toString());		
		return sparql.getResultLabels();
	}
	
	@Override
	public SemSimComponent getElementwithName(SemSimComponent ele) {
		return getElementwithName(ele.getName());
	}

	@Override
	public SemSimComponent getElementwithName(String name) {
		String result = sparql.selectObjectbyProperty("rdfs:label", name);
		if (result==null) return null;
		return getElementwithURI(URI.create(result));
	}

	@Override
	public SemSimComponent getElementwithURI(SemSimComponent ele) {
		return getElementwithURI(ele.getURI());
	}

	@Override
	public SemSimComponent getElementwithURI(URI eleuri) {
		boolean isind = sparql.isIndividual(eleuri);
		if (!isind) return null;
		SemSimComponent cmpt = null;
		
		sparql.describeGraph(eleuri);
		String type = sparql.getSingModelProperty("rdf:type",eleuri, false);
		
		String label = sparql.getSingModelProperty("rdfs:label", eleuri, true);
		
		if (type.matches(SemSimKBConstants.KB_PHYSICAL_ENTITY_CLASS_URI.toString())) {
			String complexity = sparql.getSingModelProperty("VPRKB:Entity_Complexity",eleuri, true);	
			cmpt = new DBCompositeEntity(eleuri, SemSimKBConstants.KNOWLEDGE_BASE_ENTITY_TYPES.get(complexity));
			makeType((DBCompositeEntity)cmpt);
		}
		else if (type.matches(SemSimKBConstants.KB_PHYSICAL_PROCESS_CLASS_URI.toString())) {
			makeType((DBPhysicalProcess)cmpt);
		}
		else if (type.matches(SemSimKBConstants.KB_COMPUTATIONAL_BIOMODEL_URI.toString())) {
			cmpt = new CompBioModel(label);
			makeType((CompBioModel)cmpt);
		}			
		else if (type.matches(SemSimKBConstants.REFERENCE_PHYSICAL_ENTITY_CLASS_URI.toString())) {
			String ref = sparql.getSingModelProperty("SemSim:refersTo", eleuri, false);
			cmpt = new ReferencePhysicalEntity(URI.create(ref), label);
			cmpt.setURI(eleuri);
		}
		else if (type.matches(SemSimKBConstants.PHYSICAL_PROPERTY_CLASS_URI.toString())) {
			cmpt = new PhysicalProperty(label, eleuri);
			makeType((PhysicalProperty)cmpt);
		}
		cmpt.setURI(eleuri);
		makeType(cmpt);
		cmpt.setName(label);

		return cmpt;
	}
	
	private void makeType(CompBioModel ssc) {
		
	}
	
	private void makeType(PhysicalProperty pp) {
		String ref = sparql.getSingModelProperty("SemSim:refersTo", pp.getURI(), false);
		pp.addReferenceOntologyAnnotation(SemSimKBConstants.REFERS_TO_RELATION, URI.create(ref), pp.getName());
	}
	
	private void makeType(DBPhysicalProcess dpp) {
		
	}
	
	private void makeType(DBCompositeEntity dbc) {
		String result = sparql.getSingModelProperty("ro:contained_in", dbc.getURI(), false);
		if (result==null) {
			result = sparql.getSingModelProperty("ro:part_of", dbc.getURI(), false);
			dbc.addRelation(SemSimKBConstants.PART_OF_RELATION);
			
		}
		else {
			dbc.addRelation(SemSimKBConstants.CONTAINED_IN_RELATION);
		}
		dbc.addComponent(URI.create(result));	
		result = sparql.getSingModelProperty("VPRKB:Has_Subcomponent", dbc.getURI(), false);
		dbc.addComponent(URI.create(result));	
		
		LinkedList<String> results = sparql.getMultModelProperty("model-qualifiers:isDerivedFrom", dbc.getURI());
		
		for (String rslt : results) {
			dbc.addCompBioModel(URI.create(rslt));
		}
		
		results = sparql.getComponentPropertyList(dbc.getURI());
		LinkedList<String> pmres = new LinkedList<String>();
		if (!results.isEmpty()) {
			for (String property : results) {
				pmres = sparql.getComponentPropModelMap(dbc.getURI(), URI.create(property));
				for (String mod : pmres) {
					dbc.addProperty(URI.create(property), URI.create(mod));
				}
			}
		}
	}
	
	private void makeType(SemSimComponent ssc) {}
	
	public int pushChangestoDatabase(KBBufferOperations kbbuffer) {
		//Check for and add models first
		for (SemSimComponent cbm : kbbuffer.getComponentSet(SemSimKBConstants.KB_COMPUTATIONAL_BIOMODEL_URI)) {
			switch(kbbuffer.getComponentStatus(cbm)) {
				case MISSING: 
					sparql.addIndividual(cbm, SemSimKBConstants.KB_COMPUTATIONAL_BIOMODEL_URI);
					break;
				default: 
					break;
			}	
		}
		URI muri = kbbuffer.getCurrentModelURI();
		//Add or modify Physical Components
		for (SemSimComponent ssc : kbbuffer.getComponentSet(SemSimKBConstants.PHYSICAL_MODEL_COMPONENT_CLASS_URI)) {
			kbcomponentstatus stat = kbbuffer.getComponentStatus(ssc);
			if (stat==kbcomponentstatus.EXACT_MATCH) continue;
			switch(stat) {
					case MISSING: 
						sparql.addIndividual(ssc, ssc.getClassURI());
						break;
					case NEW_INSTANCE_MODEL:
						sparql.addModeltoIndividual(ssc.getURI(), muri);
						break;
					default:
						break;
			}	
			if  (ssc.getClassURI().equals(SemSimKBConstants.KB_PHYSICAL_ENTITY_CLASS_URI) 
					|| ssc.getClassURI().equals(SemSimKBConstants.KB_PHYSICAL_PROCESS_CLASS_URI) ) {						
				DBPhysicalComponent dbpc = (DBPhysicalComponent)ssc;
				HashMap<URI, kbcomponentstatus> smap = kbbuffer.getPhysCompStatusList(dbpc);
				if (smap!=null) {
					for (URI key : smap.keySet()) {
						switch (kbbuffer.getPhysComptStatus(dbpc, key)) {
							case NEW_ASSOCIATED_PHYS_PROPERTY:
								sparql.addPropertyModelLinks(dbpc);
								break;
							case NEW_INSTANCE_PHYS_PROPERTY:
								sparql.addModeltoAxiom(dbpc.getURI(), key, kbbuffer.getCurrentModelURI());
								break;
							default:
								break;
						}
					}
				}
			}
		}
		
		return 0;
	}
}
