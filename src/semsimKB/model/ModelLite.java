//Container for model being imported into the knowledge base
package semsimKB.model;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import semsim.model.physical.CustomPhysicalEntity;
import semsim.model.physical.CustomPhysicalProcess;
import semsim.model.physical.PhysicalEntity;
import semsim.model.physical.PhysicalModelComponent;
import semsim.model.physical.PhysicalProcess;
import semsim.model.physical.ReferencePhysicalProcess;
import semsimKB.Annotatable;
import semsimKB.SemSimKBConstants;
import semsimKB.annotation.Annotation;
import semsimKB.annotation.ReferenceOntologyAnnotation;
import semsimKB.annotation.SemSimRelation;
import semsimKB.annotation.StructuralRelation;
import semsimKB.model.physical.CompositePhysicalEntity;
import semsimKB.model.physical.PhysicalProperty;
import semsimKB.model.physical.ReferencePhysicalEntity;

public class ModelLite extends SemSimComponent implements Cloneable, Annotatable {	
	//Physical Model Components

	protected Set<PhysicalEntity> physicalentities = new HashSet<PhysicalEntity>();
	protected Set<PhysicalProcess> physicalprocesses = new HashSet<PhysicalProcess>();
	protected Set<PhysicalProperty> physicalproperties = new HashSet<PhysicalProperty>();
	protected Set<Annotation> annotations = new HashSet<Annotation>();

	protected Set<String> errors = new HashSet<String>();
	
	// Model-specific data
	protected String namespace;
	
	protected static SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmssSSSZ");
	protected double semSimVersion = SemSimKBConstants.SEMSIM_VERSION;
	protected int sourceModelType;

	
	/**
	 * Constructor without namespace
	 */
	public ModelLite(){
		setNamespace(generateNamespaceFromDateAndTime());
	}
	
	
	/**
	 * Constructor with namespace
	 */
	public ModelLite(String namespace){
		setNamespace(namespace);
	}
		
	/**
	 * Add an error to the model. Errors are just string notifications indicating a 
	 * problem with the model
	 * @param error A string describing the error
	 */
	public void addError(String error){
		errors.add(error);
	}
	//Remove all data from model object	
	public void clear() {
		this.removeAllReferenceAnnotations();
		
		setURI(null); setName(""); namespace = ""; 
		 physicalentities.clear(); physicalprocesses.clear();
		 errors.clear(); annotations.clear();
		 
	}
	
	
	
