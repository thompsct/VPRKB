package semsimKB.model.physical;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import semsimKB.SemSimKBConstants;

public class PhysicalProcess extends PhysicalModelComponent{

	private Set<ProcessParticipant> sources = new HashSet<ProcessParticipant>();
	private Set<ProcessParticipant> sinks = new HashSet<ProcessParticipant>();
	private Set<ProcessParticipant> mediators = new HashSet<ProcessParticipant>();
	
	
	public void addSource(PhysicalEntity entity, double cardinality){
		sources.add(new ProcessParticipant(entity, cardinality));
	}
	
	public void addSink(PhysicalEntity entity, double cardinality){
		sinks.add(new ProcessParticipant(entity, cardinality));
	}
	
	public void addMediator(PhysicalEntity entity, double cardinality){
		mediators.add(new ProcessParticipant(entity, cardinality));
	}
	
	public Set<PhysicalEntity> getSourcePhysicalEntities(){
		Set<PhysicalEntity> ents = new HashSet<PhysicalEntity>();
		for(ProcessParticipant sp : getSources()){
			ents.add(sp.getPhysicalEntity());
		}
		return ents;
	}
	
	public Set<PhysicalEntity> getSinkPhysicalEntities(){
		Set<PhysicalEntity> ents = new HashSet<PhysicalEntity>();
		for(ProcessParticipant sp : getSinks()){
			ents.add(sp.getPhysicalEntity());
		}
		return ents;
	}
	
	public Set<PhysicalEntity> getMediatorPhysicalEntities(){
		Set<PhysicalEntity> ents = new HashSet<PhysicalEntity>();
		for(ProcessParticipant sp : getMediators()){
			ents.add(sp.getPhysicalEntity());
		}
		return ents;
	}

	public void setSources(Set<ProcessParticipant> sources) {
		this.sources = sources;
	}

	public Set<ProcessParticipant> getSources() {
		return sources;
	}

	public void setSinks(Set<ProcessParticipant> sinks) {
		this.sinks = sinks;
	}

	public Set<ProcessParticipant> getSinks() {
		return sinks;
	}

	public void setMediators(Set<ProcessParticipant> mediators) {
		this.mediators = mediators;
	}

	public Set<ProcessParticipant> getMediators() {
		return mediators;
	}
	
	public Set<ProcessParticipant> getParticipants(){
		Set<ProcessParticipant> allps = new HashSet<ProcessParticipant>();
		allps.addAll(getSources());
		allps.addAll(getSinks());
		allps.addAll(getMediators());
		return allps;
	}
	
	// Get all sources, sinks and mediators as PhysicalEntities
	public Set<PhysicalEntity> getParticipantsAsPhysicalEntities(){
		Set<PhysicalEntity> allpents = new HashSet<PhysicalEntity>();
		allpents.addAll(getSourcePhysicalEntities());
		allpents.addAll(getSinkPhysicalEntities());
		allpents.addAll(getMediatorPhysicalEntities());
		return allpents;
	}
	@Override
	public  URI getClassURI() {
		return SemSimKBConstants.PHYSICAL_PROCESS_CLASS_URI;
	}
}
