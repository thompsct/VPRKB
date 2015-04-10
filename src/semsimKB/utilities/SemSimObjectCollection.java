package semsimKB.utilities;

import java.util.ArrayList;
import java.util.Collection;

import semsimKB.model.SemSimComponent;

public class SemSimObjectCollection {

	public static ArrayList<String> getListofComponentNames(Collection<? extends SemSimComponent> modcomplist) {
		ArrayList<String> namelist = new ArrayList<String>();
		for (SemSimComponent ssc : modcomplist) {
			namelist.add(ssc.getName());
		}
		return namelist;
	}
	
}