	/**
	 * Add a new {@link CompositePhysicalEntity} to the model. 
	 * 
	 * @param cpe The CompositePhysicalEntity to be added.
	 */
	public CompositePhysicalEntity addCompositePhysicalEntity(CompositePhysicalEntity cpe){
		return addCompositePhysicalEntity(cpe.getArrayListOfEntities(), cpe.getArrayListOfStructuralRelations());
	}
	
	
	/**
	 * Add a new {@link CompositePhysicalEntity} to the model. 
	 * 
	 * @param entlist The ArrayList of physical entities for the composite entity.
	 * @param rellist The ArrayList of SemSimRelations that logically link the physical entities ("part of", "contained in", etc.).
	 * @return If the model already contains a semantically-identical composite physical entity, that entity is returned.
	 * Otherwise, the new CompositePhysicalEntity added to the model is returned.
	 */
	public CompositePhysicalEntity addCompositePhysicalEntity(ArrayList<PhysicalEntity> entlist, ArrayList<StructuralRelation> rellist){
		CompositePhysicalEntity newcpe = new CompositePhysicalEntity(entlist, rellist);
		for(CompositePhysicalEntity cpe : getCompositePhysicalEntities()){
			// If there's already an equivalent CompositePhysicalEntity in the model, return it and don't do anything else.
			if(newcpe.compareTo(cpe)==0){
				return cpe;
			}
		}
		physicalentities.add(newcpe);
		
		// If there are physical entities that are part of the composite but not yet added to the model, add them
		// This comes into play when importing annotations from other models
		for(PhysicalEntity ent : newcpe.getArrayListOfEntities()){
			if(!getPhysicalEntities().contains(ent)){
				if(ent.hasRefersToAnnotation()){
					ReferenceOntologyAnnotation roa = ent.getFirstRefersToReferenceOntologyAnnotation();
					URI roauri = roa.getReferenceURI();
					addReferencePhysicalEntity(roauri, roa.getValueDescription());
					
					String curi = SemSimKBConstants.SEMSIM_NAMESPACE.subSequence(0, SemSimKBConstants.SEMSIM_NAMESPACE.length()) 
							+ roauri.toString().substring(roauri.toString().lastIndexOf('/')+1, roauri.toString().length());
					ent.setURI(URI.create(curi));
				}
				else addCustomPhysicalEntity(ent.getName(), ent.getDescription());
			}
		}
		return newcpe;
	}
	
	
	/**
	 * Add a new {@link CustomPhysicalEntity} to the model. 
	 * 
	 * @param name The name of the CustomPhysicalEntity to be added.
	 * @param description A free-text description of the CustomPhysicalEntity
	 * @return If the model already contains a CustomPhysicalEntity with the same name, it is returned.
	 * Otherwise a new CustomPhysicalEntity with the name and description specified is returned.
	 */
	public CustomPhysicalEntity addCustomPhysicalEntity(String name, String description){
		CustomPhysicalEntity custompe = null;
		if(getCustomPhysicalEntityByName(name)!=null) custompe = getCustomPhysicalEntityByName(name);
		else{
			custompe = new CustomPhysicalEntity(name, description);
			physicalentities.add(custompe);
		}
		return custompe;
	}
	
	
	/**
	 * Add a new {@link CustomPhysicalProcess} to the model. 
	 * 
	 * @param name The name of the CustomPhysicalProcess to be added.
	 * @param description A free-text description of the CustomPhysicalProcess
	 * @return If the model already contains a CustomPhysicalProcess with the same name, it is returned.
	 * Otherwise a new CustomPhysicalProcess with the name and description specified is returned.
	 */
	public CustomPhysicalProcess addCustomPhysicalProcess(String name, String description){
		CustomPhysicalProcess custompp = null;
		if(getCustomPhysicalProcessByName(name)!=null) custompp = getCustomPhysicalProcessByName(name);
		else{
			custompp = new CustomPhysicalProcess(name, description);
			physicalprocesses.add(custompp);
		}
		return custompp;
	}
	
	
	/**
	 * Add a new ReferencePhysicalEntity to the model. ReferencePhysicalEntities are subclasses of
	 * PhysicalEntities that are defined by their annotation against a reference ontology URI. In
	 * other words, a {@link PhysicalEntity} that is annotated using the SemSimConstants:REFERS_TO_RELATION.
	 * 
	 * @param uri The URI of the reference ontology term that defines the entity.
	 * @param description A free-text description of the entity. Usually taken from the reference ontology.
	 * @return If the model already contains a ReferencePhysicalEntity with the same URI, it is returned.
	 * Otherwise a new ReferencePhysicalEntity with the URI and description specified is returned.
	 */
	public ReferencePhysicalEntity addReferencePhysicalEntity(URI uri, String description){
		ReferencePhysicalEntity rpe = null;
		if(getPhysicalEntityByReferenceURI(uri)!=null) rpe = getPhysicalEntityByReferenceURI(uri);
		else{
			rpe = new ReferencePhysicalEntity(uri, description);
			physicalentities.add(rpe);
		}
		return rpe;
	}
	
	
	/**
	 * Add a new ReferencePhysicalProcess to the model. ReferencePhysicalProcesses are subclasses of
	 * PhysicalProcesses that are defined by their annotation against a reference ontology URI. In
	 * other words, a {@link PhysicalProcess} that is annotated using the SemSimConstants:REFERS_TO_RELATION.
	 * 
	 * @param uri The URI of the reference ontology term that defines the process.
	 * @param description A free-text description of the process. Usually taken from the reference ontology.
	 * @return If the model already contains a ReferencePhysicalProcess with the same URI, it is returned.
	 * Otherwise a new ReferencePhysicalProcess with the URI and description specified is returned.
	 */
	public ReferencePhysicalProcess addReferencePhysicalProcess(URI uri, String description){
		ReferencePhysicalProcess rpp = null;
		if(getPhysicalProcessByReferenceURI(uri)!=null) rpp = getPhysicalProcessByReferenceURI(uri);
			else physicalprocesses.add(new ReferencePhysicalProcess(uri, description));
		return rpp;
	}
	
