package semsimKB.utilities;

import java.util.Comparator;

import semsimKB.model.SemSimComponent;

public class SemSimComponentComparator implements Comparator<SemSimComponent>{
	public int compare(SemSimComponent A, SemSimComponent B) {
	    return A.getName().compareToIgnoreCase(B.getName());
	  }
}
