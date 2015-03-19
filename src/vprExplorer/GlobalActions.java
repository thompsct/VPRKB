package vprExplorer;

import java.util.Observable;

/**
 * Class for notifying application level classes of requests and events 
 * from elsewhere in SemGen. Contains methods for passing a file
 * to classes outside of the calling object's ancestor hierarchy.
 */
public class GlobalActions extends Observable {
	public static enum appactions {
		QUIT
	};
	
	GlobalActions() {}

	public void quit() {
		setChanged();
		notifyObservers(appactions.QUIT);
	}
}
