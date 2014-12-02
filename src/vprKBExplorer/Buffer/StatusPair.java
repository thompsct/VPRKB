package vprKBExplorer.Buffer;

import java.awt.Color;

import semsimKB.SemSimKBConstants.kbcomponentstatus;

public class StatusPair {
		public int i; public kbcomponentstatus s;
		public StatusPair(int rowindex, kbcomponentstatus status) {
			i = rowindex; s =status;
		}
		
		public StatusPair() {}

		public void setPair(int rowindex, kbcomponentstatus status) {
			i = rowindex; s =status;
		}
		
		public Color statusColor() {						
			Color cellcolor = new Color(255, 255, 255);
			switch (s) {
			case MISSING:
				cellcolor = new Color(255,0,0);
				break;
			case EXACT_MATCH:
				cellcolor = new Color(0,255,0);
				break;
			case NEW_INSTANCE_MODEL:
				cellcolor = new Color(0,255,255);
				break;
			case NEW_INSTANCE_PHYS_PROPERTY:
				cellcolor = new Color(255,255,0);
				break;	
			case NEW_ASSOCIATED_PHYS_PROPERTY:
				cellcolor = new Color(255,0,255);
				break;
			case EXTERNAL_TO_MODEL:
				cellcolor = new Color(255,255,255);
				break;
			}
			return cellcolor;
		}
}
