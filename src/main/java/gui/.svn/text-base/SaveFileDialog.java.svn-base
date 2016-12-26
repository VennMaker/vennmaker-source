package gui;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import data.OpenFileInBackground;
import data.Projekt;
import files.FileOperations;
import files.VMPaths;

public class SaveFileDialog extends JDialog implements WindowListener
{
	/**
	 * 
	 */
	private static final long		serialVersionUID	= 1L;

	private String					filter;

	private String					rootFolder;

	private String					fileToSave;

	private String					destFolder			= ""; //$NON-NLS-1$

	private JFileChooser			chooser;

	private JFileChooser			oldChooser;

	private boolean					canceled;

	private File					vmpFile;

	private Vector<String>			files;

	private JTextField				filenameField;

	private JTextField				selectedFileField;

	private JList					fileList;

	/**
	 * legt das Wurzelverzeichnis fest
	 * 
	 * @param root
	 * 
	 */
	public SaveFileDialog(String root)
	{
		super(VennMaker.getInstance(), Messages
				.getString("SaveFileDialog.SaveFile"), true); //$NON-NLS-1$
		rootFolder = root.replace("\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$

		Container container = this.getContentPane();

		filenameField = new JTextField();
		filenameField.setEditable(false);

		selectedFileField = new JTextField();

		String actualFileName = Projekt.getProjectFileName();

		File actualFile = new File(actualFileName);

		selectedFileField.setText(actualFile.getName());
		
		JLabel contentLabel = new JLabel(
				Messages.getString("SaveFileDialog.ProjectConent")); //$NON-NLS-1$

		JButton selectFileButton = new JButton(
				Messages.getString("SaveFileDialog.SelectFile")); //$NON-NLS-1$
		fileList = new JList();

		fileList.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				selectedFileField.setText((String) fileList.getSelectedValue());
			}
		});

		final JButton delete = new JButton(
				Messages.getString("SaveFileDialog.DeleteFile")); //$NON-NLS-1$
		final JButton cancel = new JButton(Messages.getString("SaveFileDialog.Cancel")); //$NON-NLS-1$
		final JButton save = new JButton(Messages.getString("SaveFileDialog.Save")); //$NON-NLS-1$


		selectFileButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				showSaveFileDialog(Messages.getString("SaveFileDialog.Title")); //$NON-NLS-1$
			}
		});

		save.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (performSaving())
					dispose();
			}
		});
		
		save.setEnabled(false);

		cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
				canceled = true;
			}
		});
		
		selectedFileField.addCaretListener(new CaretListener()
		{
			@Override
			public void caretUpdate(CaretEvent e)
			{
				if(!selectedFileField.getText().equals("")) //$NON-NLS-1$
					save.setEnabled(true);
				else
					save.setEnabled(false);
			}
		});

		JScrollPane list = new JScrollPane(fileList);

		GridBagLayout gbl = new GridBagLayout();

		container.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints();
		
		JLabel numberLabel = new JLabel("1.");
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(10, 9, 10, 10);
		container.add(numberLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 8;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(10, 22, 10, 10);
		container.add(filenameField, gbc);

		gbc.gridx = 8;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(10, 10, 10, 10);
		container.add(selectFileButton, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(1, 10, 1, 1);
		container.add(contentLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 10;
		gbc.gridheight = 10;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(10, 10, 10, 10);
		container.add(list, gbc);

		gbc.gridx = 0;
		gbc.gridy = 16;
		gbc.gridwidth = 10;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(10, 10, 10, 10);
		container.add(selectedFileField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 17;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbl.setConstraints(delete, gbc);
		container.add(delete);
		
		delete.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{

				String fileToDelete = (String) fileList.getSelectedValue();

				if(fileToDelete != null)
				{
					int returnValue = JOptionPane.showConfirmDialog(
							VennMaker.getInstance(),
							Messages.getString("OpenFileDialog.DeleteFileQuestion") + fileToDelete + "? "+Messages.getString("OpenFileDialog.DeleteFileQuestion2"), Messages.getString("OpenFileDialog.DeleteFileTitle"), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	
					if (returnValue != JOptionPane.YES_OPTION)
						return;
	
					String folder = new File(filenameField.getText()).getName();
					String tmpFolder = VMPaths.VENNMAKER_TEMPDIR
							+ "projects" + VMPaths.SEPARATOR + folder.substring(0, folder.length() - 4); //$NON-NLS-1$
	
					File vennFileToDelete = new File(tmpFolder + VMPaths.SEPARATOR
							+ fileToDelete);
					vennFileToDelete.delete();
	
					String vennEnFile = fileToDelete.substring(0,
							fileToDelete.length() - 4) + "vennEn"; //$NON-NLS-1$
	
					File vennEn = new File(tmpFolder + VMPaths.SEPARATOR + vennEnFile);
					vennEn.delete();
	
					files.remove(fileToDelete);
					fileList.setListData(files);
	
					File destination = new File(filenameField.getText());
	
					FileOperations.zip(new File(tmpFolder), destination);
				}
			}
		});
		
		delete.setEnabled(false);
		
		fileList.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if(fileList.getSelectedIndex() > -1)
					delete.setEnabled(true);
			}
		});
		
		gbc.gridx = 8;
		gbc.gridy = 17;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		container.add(cancel, gbc);

		gbc.gridx = 9;
		gbc.gridy = 17;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		container.add(save, gbc);

		this.addWindowListener(this);
		// this.setLocation(p);
		this.setSize(385, 309);

		VennMaker v = VennMaker.getInstance();
		int xOff = (v.getWidth()-this.getWidth())/2;
		int yOff = (v.getHeight()-this.getHeight())/2;
		this.setLocation(v.getX() + xOff, v.getY() + yOff);

	}

	/**
	 * zeigt den Savefiledialog an
	 */
	public void showSaveFileDialog()
	{
		showSaveFileDialog(Messages.getString("SaveFileDialog.chooseFile")); //$NON-NLS-1$
	}

	private void showSaveFileDialog(String title)
	{

		// wenn keine datei gewaehlt wurde, erneut nachfragen
		boolean again = true;

		while (again)
		{
			again = false;
			chooser = new JFileChooser();
			chooser.setDialogTitle(title);

			if (new File(rootFolder).equals(new File("./projects/temp/"))) //$NON-NLS-1$
				chooser.setCurrentDirectory(new File("./projects/")); //$NON-NLS-1$
			else if (vmpFile == null)
				chooser.setCurrentDirectory(new File(rootFolder));
			else
				chooser.setCurrentDirectory(new File(vmpFile.getParent()));
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

			chooser.setAcceptAllFileFilterUsed(false);

			chooser.addChoosableFileFilter(new FileFilter()
			{
				private String	vmp	= ".vmp";	//$NON-NLS-1$

				public boolean accept(File f)
				{
					if (f != null && f.toString() != null)
					{
						if (f.toString().toLowerCase().endsWith(vmp)
								|| f.isDirectory())
							return true;
						if (f.toString().toLowerCase().endsWith(filter.toLowerCase())
								|| f.isDirectory())
							return true;
					}
					return false;
				}

				@Override
				public String getDescription()
				{
					return ".vmp"; //$NON-NLS-1$
				}

			});

			if (chooser.showSaveDialog(VennMaker.getInstance()) == JFileChooser.APPROVE_OPTION)
			{
				if (chooser.getSelectedFile() == null)// || !chooser.getSelectedFile().getName().endsWith(".vmp")) //$NON-NLS-1$
				{
					fileToSave = null;
					again = true;
				}
				else
				{

					File vmp = chooser.getSelectedFile();

					if (!vmp.getName().endsWith(".vmp")) //$NON-NLS-1$
						vmp = new File(vmp.getAbsolutePath() + ".vmp"); //$NON-NLS-1$

					try
					{
						if (!vmp.exists())
							vmp.createNewFile();
					} catch (IOException ioe)
					{
						ErrorCenter
								.manageException(
										ioe,
										Messages
												.getString("SaveFileDialog.FileCanNotCreated"), ErrorCenter.ERROR, false, true); //$NON-NLS-1$
					}

					filenameField.setText(vmp.getAbsolutePath());

					vmpFile = vmp;

					Semaphore sem = new Semaphore(1);
					OpenFileInBackground ofib = new OpenFileInBackground(vmpFile, null, sem,
							VennMaker.getInstance(), OpenFileInBackground.UNZIP);
					ofib.startAction();
					
					try
					{
						sem.acquire();
					} catch (InterruptedException exn)
					{
						// TODO Auto-generated catch block
						exn.printStackTrace();
					}

					if (this.vmpFile != null)
						files = FileOperations
								.getFolderContents(
										VMPaths.VENNMAKER_TEMPDIR
												+ "projects" + VMPaths.SEPARATOR + vmpFile.getName().substring(0, vmpFile.getName().length() - 4), ".venn"); //$NON-NLS-1$//$NON-NLS-2$

					else if (filenameField.getText().endsWith(".vmp")) //$NON-NLS-1$
					{
						File f = new File(filenameField.getText());

						files = FileOperations
								.getFolderContents(
										VMPaths.VENNMAKER_TEMPDIR
												+ "projects" + VMPaths.SEPARATOR + f.getName().substring(0, f.getName().length() - 4), ".venn"); //$NON-NLS-1$//$NON-NLS-2$
					}
					else
						files = FileOperations.getFolderContents(VMPaths.getCurrentWorkingDirectory(), ".venn"); //$NON-NLS-1$

					fileList.setListData(files);
				}
			}
		}
	}

	/**
	 * This method will be removed if the Interview-Configuration has been
	 * updated
	 * 
	 * @param title
	 * @return the filename
	 */
	public String showOldSaveDialog(String title)
	{
		// wenn keine datei gewaehlt wurde, erneut nachfragen
		boolean again = true;

		while (again)
		{
			again = false;
			oldChooser = new JFileChooser();
			oldChooser.setDialogTitle(title);
			if (new File(rootFolder).equals(new File("./projects/temp/"))) //$NON-NLS-1$
				oldChooser.setCurrentDirectory(new File("./projects/")); //$NON-NLS-1$
			else
				oldChooser.setCurrentDirectory(new File(rootFolder));
			oldChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

			oldChooser.addChoosableFileFilter(new FileFilter()
			{
				public boolean accept(File f)
				{
					if (f != null)
					{
						if (f.toString().toLowerCase().endsWith(filter.toLowerCase())
								|| f.isDirectory())
							return true;
					}
					return false;
				}

				@Override
				public String getDescription()
				{
					return filter;
				}

			});

			if (oldChooser.showSaveDialog(VennMaker.getInstance()) == JFileChooser.APPROVE_OPTION)
			{
				if (oldChooser.getSelectedFile() == null)
				{
					fileToSave = null;
				}
				else
				{
					rootFolder = oldChooser.getSelectedFile().getParent();
					if (!rootFolder.endsWith("/")) //$NON-NLS-1$
						rootFolder = rootFolder + "/"; //$NON-NLS-1$

					// wenn gewaehlter parentordner kein projektordner ist, fragen,
					// ob er erstellt werden soll
					if (!FileOperations.checkSubfolders(oldChooser.getSelectedFile()
							.getParent()))
					{
						Object[] options = {
								Messages.getString("ProjectChooser.create"), //$NON-NLS-1$
								Messages.getString("ProjectChooser.changeFolder"), //$NON-NLS-1$
								Messages.getString("ProjectChooser.cancel") }; //$NON-NLS-1$
						Object selected = JOptionPane
								.showOptionDialog(
										VennMaker.getInstance(),
										Messages
												.getString("ProjectChooser.createProjectFolder"), //$NON-NLS-1$
										Messages
												.getString("ProjectChooser.createProjectFolderTitle"), //$NON-NLS-1$
										JOptionPane.DEFAULT_OPTION,
										JOptionPane.INFORMATION_MESSAGE, null, options,
										options[0]);

						if (selected.equals(0))
						{
							/* Nach Namen fragen und erstellen */
							String ordnerName = JOptionPane
									.showInputDialog(VennMaker.getInstance(), Messages
											.getString("ProjectChooser.enterFolderName")); //$NON-NLS-1$
							if (!ordnerName.isEmpty())
							{
								ordnerName = ordnerName.replace(":", "_"); //$NON-NLS-1$//$NON-NLS-2$
								rootFolder = rootFolder + ordnerName + "/"; //$NON-NLS-1$
								if (!(new File(rootFolder).exists()))
								{
									new File(rootFolder).mkdir();
									FileOperations.createSubfolders(rootFolder);
									fileToSave = rootFolder
											+ oldChooser.getSelectedFile().getName();
								}
							}
							// wenn kein Ordner angegeben wird, dann neu starten
							else
							{
								again = true;
								fileToSave = null;
							}
						}
						// anderen ordner waehlen - heisst: dialog nochmal starten
						else if (selected.equals(1))
						{
							again = true;
							fileToSave = null;
						}
						else
						{
							// sonst alles auf null
							fileToSave = null;
							canceled = true;
							return null;
						}
					}
					else
						fileToSave = oldChooser.getSelectedFile().getAbsolutePath();
				}
			}
			else
			{
				canceled = true;
			}
		}

		return fileToSave;
	}

	public void showDialog()
	{
		canceled = false;
		this.setVisible(true);
	}

	/**
	 * This method prepares the venn and vmp file for saving. Savings will be
	 * done in the tmp folder of the operating system
	 * 
	 * @return true if preperation was sucessfull
	 */
	public boolean performSaving()
	{
		String vennName = selectedFileField.getText();

		if (vennName == null || vennName.equals("")) //$NON-NLS-1$
		{
			JOptionPane.showMessageDialog(this,
					Messages.getString("SaveFileDialog.SpecifyFilename")); //$NON-NLS-1$
			return false;
		}

		if (filenameField.getText() == null || filenameField.getText().equals("")) //$NON-NLS-1$
		{
			JOptionPane.showMessageDialog(this,
					Messages.getString("SaveFileDialog.ChooseProjectFile")); //$NON-NLS-1$
			return false;
		}

		if (!vennName.endsWith(".venn")) //$NON-NLS-1$
			vennName += ".venn"; //$NON-NLS-1$

		if ( files == null ) 
			files = new Vector<String>();
		
		if (files != null && files.contains(vennName))
		{
			int returnValue = JOptionPane
					.showConfirmDialog(
							this,
							Messages.getString("SaveFileDialog.Override") + " " + vennName + "?", Messages.getString("SaveFileDialog.Override"), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

			if (returnValue != JOptionPane.YES_OPTION)
				return false;

		}
		else if (!files.contains(vennName))
		{
			files.add(vennName);
			fileList.setListData(files);
		}

		if (vmpFile == null)
			destFolder = new File(filenameField.getText()).getParent();
		else
			destFolder = vmpFile.getParent();

		File projectFile = new File(filenameField.getText());
		String projectName = projectFile.getName().substring(0,
				projectFile.getName().length() - 4);

		rootFolder = VMPaths.VENNMAKER_TEMPDIR
				+ "projects" + VMPaths.SEPARATOR + projectName + VMPaths.SEPARATOR; //$NON-NLS-1$

		File f = new File(rootFolder);

		if (!f.exists())
		{
			f.mkdirs();
			FileOperations.createSubfolders(rootFolder);
		}

		fileToSave = rootFolder + vennName;
		return true;
	}

	/**
	 * Filter, um festzulegen, welche dateien angezeigt werden sollen
	 * 
	 * @param newFilter
	 *           legt den entsprechenden filter fest
	 */
	public void setFilter(String newFilter)
	{
		filter = newFilter;
	}

	/**
	 * legt fest, in welchem verzeichnis gestartet werden soll
	 * 
	 * @param newRootFolder
	 */
	public void setRootFolder(String newRootFolder)
	{
		rootFolder = newRootFolder.replace("\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * gibt das aktuelle wurzelverzeichnis an
	 * 
	 * @return the current root folder
	 */
	public String getRootFolder()
	{
		return rootFolder;
	}

	/**
	 * gibt die zu speichernde datei zurueck
	 * 
	 * @return file name
	 */
	public String getFilename()
	{
		return fileToSave;
	}

	/**
	 * legt die zu oeffnende datei fest
	 * 
	 * @param newFile
	 *           Pfadangabe zur datei
	 */
	public void setFilename(String newFile)
	{
		fileToSave = newFile;
	}
	
	/**
	 * legt den Namen des Projects fest
	 * 
	 */
	public void setProjectName(String name)
	{
		filenameField.setText(name);
	}
	
	/**
	 * legt den Namen des Interviews fest
	 * 
	 */
	public void setInterviewName(String name)
	{
		selectedFileField.setText(name);
	}
	

	
	public boolean wasCanceled()
	{
		return this.canceled;
	}

	public String getDestinationFolder()
	{
		return this.destFolder;
	}

	public void setVmpFile(File vmpFile)
	{
		if (vmpFile == null)
			return;
		this.vmpFile = vmpFile;
		updateFileList();
	}

	public File getVmpFile()
	{
		return this.vmpFile;
	}

	private void updateFileList()
	{
		files = FileOperations
				.getFolderContents(
						VMPaths.VENNMAKER_TEMPDIR
								+ "projects" + VMPaths.SEPARATOR + vmpFile.getName().substring(0, vmpFile.getName().length() - 4), ".venn"); //$NON-NLS-1$//$NON-NLS-2$
		fileList.setListData(files);

		filenameField.setText(vmpFile.getAbsolutePath());
	}

	public String getFilter()
	{
		return filter;
	}

	public String getVmpPath()
	{
		return filenameField.getText();
	}

	@Override
	public void windowActivated(WindowEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void windowClosed(WindowEvent e)
	{
		canceled = true;
	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		canceled = true;
	}

	@Override
	public void windowDeactivated(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void windowOpened(WindowEvent e)
	{
		// TODO Auto-generated method stub
	}

}
