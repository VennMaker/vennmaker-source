/**
 * 
 */
package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import files.FileOperations;
import files.ListFile;
import files.VMPaths;

/**
 * 
 * 
 */
public class SelectInterviewDialog extends JDialog
{

	private static final long	serialVersionUID	= 1L;

	private Vector<ListFile>	files;

	private JList					list;

	private File					fileToOpen;

	public SelectInterviewDialog(Vector<ListFile> vennFiles,
			final String projectName)
	{
		super(VennMaker.getInstance(), Messages.getString("SelectInterviewDialog.Open"), true);
		this.files = vennFiles;
		this.setLayout(new GridBagLayout());
		this.setSize(385, 309);
		JTextField projectNameField = new JTextField();
		projectNameField.setEditable(false);

		list = new JList(files);

		GridBagConstraints gbc = new GridBagConstraints();

		JLabel contentLabel = new JLabel(
				Messages.getString("SelectInterviewDialog.Content")); //$NON-NLS-1$

		final JButton cancel = new JButton(
				Messages.getString("SelectInterviewDialog.Cancel")); //$NON-NLS-1$
		final JButton open = new JButton(
				Messages.getString("SelectInterviewDialog.Open")); //$NON-NLS-1$
		final JButton delete = new JButton(
				Messages.getString("SelectInterviewDialog.Delete")); //$NON-NLS-1$

		open.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(list.getSelectedIndex() == -1 && list.getModel().getSize() == 1)
				{
					fileToOpen = (ListFile) files.get(0);
					dispose();
				}
				else if(list.getSelectedIndex() == -1)
				{
					JOptionPane.showMessageDialog(getDialog(), Messages.getString("SelectInterviewDialog.SelectAFileToOpen")); //$NON-NLS-1$
				}
				else
				{
					fileToOpen = (ListFile) (files.get(list.getSelectedIndex()));
					dispose();
				}
				
			}

		});

		cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});

		delete.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{

				ListFile fileToDelete = files.get(list.getSelectedIndex());

				int returnValue = JOptionPane.showConfirmDialog(
						VennMaker.getInstance(),
						Messages.getString("OpenFileDialog.DeleteFileQuestion") +" "+ fileToDelete + "? "+Messages.getString("OpenFileDialog.DeleteFileQuestion2"), Messages.getString("OpenFileDialog.DeleteFileTitle"), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

				if (returnValue != JOptionPane.YES_OPTION)
					return;

				String tmpFolder = fileToDelete.getParent();
				File vennFileToDelete = new File(tmpFolder + VMPaths.SEPARATOR
						+ fileToDelete);

				if (vennFileToDelete.getAbsolutePath().equals(
						VennMaker.getInstance().getProject().getProjectFileName()))
				{
					JOptionPane.showMessageDialog(getDialog(),
							Messages.getString("SelectInterviewDialog.FileInUse")); //$NON-NLS-1$
					return;
				}

				vennFileToDelete.delete();

				String vennEnFile = fileToDelete.getName().substring(0,
						fileToDelete.getName().length() - 4)
						+ "vennEn"; //$NON-NLS-1$

				File vennEn = new File(tmpFolder + VMPaths.SEPARATOR + vennEnFile);
				vennEn.delete();

				files.remove(fileToDelete);
				list.setListData(files);

				if (projectName != null)
				{
					File destination = new File(projectName);

					FileOperations.zip(new File(tmpFolder), destination);
				}
			}
		});
		
		delete.setEnabled(false);
		
		list.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if(list.getSelectedIndex() > -1)
					delete.setEnabled(true);
			}
		});
		
		JScrollPane pane = new JScrollPane(list);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(1, 10, 1, 1);
		this.add(contentLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 10;
		gbc.gridheight = 10;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(10, 10, 10, 10);
		this.add(pane, gbc);

		gbc.gridx = 8;
		gbc.gridy = 17;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		this.add(cancel, gbc);

		gbc.gridx = 9;
		gbc.gridy = 17;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		this.add(open, gbc);

		gbc.gridx = 0;
		gbc.gridy = 17;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		this.add(delete, gbc);

		this.setSize(385, 309);

		VennMaker v = VennMaker.getInstance();
		int xOff = (v.getWidth() - this.getWidth()) / 2;
		int yOff = (v.getHeight() - this.getHeight()) / 2;

		/*
		 * if VennMakerWindow isn't yet created, or VennMakerWindow is too far off
		 * screen, correct xOff & yOff
		 */
		xOff = (((xOff > 0) || (xOff + this.getWidth() > Toolkit
				.getDefaultToolkit().getScreenSize().width)) ? xOff : Toolkit
				.getDefaultToolkit().getScreenSize().width
				/ 2
				- this.getWidth()
				/ 2);
		yOff = (((yOff > 0) || (yOff + this.getHeight() > Toolkit
				.getDefaultToolkit().getScreenSize().height)) ? yOff : Toolkit
				.getDefaultToolkit().getScreenSize().height
				/ 2
				- this.getHeight()
				/ 2);

		this.setLocation(v.getX() + xOff, v.getY() + yOff);

		this.setVisible(true);
	}

	public File getFileToOpen()
	{
		return fileToOpen;
	}

	private JDialog getDialog()
	{
		return this;
	}
}

