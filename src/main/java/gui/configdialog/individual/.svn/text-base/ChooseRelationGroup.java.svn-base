package gui.configdialog.individual;

import gui.Messages;
import gui.VennMaker;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import data.AttributeType;

/**
 * dialog to choose a relationgroup out of the existing ones, or create a new
 * relationgroup, if wanted.
 * 
 * 
 * 
 */
public class ChooseRelationGroup extends JDialog
{
	private static final long	serialVersionUID	= 1L;

	/** stores all relationgroups and a possibility to create a new one */
	private JComboBox<String>	relationGroupList;

	/** button to close the dialog and save the new relationgroup */
	private JButton				okayBtn;

	/** button to cancel the dialog without decision */
	private JButton				cancelBtn;

	/** stores the different relationgroups */
	private Vector<String>		items;

	/** stores information about the button clicked */
	private int						clickedBtn			= JOptionPane.CANCEL_OPTION;

	/**
	 * creates and shows the dialog to select/create a different relationgroup
	 * 
	 * @param owner
	 *           the ownerframe for this dialog
	 */
	public ChooseRelationGroup(Frame owner)
	{
		super(owner, Messages.getString("ChooseImagePartDialog.Title"), true); //$NON-NLS-1$

		Vector<AttributeType> tempTypes = VennMaker.getInstance().getProject()
				.getAttributeTypes();

		items = new Vector<String>();
		for (AttributeType tempType : tempTypes)
		{
			if (!tempType.getType().equals("ACTOR") //$NON-NLS-1$
					&& (!items.contains(tempType.getType())))
			{
				items.add(tempType.getType());
			}
		}

		relationGroupList = new JComboBox<String>(items);
		relationGroupList.addItem(Messages.getString("ChooseRelationGroup.addRelationgroup")); //$NON-NLS-1$

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(relationGroupList, c);

		okayBtn = new JButton(Messages.getString("ChooseRelationGroup.OK")); //$NON-NLS-1$
		c.gridy = 1;
		c.gridwidth = 1;
		this.add(okayBtn, c);

		cancelBtn = new JButton(Messages.getString("ChooseRelationGroup.Cancel")); //$NON-NLS-1$
		c.gridx = 1;
		this.add(cancelBtn, c);

		okayBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				clickedBtn = JOptionPane.OK_OPTION;
				close();
			}
		});

		cancelBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				close();
			}
		});

		/* pack, center and show dialog */
		this.pack();
		this.setLocation(
				(int) ((getToolkit().getScreenSize().getWidth() - this.getWidth()) / 2),
				(int) ((getToolkit().getScreenSize().getHeight() - this.getHeight()) / 2));
		this.setVisible(true);

	}

	/**
	 * closes this dialog
	 */
	private void close()
	{
		this.setVisible(false);
	}

	/**
	 * returns the selected relationgroup if the user selected to create a new
	 * one, a dialog is presented to create one
	 * 
	 * @return returns the Stringvalue of the (new created) selected
	 *         Relationtypegroup
	 */
	public String getRelationGroup()
	{
		/*
		 * checks, if the last item is selected (create new relationgroup), if
		 * not, simply return the selected relationgroup
		 */
		if (relationGroupList.getSelectedIndex() == relationGroupList
				.getItemCount() - 1)
		{
			String newType = JOptionPane.showInputDialog(Messages
					.getString("CDialogEditRelationalAttributeTypes.3")); //$NON-NLS-1$
			while ((items.contains(newType) || newType.trim().equals("ACTOR")) //$NON-NLS-1$
					|| newType.equals("")) //$NON-NLS-1$
			{
				JOptionPane
						.showMessageDialog(
								relationGroupList,
								Messages
										.getString("CDialogEditRelationalAttributeTypes.4"), //$NON-NLS-1$
								Messages
										.getString("CDialogEditRelationalAttributeTypes.5"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
				newType = JOptionPane.showInputDialog(Messages
						.getString("CDialogEditRelationalAttributeTypes.3")); //$NON-NLS-1$
			}

			return newType;
		}
		return (String) relationGroupList.getSelectedItem();
	}

	/**
	 * Which button was clicked (okay/cancel/exit)
	 * 
	 * @return returns the integervalue of the clicked button
	 */
	public int getButton()
	{
		return clickedBtn;
	}
}
