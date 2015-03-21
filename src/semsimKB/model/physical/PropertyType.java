package semsimKB.model.physical;

public enum PropertyType {
	Unknown (0, "Constitutive"),
	PropertyOfPhysicalEntity (1, "State"),
	PropertyOfPhysicalProcess (2, "Rate");
	
	private final String _name;
	private final int _index;

    private PropertyType(int index, String name) {
        _name = name;
        _index = index;
    }

    public int getIndex() {
    	return _index;
    }
    
    public String toString(){
       return _name;
    }
}