	/**
	 * 
	 * @return The model's namespace.
	 */
	public String getNamespace(){
		return namespace;
	}
	
	
	/**
	 * @return A Map where the KeySet includes all PhysicalProperties in the model that are properties of 
	 * CompositePhyiscalEntities. The value for each key is the CompositePhysicalEntity that possesses the property. 
	 */
	public Map <PhysicalProperty,CompositePhysicalEntity> getPropertyAndCompositePhysicalEntityMap(){
		Map<PhysicalProperty,CompositePhysicalEntity> table = new HashMap<PhysicalProperty,CompositePhysicalEntity>();
		for(PhysicalProperty pp: getPhysicalProperties()){
			if(pp.getPhysicalPropertyOf() instanceof CompositePhysicalEntity){
				CompositePhysicalEntity cpe = (CompositePhysicalEntity) pp.getPhysicalPropertyOf();
				table.put(pp,cpe);
			}
		}
		return table;
	}
	
	
	/**
	 * @return A Map where the KeySet includes all PhysicalProperties in the model that are properties of 
	 * PhysicalProcesses. The value for each key is the PhysicalProcess that possesses the property. 
	 */
	public Map <PhysicalProperty,PhysicalProcess> getPropertyAndPhysicalProcessTable(){
		Map<PhysicalProperty,PhysicalProcess> table = new HashMap<PhysicalProperty,PhysicalProcess>();
		for(PhysicalProperty pp: getPhysicalProperties()){
			if(pp.getPhysicalPropertyOf() instanceof PhysicalProcess){
				PhysicalProcess pproc = (PhysicalProcess) pp.getPhysicalPropertyOf();
				table.put(pp,pproc);
			}
		}
		return table;
	}
	
	
	/**
	 * @return A Map where the KeySet includes all PhysicalProperties in the model that are properties of 
	 * PhysicalEntities (composite or singular). The value for each key is the PhysicalEntity that possesses the property. 
	 */
	public Map <PhysicalProperty,PhysicalEntity> getPropertyAndPhysicalEntityMap(){
		Map<PhysicalProperty,PhysicalEntity> table = new HashMap<PhysicalProperty,PhysicalEntity>();
		for(PhysicalProperty pp: getPhysicalProperties()){
			if(pp.getPhysicalPropertyOf() instanceof PhysicalEntity){
				PhysicalEntity ent = (PhysicalEntity) pp.getPhysicalPropertyOf();
				table.put(pp,ent);
			}
		}
		return table;
	}
	
	
	/**
	 * @return All PhysicalEntities in the model. 
	 */
	public Set<PhysicalEntity> getPhysicalEntities() {
		return physicalentities;
	}
	
	
	/**
	 * @return All PhysicalEntities in the model, except those that either are, or use, a specifically excluded entity 
	 */
	public Set<PhysicalEntity> getPhysicalEntitiesAndExclude(PhysicalEntity entityToExclude) {
		Set<PhysicalEntity> includedents = new HashSet<PhysicalEntity>();
		
		if(entityToExclude!=null){
			for(PhysicalEntity pe : getPhysicalEntities()){
				// If pe is a composite entity, check if it uses the entityToExclue, ignore if so
				if(pe instanceof CompositePhysicalEntity){
					if(!((CompositePhysicalEntity) pe).getArrayListOfEntities().contains(entityToExclude))
						includedents.add(pe);
				}
				else if(pe!=entityToExclude) includedents.add(pe);
			}
		}
		else return getPhysicalEntities();
		
		return includedents;
	}
	
	
	/**
	 * Specify the set of PhysicalEntities in the model. 
	 */
	public void setPhysicalEntities(Set<PhysicalEntity> physicalentities) {
		physicalentities = new HashSet<PhysicalEntity>(physicalentities);
	}

	
	/**
	 * @return All the CompositePhysicalEntities in the model. 
	 */
	public Set<CompositePhysicalEntity> getCompositePhysicalEntities(){
		Set<CompositePhysicalEntity> set = new HashSet<CompositePhysicalEntity>();
		for(PhysicalEntity ent : getPhysicalEntities()){
			if(ent instanceof CompositePhysicalEntity){
				CompositePhysicalEntity cpe = (CompositePhysicalEntity)ent;
				set.add(cpe);
			}
		}
		return set;
	}

	
	/**
	 * @return All ReferencePhysicalEntities in the model.
	 */
	public Set<ReferencePhysicalEntity> getReferencePhysicalEntities(){
		Set<ReferencePhysicalEntity> refents = new HashSet<ReferencePhysicalEntity>();
		for(PhysicalEntity ent : getPhysicalEntities()){
			if(ent instanceof ReferencePhysicalEntity) refents.add((ReferencePhysicalEntity) ent);
		}
		return refents;
	}
	
	
	/**
	 * @return All PhysicalProperties in the model.
	 */
	public Set<PhysicalProperty> getPhysicalProperties() {
		return physicalproperties;
	}
	

