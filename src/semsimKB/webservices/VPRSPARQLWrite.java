package semsimKB.webservices;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import semsimKB.SemSimKBConstants;
import semsimKB.model.CompBioModel;
import semsimKB.model.SemSimComponent;
import semsimKB.model.physical.DBCompositeEntity;
import semsimKB.model.physical.DBPhysicalComponent;
import semsimKB.model.physical.DBPhysicalProcess;
import semsimKB.model.physical.PhysicalProperty;
import semsimKB.model.physical.ReferencePhysicalEntity;
import vprExplorer.Globals;

import com.hp.hpl.jena.sparql.modify.UpdateProcessRemote;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

public class VPRSPARQLWrite extends vprSPARQL {
	//Insert DATA  { (individual) a (type) .\n (data list) }
	public final String sinsertind = "INSERT  {<%i> a owl:NamedIndividual .\n  %d} "
			+ "WHERE { FILTER NOT EXISTS { <%i> a owl:NamedIndividual }}";
	//Add model to individual
	public final String inmodel = "INSERT  {<%i> model-qualifiers:isDerivedFrom <%m>} "
			+ "WHERE { FILTER NOT EXISTS { <%i> model-qualifiers:isDerivedFrom <%m> }}";
	//Insert axiom for property of individual
	public final String sinsertax = "INSERT  { VPRKB:Ax%n a owl:Axiom. "
			+ "VPRKB:Ax%n owl:annotatedSource <%s>. VPRKB:Ax%n owl:annotatedTarget <%t>}" 
			+ "WHERE { FILTER NOT EXISTS { ?a owl:annotatedSource <%s>. ?a owl:annotatedTarget <%t> }}";
	//Update model/property mappings
	public final String addppmod = "INSERT  {?Axiom model-qualifiers:isDescribedBy <%m>} "
			+ "WHERE {?Axiom owl:annotatedSource <%s>. ?Axiom owl:annotatedTarget <%t>."
			+ "FILTER NOT EXISTS {?Axiom model-qualifiers:isDescribedBy <%m>}}";
	
	
	public VPRSPARQLWrite(Globals global) {
		super(global);
	}
	//Add a named individual with properties	
	public int addIndividual(SemSimComponent ind, URI type) {
		String iuri = ind.getURI().toString();
		
		String typeid = SPARQLConstants.classmap.get(type);
		String ui = sinsertind;
		ui = ui.replace("%i", iuri);
		String data = "<" + iuri + "> a " + typeid + " .\n";
		data = data + "<" + iuri + "> rdfs:label '" + ind.getName() + "' .\n";
		
		if (type==SemSimKBConstants.KB_PHYSICAL_ENTITY_CLASS_URI) {
			data = buildEntry((DBCompositeEntity)ind, data);
		}
		else if (type==SemSimKBConstants.KB_PHYSICAL_PROCESS_CLASS_URI) {
			buildEntry((DBPhysicalProcess)ind, data);
		}
		else if (type==SemSimKBConstants.KB_COMPUTATIONAL_BIOMODEL_URI) {
			buildEntry((CompBioModel)ind, data);
		}
		else if (type==SemSimKBConstants.REFERENCE_PHYSICAL_ENTITY_CLASS_URI) {
			data = buildEntry((ReferencePhysicalEntity)ind, data);
		}
		else if (type==SemSimKBConstants.PHYSICAL_PROPERTY_CLASS_URI) {
			data = buildEntry((PhysicalProperty)ind, data);
		}
		ui = ui.replace("%d", data);
		
		updateRemote(ui);
		
		if ((type==SemSimKBConstants.KB_PHYSICAL_ENTITY_CLASS_URI) || (type==SemSimKBConstants.KB_PHYSICAL_PROCESS_CLASS_URI)) {
			if (!((DBPhysicalComponent)ind).getPropertyList().isEmpty()) {
				addPropertyModelLinks((DBPhysicalComponent)ind);
			}
		}
		return 0;
	}
	
