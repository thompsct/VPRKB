/** 
 * Abstract class for producing dialogs with consistent behavior.
 */

package vprExplorer.utilities;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public abstract class CommonDialog extends JDialog{
	private static final long serialVersionUID = 1L;

	private static JFrame location;
	
	public CommonDialog(String title) {
		super(location, title, true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	
	public static void setFrame(JFrame frame) {
		frame = location;
	}
	
	/**
	 * Call when the dialog is ready for display
	 */
	protected void showDialog() {
		pack();

		setLocationRelativeTo(location);
		setVisible(true);
	}
}
