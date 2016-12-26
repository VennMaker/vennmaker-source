package gui.configdialog;

import gui.Messages;
import gui.VennMaker;
import gui.configdialog.individual.EditIndividualAttributeTypeDialog;
import gui.configdialog.settings.SettingAddAttributeType;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import data.AttributeType;

public class NewAttributeListener implements ActionListener
{
	private String				getType;

	private AttributeType	attr	= null;

	public NewAttributeListener()
	{
		this("ACTOR"); //$NON-NLS-1$
	}

	public NewAttributeListener(String getType)
	{
		this.getType = getType;
	}

	private boolean existingName(String s)
	{
		s = s.toLowerCase();
		for (AttributeType a : VennMaker.getInstance().getProject()
				.getAttributeTypes())
		{
			if (a.getLabel().toLowerCase().equals(s))
				return true;
		}
		return false;
	}

	public AttributeType getAttributeType()
	{
		return attr;
	}

	public void actionPerformed(ActionEvent e)
	{
		attr = null;
		Icon icon = new ImageIcon("icons/intern/icon.png"); //$NON-NLS-1$
		String label = (String) JOptionPane
				.showInputDialog(
						ConfigDialog.getInstance(),
						Messages.getString("EditIndividualAttributeTypeDialog.15"), Messages.getString("EditIndividualAttributeTypeDialog.19"), JOptionPane.QUESTION_MESSAGE, icon, null, null); //$NON-NLS-1$ //$NON-NLS-2$
		if (label != null)
		{
			if (existingName(label))
			{
				JOptionPane
						.showMessageDialog(
								null,
								Messages.getString("VennMaker.Allready_Existing"), Messages //$NON-NLS-1$
										.getString("VennMaker.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			}
			else if (!label.equals("")) //$NON-NLS-1$
			{
				attr = new AttributeType();
				attr.setType(getType);
				attr.setLabel(label);
			}
			else
				JOptionPane
						.showMessageDialog(
								null,
								Messages.getString("VennMaker.Empty_Name"), Messages //$NON-NLS-1$
										.getString("VennMaker.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
		}

		if (attr != null)
		{
			attr.setPredefinedValues(new Object[0]);
			EditIndividualAttributeTypeDialog ed = new EditIndividualAttributeTypeDialog();

			ed.showDialog(attr);

			if (!ed.wasCanceled())
			{
				ConfigDialogTempCache.getInstance().addSetting(
						new SettingAddAttributeType(attr));
			}
			else
				attr = null;
		}
	}

	/**
	 * refresh the getType of the current NewAttributeListener (e.g. to change
	 * from "ACTOR" to a relationtype)
	 * 
	 * @param newGetType
	 *           the new type
	 */
	public void setGetType(String newGetType)
	{
		this.getType = newGetType;
	}
}