	//Reference Physical Entity
	String buildEntry(ReferencePhysicalEntity obj, String data) {
		String ouri = obj.getURI().toString() ;
		data = data + "<" + ouri + "> SemSim:refersTo <" + obj.getFirstRefersToReferenceOntologyAnnotation().getReferenceURI() + "> .\n";
		return data;
	}
	
	String buildEntry(PhysicalProperty obj, String data) {
		String ouri = obj.getURI().toString() ;
		data = data + "<" + ouri + "> SemSim:refersTo <" + obj.getFirstRefersToReferenceOntologyAnnotation().getReferenceURI() + "> .\n";
		return data;
	}
	String buildEntry(DBCompositeEntity obj, String data) {
		String ouri = obj.getURI().toString() ;
		data = data + "<" + ouri + "> VPRKB:Entity_Complexity '" + obj.getType().toString() + "' .\n";
		for (PhysicalProperty pp : obj.getPhysicalProperties()) {
			data = data + "<" + ouri + "> SemSim:hasPhysicalProperty <" + pp.getURI().toString() + "> .\n";
		}
		for (URI cbm : obj.getBioCompModels()) {
			data = data + "<" + ouri + "> model-qualifiers:isDerivedFrom <" + cbm + "> .\n";
		}
		data = data + "<" + ouri + "> <" + obj.getRelation(0).getURI().toString() + "> <" + obj.getComponents().get(0).toString() + "> .\n";
		data = data + "<" + ouri + "> VPRKB:Has_Subcomponent <" +  obj.getComponents().get(1).toString() + "> .\n";
		return data;
	}
	
	void buildEntry(DBPhysicalProcess obj, String data) {
		
	}
	void buildEntry(CompBioModel obj, String data) {
		//String ouri = obj.getURI().toString() ; 
	}
	
	//Annotate individual with property map
	public void addModeltoIndividual(URI dpc, URI muri) {
		String ui = makePrefixString("owl") + inmodel;
		
		ui = ui.replace("%i", dpc.toString());
		ui = ui.replace("%m", muri.toString());
		
		updateRemote(ui);
	}
	
	//Annotate individual with property map
	void addPropertyAxiom(URI dpc, URI puri) {
		int n = countObj("owl:Axiom")+1;
		
		String ui = makePrefixString("owl") + sinsertax;
		
		ui = ui.replace("%n", String.valueOf(n));
		ui = ui.replace("%s", dpc.toString());
		ui = ui.replace("%t", puri.toString());
		
		updateRemote(ui);
	}
	
	//Annotate individual with property map
	public void addModeltoAxiom(URI dpc, URI puri, URI muri) {
		String ui = makePrefixString("owl") + addppmod;
		ui = ui.replace("%s", dpc.toString());
		ui = ui.replace("%t", puri.toString());
		ui = ui.replace("%m", muri.toString());
		
		updateRemote(ui);
	}
	
	public void addPropertyModelLinks(DBPhysicalComponent dpc) {
		URI dpcuri = dpc.getURI();
		Map<URI, Set<URI>> pmmap = dpc.getPropertyMap();
		URI muri = dpc.getBioCompModels().iterator().next();
				
		for (URI ppuri : pmmap.keySet()) {
			addPropertyAxiom(dpcuri, ppuri);
			addModeltoAxiom(dpcuri, ppuri, muri);			
		}
	}
	//Make and execute update request
	void updateRemote(String ui) {
		host = server + "update";
		UpdateRequest request = UpdateFactory.create();
		request.setPrefixMapping(pmap);
		request.add(ui);
		UpdateProcessRemote upr = new UpdateProcessRemote(request, host, new Context(), auth);
		upr.execute();
	}
	
	//Removes all named individuals and associated data
	public void purgeKB() {
		String s = "DELETE { ?s ?p ?o } WHERE { ?s ?p ?o }";
		
		updateRemote(s);
	}
	
}
