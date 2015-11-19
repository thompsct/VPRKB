package semsimKB.webservices;

import java.net.URI;

import org.apache.commons.lang3.tuple.Pair;

import semsimKB.annotation.CurationalMetadata;
import semsimKB.annotation.CurationalMetadata.Metadata;
import semsimKB.annotation.StructuralRelation;
import semsimKB.model.CompBioModel;
import semsimKB.model.SemSimObject;
import semsimKB.model.SemSimTypes;
import semsimKB.model.physical.DBCompositeEntity;
import semsimKB.model.physical.DBPhysicalComponent;
import semsimKB.model.physical.DBPhysicalProcess;
import vprExplorer.Settings;

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
	public final String sinsertax = "INSERT  { physkb:Ax%n a owl:Axiom. "
			+ "physkb:Ax%n owl:annotatedSource <%s>. physkb:Ax%n owl:annotatedTarget <%t>}" 
			+ "WHERE { FILTER NOT EXISTS { ?a owl:annotatedSource <%s>. ?a owl:annotatedTarget <%t> }}";
	//Update model/property mappings
	public final String addppmod = "INSERT  {?Axiom model-qualifiers:isDescribedBy <%m>} "
			+ "WHERE {?Axiom owl:annotatedSource <%s>. ?Axiom owl:annotatedTarget <%t>."
			+ "FILTER NOT EXISTS {?Axiom model-qualifiers:isDescribedBy <%m>}}";
	
	
	public VPRSPARQLWrite(Settings global) {
		super(global);
	}
	//Add a named individual with properties	
	public int addIndividual(SemSimObject ind) {
		SemSimTypes type = ind.getType();
		String iuri = ind.getURI().toString();
		
		String ui = sinsertind;
		ui = ui.replace("%i", iuri);
		String data = "<" + iuri + "> a " + type.getSparqlCode() + " .\n";
		data = data + "<" + iuri + "> rdfs:label '" + ind.getName() + "' .\n";
		
		if (type==SemSimTypes.KB_COMPOSITE_ENTITY) {
			data = buildEntry((DBCompositeEntity)ind, data);
		}
		else if (type==SemSimTypes.KB_PHYSICAL_PROCESS) {
			buildEntry((DBPhysicalProcess)ind, data);
		}
		else if (type==SemSimTypes.KB_MODEL) {
			data = buildEntry((CompBioModel)ind, data);
		}
		
		ui = ui.replace("%d", data);
		
		updateRemote(ui);
		
		if ((type==SemSimTypes.KB_COMPOSITE_ENTITY) || (type==SemSimTypes.KB_PHYSICAL_PROCESS)) {
			DBPhysicalComponent dbc = (DBPhysicalComponent)ind;
			if (!dbc.getPropertyList().isEmpty()) {
				for (int i=0; i<dbc.getPropertyCount(); i++) {
					
					addPropertyModelLinks(dbc.getURI(), dbc.getPhysicalProperty(i).getURI(), dbc.getPropertyModelList(i).get(0).getURI());
				}
			}
		}
		return 0;
	}
	
	String buildEntry(DBCompositeEntity obj, String data) {
		String ouri = obj.getURI().toString() ;
		Pair<URI, URI> compuris = obj.getComponentURIs();
		data = data + "<" + ouri + "> " + StructuralRelation.INDEX_ENTITY_RELATION.getSparqlCode() + " <" +  compuris.getLeft().toString() + "> .\n";
		if (compuris.getRight()!=null) {
			data = data + "<" + ouri + "> ";
			data = data + obj.getRelation().getSparqlCode() + " <" + compuris.getRight().toString() + "> .\n";
		}

		return data;
	}
	
	void buildEntry(DBPhysicalProcess obj, String data) {
		
	}
	
	private String buildEntry(CompBioModel obj, String data) {
		String ouri = obj.getURI().toString();
		CurationalMetadata cmd = obj.getCurationalMetadata();
		for (Metadata m : Metadata.values()) {
			if (cmd.hasAnnotationValue(m)) {
				data = data + "<" + ouri + "> " + m.getSparqlCode() + " '" +  cmd.getAnnotationValue(m) + "' .\n";
			}
		}
		return data;
	}
	
	//Annotate individual with property map
	public void addModeltoIndividual(URI dpc, URI muri) {
		String ui = makePrefixString("owl") + inmodel;
		
		ui = ui.replace("%i", dpc.toString());
		ui = ui.replace("%m", muri.toString());
		
		updateRemote(ui);
	}
	
	//Annotate individual with property map
	protected void addPropertyAxiom(URI dpc, URI puri) {
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
	
	public void addPropertyModelLinks(URI dpcuri, URI ppuri, URI muri) {				
			addPropertyAxiom(dpcuri, ppuri);
			addModeltoAxiom(dpcuri, ppuri, muri);		
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
