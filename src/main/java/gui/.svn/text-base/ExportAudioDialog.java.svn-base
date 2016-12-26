/**
 * 
 */
package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import files.FileOperations;
import files.VMPaths;

/**
 * Dialog der beim Audio-Export genutzt wird.
 * 
 * 
 * 
 */
public class ExportAudioDialog extends JDialog
{
	private static final long	serialVersionUID	= 1L;

	private final JTextField	tfPath;

	public ExportAudioDialog()
	{
		super(VennMaker.getInstance(), Messages
				.getString("AudioExportDialog.Title"), true);
		setIconImage(new ImageIcon(FileOperations.getAbsolutePath(Messages
				.getString("VennMaker.Icon_Kommentar"))).getImage()); //$NON-NLS-1$

		JButton btnExport = new JButton(
				Messages.getString("VennMaker.MenuFileExport"));
		JButton btnCancel = new JButton(Messages.getString("ImportData.4"));
		JButton btnChoosePath = new JButton(
				Messages.getString("VennMaker.Choose_Path"));
		tfPath = new JTextField();
		final JDialog c = this;

		btnChoosePath.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser(tfPath.getText());
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				chooser.setDialogTitle(Messages
						.getString("AudioExportDialog.FileChooser_Title"));
				int result = chooser.showDialog(c,
						Messages.getString("AudioExportDialog.FileChooser_Button"));

				if ((chooser.getSelectedFile() != null)
						&& (result == JFileChooser.APPROVE_OPTION))
				{
					tfPath.setText(chooser.getSelectedFile().getAbsolutePath());
				}
			}
		});

		btnCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				c.dispose();
			}
		});

		btnExport.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final File destDir = new File(tfPath.getText());

				// Treading
				class CopyAudioFilesThread extends Thread
				{
					private WaitDialog	d;

					private String			srcPath;

					public CopyAudioFilesThread()
					{
						d = new WaitDialog(c, false);
						if (!destDir.exists())
							destDir.mkdir();

						srcPath = VMPaths.getCurrentWorkingDirectory() + "/audio/";

						start();
						d.setVisible(true);
					}

					public void run()
					{
						try
						{
							FileOperations
									.copyFolder(new File(srcPath), destDir, 1024);
						} catch (IOException exn)
						{
							JOptionPane.showMessageDialog(c,
									Messages.getString("VennMaker.ErrorWriting"));
						} finally
						{
							d.dispose();
							c.dispose();
						}
					}
				}

				new CopyAudioFilesThread();
			}
		});

		tfPath.setPreferredSize(new Dimension(200, 22));
		String homePath = System.getProperty("user.home");
		homePath = homePath.replace("\\", "/");
		tfPath.setText(homePath);

		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc;

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(5, 5, 0, 0);
		this.add(new JLabel(Messages.getString("CsvExportDialog.5")), gbc);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(5, 0, 0, 10);
		this.add(tfPath, gbc);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(5, 0, 0, 5);
		this.add(btnChoosePath, gbc);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(10, 5, 5, 0);
		this.add(btnExport, gbc);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(10, 0, 5, 5);
		this.add(btnCancel, gbc);

		this.pack();

		VennMaker v = VennMaker.getInstance();
		int xOff = (v.getWidth() - this.getWidth()) / 2;
		int yOff = (v.getHeight() - this.getHeight()) / 2;
		this.setLocation(v.getX() + xOff, v.getY() + yOff);

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
	}
}
