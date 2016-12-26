/**
 * 
 */
package gui.admin;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * The GUI logic for the text box dialog in configuration mode.
 * The GUI form is inherited and has no logic.
 * 
 * 
 *
 */
public class TextBoxDialogImpl extends TextBoxDialog
{
	private boolean cancelled = true;

	/**
	 * Creates the text box dialog for an already existing text wizard definition.
	 * The main difference to <code>TextBoxDialogImpl(Frame,boolean)</code> is that
	 * this constructor is able to define the string values for the guis controls.
	 * Furthermore some messages are different to help the user!
	 * @param parent The parent of the dialog.
	 * @param modal Whether the dialog should be modal
	 * @param content The content of the wizard's text box ( must not be <code>null</code>)
	 */
	public TextBoxDialogImpl(Frame parent, boolean modal, String content)
	{
		this(parent, modal);
		this.wizardContentBox.setText(content);
	}
	
	/**
	 * Creates the text box dialog for a new text wizard definition.
	 * @param parent The parent of the dialog.
	 * @param modal Whether this dialog has to be modal. If <code>true</code> the event queue of all
	 * other dialogs is suspended and hence this dialog is the only active one of the application.
	 */
	public TextBoxDialogImpl(Frame parent, boolean modal)
	{
		super(parent, modal);
		
		initGUIComponents();
	}
	
	/**
	 * Returns <code>true</code> if the dialog was cancelled (i.e. pressing the cancel button or the window close icon).
	 * In this case all getter-methods throw an exception instead of returning valid values.
	 * @return <code>false</code> if the dialog was closed via the ok-button.
	 */
	public boolean isCancelled()
	{
		return this.cancelled;
	}

	
	/**
	 * Returns the content of the text box wizard. 
	 * @return A valid string containing anything but <code>null</code>. :-)
	 * @throws UnsupportedOperationException If the dialog was cancelled nothing must be read from the dialog!
	 */
	public String getWizardText()
	throws UnsupportedOperationException
	{
		if (this.cancelled)
			throw new UnsupportedOperationException("Read-access to a cancelled dialog is not allowed.");
		
		return this.wizardContentBox.getText();
	}
	
	/**
	 * Creates the static GUI logic. 
	 */
	private void initGUIComponents()
	{
		this.okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				cancelled = false;
				setVisible(false);
			} });
		this.cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				cancelled = true;
				setVisible(false);
			} });
	}
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	/* private javax.swing.JLabel jLabel2;
       private javax.swing.JButton cancelButton;
           private javax.swing.JButton previewButton;
           private javax.swing.JLabel jLabel3;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JScrollPane jScrollPane1;
   private org.jdesktop.swingx.JXHeader jXHeader1;
   private javax.swing.JTextArea wizardContentBox;
   private javax.swing.JTextField wizardNameField; */
}
