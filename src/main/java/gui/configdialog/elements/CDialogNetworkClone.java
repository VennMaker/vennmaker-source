/**
 * 
 */
package gui.configdialog.elements;

import gui.Messages;
import gui.VennMaker;
import gui.configdialog.ConfigDialog;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.save.SaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingCloneNetwork;
import gui.configdialog.settings.SettingNewNetwork;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import data.Netzwerk;

/**
 * Dialog zum Klonen und Hinzufuegen von Netzwerken.
 * 
 * 
 */
public class CDialogNetworkClone extends ConfigDialogElement implements
		ActionListener
{
	private static final long	serialVersionUID	= 1L;

	private JComboBox				networks;

	private Vector<Netzwerk>	tempNetworks;

	@Override
	public void buildPanel()
	{
		JButton cloneButton = new JButton(Messages.getString("ConfigDialog.20")); //$NON-NLS-1$
		JButton newNetworkButton = new JButton(
				Messages.getString("ConfigDialog.21")); //$NON-NLS-1$
		cloneButton.setActionCommand("clone"); //$NON-NLS-1$
		cloneButton.addActionListener(this);
		newNetworkButton.addActionListener(this);
		newNetworkButton.setActionCommand("new"); //$NON-NLS-1$
		GridBagLayout layout = new GridBagLayout();
		dialogPanel = new JPanel(layout);
		networks = new JComboBox();
		for (Netzwerk net : VennMaker.getInstance().getProject().getNetzwerke())
		{
			networks.addItem(net);
		}
		if (tempNetworks != null)
		{
			for (Netzwerk net : tempNetworks)
			{
				networks.addItem(net);
			}
		}

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		gbc.insets = new Insets(10, 10, 0, 10);
		layout.setConstraints(newNetworkButton, gbc);
		dialogPanel.add(newNetworkButton);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		gbc.insets = new Insets(10, 0, 0, 10);
		layout.setConstraints(cloneButton, gbc);
		dialogPanel.add(cloneButton);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 3;
		gbc.insets = new Insets(10, 0, 0, 10);
		layout.setConstraints(networks, gbc);
		dialogPanel.add(networks);

		JLabel label = new JLabel();
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.insets = new Insets(10, 0, 0, 10);
		gbc.weighty = 1;
		gbc.weightx = 1;
		layout.setConstraints(label, gbc);
		dialogPanel.add(label);
	}

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		String name = JOptionPane.showInputDialog(Messages
				.getString("ConfigDialog.30"));
		if (name == null || name.equals(""))
			return;
		ConfigDialogSetting s = null;
		if (evt.getActionCommand().equals("clone")) //$NON-NLS-1$
		{
			s = new SettingCloneNetwork((Netzwerk) networks.getSelectedItem(),
					name);

		}
		else
		{
			s = new SettingNewNetwork(name);
		}
		ConfigDialogTempCache.getInstance().addSetting(s);
		ConfigDialog.getInstance().updateTree();
	}

	@Override
	public ImageIcon getIcon()
	{
		return new ImageIcon("icons/intern/PlusIcon.png"); //$NON-NLS-1$
	}

	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		return null;
	}

	/**
	 * clones an object through byte serialization
	 */
	public static Object deepCopy(Object inf)
	{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		Object obj = null;
		try
		{
			new ObjectOutputStream(bout).writeObject(inf);
			ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			obj = new ObjectInputStream(bin).readObject();
		} catch (Exception exn)
		{
			exn.printStackTrace();
		}
		return obj;
	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public SaveElement getSaveElement()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
