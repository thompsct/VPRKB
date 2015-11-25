package semsimKB.webservices;

import java.net.URI;

import org.apache.commons.lang3.tuple.Pair;

import semsimKB.annotation.CurationalMetadata;
import semsimKB.annotation.CurationalMetadata.Metadata;
import semsimKB.definitions.SemSimTypes;
import semsimKB.definitions.StructuralRelation;
import semsimKB.definitions.SemSimRelation.KBRelations;
import semsimKB.model.CompBioModel;
import semsimKB.model.SemSimObject;
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
	public final String inmodel = "INSERT  {<%i> " + KBRelations.BQB_IS_VERSION_OF.getSPARQLCode() + " <%m>} "
			+ "WHERE { FILTER NOT EXISTS { <%i> " + KBRelations.BQB_IS_VERSION_OF.getSPARQLCode() + " <%m> }}";
	//Insert axiom for property of individual
	public final String sinsertax = "INSERT  { physkb:Propertyof%n a " + SemSimTypes.PROPERTYOF_INSTANCE.getSparqlCode() + ". "
			+ "physkb:Propertyof%n " + KBRelations.BQM_IS.getSPARQLCode() + " <%s>. physkb:Propertyof%n " + KBRelations.PHYSICAL_PROPERTY_OF.getSPARQLCode() + " <%t>}" 
			+ "WHERE { FILTER NOT EXISTS { ?a " + KBRelations.BQM_IS.getSPARQLCode() + " <%s>. ?a " + KBRelations.PHYSICAL_PROPERTY_OF.getSPARQLCode() + " <%t> }}";
	//Update model/property mappings
	public final String addppmod = "INSERT  {?Axiom " + KBRelations.BQM_IS_DESCRIBED_BY.getSPARQLCode() + " <%m>} "
			+ "WHERE {?Axiom " + KBRelations.BQM_IS.getSPARQLCode() + " <%s>. ?Axiom " + KBRelations.PHYSICAL_PROPERTY_OF.getSPARQLCode() + " <%t>."
			+ "FILTER NOT EXISTS {?Axiom " + KBRelations.BQM_IS_DESCRIBED_BY.getSPARQLCode() + " <%m>}}";
	
	
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
		int n = countObj(SemSimTypes.PROPERTYOF_INSTANCE.getSparqlCode())+1;
		
		String ui = makePrefixString("owl") + makePrefixString("physkb") + sinsertax;
		
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
