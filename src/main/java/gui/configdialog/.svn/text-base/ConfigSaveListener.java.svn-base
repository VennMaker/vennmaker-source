package gui.configdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import files.IO;

/**
 * Objects of this class listen for save or loading template actions
 * 
 *
 */
public class ConfigSaveListener implements ActionListener
{

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (arg0.getActionCommand().equals("save")) //$NON-NLS-1$
			IO.saveTemplate();
		else if (arg0.getActionCommand().equals("load"))
			IO.loadTemplate(false);
	}
}