	/**
	 * @return Retrieves all PhysicalEntities, PhysicalProperties and PhsicalProcesses in the model
	 */
	public Set<PhysicalModelComponent> getPhysicalModelComponents(){
		Set<PhysicalModelComponent> set = new HashSet<PhysicalModelComponent>();
		set.addAll(getPhysicalProperties());
		set.addAll(getPhysicalEntities());
		set.addAll(getPhysicalProcesses());
		return set;
	}
	
	
	/**
	 * @param uri A reference term URI
	 * @return The {@link PhysicalModelComponent} that is annotated against the URI using the REFERS_TO_RELATION.
	 */
	public PhysicalModelComponent getPhysicalModelComponentByReferenceURI(URI uri){
		for(PhysicalModelComponent pmcomp : getPhysicalModelComponents()){
			for(ReferenceOntologyAnnotation ann : pmcomp.getReferenceOntologyAnnotations(SemSimKBConstants.REFERS_TO_RELATION)){
				if(ann.getReferenceURI().compareTo(uri)==0) return pmcomp;
			}
		}
		return null;
	}
	
	
	/**
	 * @param uri A reference term URI
	 * @return The {@link ReferencePhysicalEntity} that is annotated against the URI using the REFERS_TO_RELATION.
	 * If no ReferencePhysicalEntities have been annotated against the URI, null is returned.
	 */
	public ReferencePhysicalEntity getPhysicalEntityByReferenceURI(URI uri){
		if(getPhysicalModelComponentByReferenceURI(uri)!=null){
			Annotatable pmc = getPhysicalModelComponentByReferenceURI(uri);
			if(pmc instanceof ReferencePhysicalEntity) return (ReferencePhysicalEntity)pmc;
		}
		return null;
	}
	
	
	/**
	 * @param uri A reference term URI
	 * @return The {@link ReferencePhysicalProcess} that is annotated against the URI using the REFERS_TO_RELATION.
	 * If no ReferencePhysicalProcess has been annotated against the URI, null is returned.
	 */
	public ReferencePhysicalProcess getPhysicalProcessByReferenceURI(URI uri){
		Annotatable pmc = getPhysicalModelComponentByReferenceURI(uri);
		if(pmc!=null){
			if(pmc instanceof ReferencePhysicalProcess) return (ReferencePhysicalProcess)pmc;
		}
		return null;
	}
	
	
	/**
	 * @return The set of all CustomPhysicalEntities in the model.
	 */
	public Set<CustomPhysicalEntity> getCustomPhysicalEntities(){
		Set<CustomPhysicalEntity> custs = new HashSet<CustomPhysicalEntity>();
		for(PhysicalEntity ent : getPhysicalEntities()){
			if(ent instanceof CustomPhysicalEntity) custs.add((CustomPhysicalEntity) ent);
		}
		return custs;
	}
	
	
	/**
	 * @param name The name of CustomPhysicalEntity to return
	 * @return The CustomPhysicalEntity with the specified name or null if no match was found.
	 */
	public CustomPhysicalEntity getCustomPhysicalEntityByName(String name){
		for(PhysicalModelComponent apmc : getPhysicalEntities()){
			if(apmc instanceof CustomPhysicalEntity){
				if(apmc.getName().equals(name)) return (CustomPhysicalEntity)apmc;
			}
		}
		return null;
	}
	
	
	/**
	 * @return The set of all CustomPhysicalProcesses in the model.
	 */
	public Set<CustomPhysicalProcess> getCustomPhysicalProcesses(){
		Set<CustomPhysicalProcess> custs = new HashSet<CustomPhysicalProcess>();
		for(PhysicalProcess proc : getPhysicalProcesses()){
			if(proc instanceof CustomPhysicalProcess) custs.add((CustomPhysicalProcess) proc);
		}
		return custs;
	}
	
	
	/**
	 * @param name The name of CustomPhysicalProcess to return
	 * @return The CustomPhysicalProcess with the specified name or null if no match was found.
	 */
	public CustomPhysicalProcess getCustomPhysicalProcessByName(String name){
		for(PhysicalModelComponent apmc : getPhysicalProcesses()){
			if(apmc instanceof CustomPhysicalProcess){
				if(apmc.getName().equals(name)) return (CustomPhysicalProcess)apmc;
			}
		}
		return null;
	}
	
