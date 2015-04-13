package vprExplorer.buffer;

import java.awt.Color;

public enum ComponentStatus {
	EXACT_MATCH("Exact Match", Color.green), 
	MISSING("Missing", Color.red), 
	NEW_INSTANCE_MODEL("New Model", Color.orange), 
	NEW_PROPERTY_MODEL_ASSOCIATION("New Property", Color.cyan), 
	NEW_ASSOCIATED_PHYS_PROPERTY("New Property Association", Color.yellow), 
	NEW_ASSOCIATED_MODEL("New Model Association", Color.orange), 
	EXTERNAL_TO_MODEL("", Color.white);
		
	String meaning;
	Color color;
	
	ComponentStatus(String tip, Color col) {
		meaning = tip;
		color = col;
	}
	
	public String getMeaning() {
		return meaning;
	}
	
	public Color getColor() {
		return color;
	}
}
