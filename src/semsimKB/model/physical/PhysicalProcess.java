package semsimKB.model.physical;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import semsimKB.SemSimKBConstants;

public class PhysicalProcess extends PhysicalModelComponent {

	private Set<PhysicalEntity> sources = new HashSet<PhysicalEntity>();
	private Set<PhysicalEntity> sinks = new HashSet<PhysicalEntity>();
	private Set<PhysicalEntity> mediators = new HashSet<PhysicalEntity>();
	
	
	public void addSource(PhysicalEntity entity){
		sources.add(entity);
	}
	
	public void addSink(PhysicalEntity entity){
		sinks.add(entity);
	}

	public void addMediator(PhysicalEntity entity){
		mediators.add(entity);
	}
	
	public Set<PhysicalEntity> getSourcePhysicalEntities(){
		Set<PhysicalEntity> ents = new HashSet<PhysicalEntity>();
		for(PhysicalEntity sp : getSources()){
			ents.add(sp);
		}
		return ents;
	}
	
	public Set<PhysicalEntity> getSinkPhysicalEntities(){
		Set<PhysicalEntity> ents = new HashSet<PhysicalEntity>();
		for(PhysicalEntity sp : getSinks()){
			ents.add(sp);
		}
		return ents;
	}
	
	public Set<PhysicalEntity> getMediatorPhysicalEntities(){
		Set<PhysicalEntity> ents = new HashSet<PhysicalEntity>();
		for(PhysicalEntity sp : getMediators()){
			ents.add(sp);
		}
		return ents;
	}

	public void setSources(Set<PhysicalEntity> sources) {
		this.sources = sources;
	}

	public Set<PhysicalEntity> getSources() {
		return sources;
	}

	public void setSinks(Set<PhysicalEntity> sinks) {
		this.sinks = sinks;
	}

	public Set<PhysicalEntity> getSinks() {
		return sinks;
	}

	public void setMediators(Set<PhysicalEntity> mediators) {
		this.mediators = mediators;
	}

	public Set<PhysicalEntity> getMediators() {
		return mediators;
	}
	
	public Set<PhysicalEntity> getParticipants(){
		Set<PhysicalEntity> allps = new HashSet<PhysicalEntity>();
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