	public ArrayList<String> getCompositeNames() {
		ArrayList<String> compnames = new ArrayList<String>();
		
		for (CompositePhysicalEntity cpe : getCompositePhysicalEntities()) {
			compnames.add(cpe.getName());
		}
		return compnames;
	}		
	
		
	/**
	 * @return The version of the SemSim API used to generate the model.
	 */
	public double getSemSimVersion() {
		return semSimVersion;
	}
	
	/**
	 * Set the namespace of the model.
	 * @param namespace The namespace to use.
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	/**
	 * @return A new SemSim model namespace from the current date and time
	 */
	public String generateNamespaceFromDateAndTime(){
		namespace = SemSimKBConstants.SEMSIM_NAMESPACE.replace("#", "/" + sdf.format(new Date()).replace("-", "m").replace("+", "p") + "#");
		return namespace;
	}

	
	/**
	 * @return All ReferenceOntologyAnnotations in the model.
	 */
	public Set<ReferenceOntologyAnnotation> getReferenceOntologyAnnotations() {
		Set<ReferenceOntologyAnnotation> raos = new HashSet<ReferenceOntologyAnnotation>();
		for(Annotation ann : getAnnotations()){
			if(ann instanceof ReferenceOntologyAnnotation) raos.add((ReferenceOntologyAnnotation)ann);
		}
		return raos;
	}
	
	/**
	 * Specify the set of PhysicalProcesses in the model.
	 * @param physicalprocess The new set of PhysicalProcesses to include
	 */
	public void setPhysicalProcesses(Set<PhysicalProcess> physicalprocess) {
		physicalprocesses = new HashSet<PhysicalProcess>(physicalprocess);
	}

	
	/**
	 * @return All {@link PhysicalProcess}es in the model.
	 */
	public Set<PhysicalProcess> getPhysicalProcesses() {
		return physicalprocesses;
	}
	
	
	/**
	 * @return All {@link PhysicalProcess}es in the model that are annotated against
	 * a URI with the REFERS_TO_RELATION (as opposed to CustomPhysicalProcesses).
	 */
	public Set<ReferencePhysicalProcess> getReferencePhysicalProcesses(){
		Set<ReferencePhysicalProcess> refprocs = new HashSet<ReferencePhysicalProcess>();
		for(PhysicalProcess proc : getPhysicalProcesses()){
			if(proc instanceof ReferencePhysicalProcess) refprocs.add((ReferencePhysicalProcess) proc);
		}
		return refprocs;
	}


	public void setPhysicalProperties(Set<PhysicalProperty> physicalproperties) {
		this.physicalproperties = physicalproperties;
	}
	
	/**
	 * Set the errors associated with the model.
	 * @param errors A set of errors written as strings.
	 */
	public void setErrors(Set<String> errors) {
		this.errors = errors;
	}

	
	/**
	 * @return All errors associated with the model.
	 */
	public Set<String> getErrors() {
		return errors;
	}
	
	
	/**
	 * @return The number of errors associated with the model.
	 */
	public int getNumErrors(){
		return errors.size();
	}
	
	
	/**
	 * Print all the errors associated with the model to System.err.
	 */
	public void printErrors(){
		for(String err : getErrors()) System.err.println("***************\n" + err + "\n");
	}
	
	/**
	 * Remove a physical entity from the model cache
	 * @param ent The physical entity to remove
	 */
	public void removePhysicalEntityFromCache(PhysicalEntity ent){
		if(physicalentities.contains(ent))
			physicalentities.remove(ent);
	}
	
	/**
	 * Remove a physical process from the model cache
	 * @param ent The physical process to remove
	 */
	public void removePhysicalProcessFromCache(PhysicalProcess ent){
		if(physicalprocesses.contains(ent))
			physicalprocesses.remove(ent);
	}
	
	/**
	 * @return A clone of the model.
	 */
	public ModelLite clone() throws CloneNotSupportedException {
        return (ModelLite) super.clone();
	}

