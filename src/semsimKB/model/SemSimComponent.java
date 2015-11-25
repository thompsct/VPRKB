package semsimKB.model;

import semsimKB.definitions.SemSimTypes;

/**
 * A SemSim model element. A {@link SemSimModel} extends this class as well.
 */
public abstract class SemSimComponent extends SemSimObject {
	protected SemSimComponent(SemSimTypes sstype) {
		super(sstype);
	}

}
