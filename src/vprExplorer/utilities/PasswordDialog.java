package vprExplorer.utilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import vprExplorer.Settings;

public class PasswordDialog extends CommonDialog implements PropertyChangeListener {
	private static final long serialVersionUID = 1L;
	private Settings settings;
	private JPasswordField pwdfield = new JPasswordField();
	private char[] password = {};
	private JOptionPane optionPane;
	
	public PasswordDialog(Settings sets) {
		super("Enter Password");
		settings = sets;
		
		Object[] array = new Object[] { pwdfield };
		Object[] options = new Object[] { "OK", "Cancel" };

		optionPane = new JOptionPane(array, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);
		optionPane.addPropertyChangeListener(this);
		optionPane.setOptions(options);
		optionPane.setInitialValue(options[0]);

		setContentPane(optionPane);

		showDialog();
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if (arg0.getPropertyName()=="value") {
			String value = optionPane.getValue().toString();
			if (value == "OK") {
				password = pwdfield.getPassword();
				if (password.length==0) {
					optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
					return;
				}
				settings.setPassword(password);
			}
			else {
				settings.setPassword(new char[]{});
			}
			dispose();
		}
	}
	
}