	/**
	 * Specify which modeling language was used for the original version of the model.
	 * See {@link ModelClassifier} constants. 
	 * @param originalModelType An integer corresponding to the language of the original model code (see {@link ModelClassifier} ).
	 */
	public void setSourceModelType(int originalModelType) {
		this.sourceModelType = originalModelType;
	}

	
	/**
	 * @return An integer representing the language of the original model code (see {@link ModelClassifier} ) 
	 * and associated constants.
	 */
	public int getSourceModelType() {
		return sourceModelType;
	}
	
	
	// Required by annotable interface:
	/**
	 * @return All SemSim Annotations applied to an object
	 */
	public Set<Annotation> getAnnotations() {
		return annotations;
	}
	
	
	/**
	 * Set the SemSim Annotations for an object
	 * @param annset The set of annotations to apply
	 */
	public void setAnnotations(Set<Annotation> annset){
		annotations = new HashSet<Annotation>(annset);
	}

	
	/**
	 * Add a SemSim {@link Annotation} to this object
	 * @param ann The {@link Annotation} to add
	 */
	public void addAnnotation(Annotation ann) {
		annotations.add(ann);
	}
	
	
	/**
	 * Add a SemSim {@link ReferenceOntologyAnnotation} to an object
	 * 
	 * @param relation The {@link SemSimRelation} that qualifies the
	 * relationship between the object and what it's annotated against
	 * @param uri The URI of the reference ontology term used for
	 * annotation
	 * @param description A free-text description of the reference
	 * ontology term (obtained from the ontology itself whenever possible). 
	 */
	public void addReferenceOntologyAnnotation(SemSimRelation relation, URI uri, String description){
		addAnnotation(new ReferenceOntologyAnnotation(relation, uri, description));
	}

	
	/**
	 * Get all SemSim {@link ReferenceOntologyAnnotation}s applied to an object
	 * that have a specific {@link SemSimRelation}.
	 * 
	 * @param relation The {@link SemSimRelation} that filters the annotations 
	 * to return  
	 */
	public Set<ReferenceOntologyAnnotation> getReferenceOntologyAnnotations(SemSimRelation relation) {
		Set<ReferenceOntologyAnnotation> raos = new HashSet<ReferenceOntologyAnnotation>();
		for(Annotation ann : getAnnotations()){
			if(ann instanceof ReferenceOntologyAnnotation && ann.getRelation()==relation)
				raos.add((ReferenceOntologyAnnotation)ann);
		}
		return raos;
	}
	
	
	/**
	 * Retrieve the first {@link ReferenceOntologyAnnotation} found applied to this object
	 * that uses the SemSim:refersTo relation (SemSimConstants.REFERS_TO_RELATION).
	 */
	public ReferenceOntologyAnnotation getFirstRefersToReferenceOntologyAnnotation(){
		if(!getReferenceOntologyAnnotations(SemSimKBConstants.REFERS_TO_RELATION).isEmpty())
			return getReferenceOntologyAnnotations(SemSimKBConstants.REFERS_TO_RELATION).toArray(new ReferenceOntologyAnnotation[]{})[0];
		return null;
	}
	
	
	/**
	 * Retrieve the first {@link ReferenceOntologyAnnotation} found applied to this object
	 * that uses the SemSim:refersTo relation (SemSimConstants.REFERS_TO_RELATION) and references
	 * a specific URI
	 * 
	 * @param uri The URI of the ontology term to search for in the set of {@link ReferenceOntologyAnnotation}s
	 * applied to this object.
	 */
	public ReferenceOntologyAnnotation getRefersToReferenceOntologyAnnotationByURI(URI uri){
		for(ReferenceOntologyAnnotation ann : getReferenceOntologyAnnotations(SemSimKBConstants.REFERS_TO_RELATION)){
			if(ann.getReferenceURI().compareTo(uri)==0) return ann;
		}
		return null;
	}
	
	
	/**
	 * @return True if an object has at least one {@link Annotation}, otherwise false.
	 */
	public Boolean isAnnotated(){
		return !getAnnotations().isEmpty();
	}
	
	
	/**
	 * @return True if an object has at least one {@link ReferenceOntologyAnnotation}, otherwise false;
	 */
	public Boolean hasRefersToAnnotation(){
		return getFirstRefersToReferenceOntologyAnnotation()!=null;
	}

	public PhysicalProperty getPhysicalPropertybyURI(URI uri) {
		for ( PhysicalProperty pp : getPhysicalProperties()) {
			if (pp.getURI().equals(uri)) 
				return pp;
		}
		
		return null;
	}
	
	/**
	 * Delete all {@link ReferenceOntologyAnnotation}s applied to this object
	 */
	public void removeAllReferenceAnnotations() {
		Set<Annotation> newset = new HashSet<Annotation>();
		for(Annotation ann : getAnnotations()){
			if(!(ann instanceof ReferenceOntologyAnnotation)) newset.add(ann);
		}
		annotations = new HashSet<Annotation>(newset);
	}
	// End of methods required by Annotatable interface
}
