package gui;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import data.OpenFileInBackground;
import files.FileOperations;
import files.ListFile;
import files.VMPaths;

/**
 * An object of this class represents a file chooser to open a VennMaker project file (*.vmp) or an old 
 * venn file (*.venn)
 * 
 *
 */
public class OpenFileDialog
{
	private JFileChooser	chooser;

	private String			fileToOpen;

	private String			rootFolder;

	private String			filter;

	private String			lastVisitedDirectory;

	private boolean		vennFile;

	private File			vmpFile;

	private boolean		canceled;
	
	/**
	 * Constrcuts a new Object of this class
	 * @param lastVisitedDirectory the last visited directory by the user
	 */
	public OpenFileDialog(String lastVisitedDirectory)
	{
		this.lastVisitedDirectory = lastVisitedDirectory;
	}
	
	/**
	 * Constcuts a new Object of this class
	 */
	public OpenFileDialog()
	{
		
	}
	
	/**
	 * Shows the dialog to the user
	 */
	public void show()
	{
		if (lastVisitedDirectory == null)
			chooser = new JFileChooser(System.getProperty("user.home")); //$NON-NLS-1$
		else
			chooser = new JFileChooser(lastVisitedDirectory);

		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setAcceptAllFileFilterUsed(false);
		
		chooser.addChoosableFileFilter(new FileFilter()
		{
			private String	vmp	= ".vmp";	//$NON-NLS-1$

			public boolean accept(File f)
			{
				if (f != null)
					if (f.toString() != null
							&& f.toString().toLowerCase().endsWith(vmp)
							|| f.isDirectory())
						return true;
				
				return false;
			}

			public String getDescription()
			{
				return vmp;
			}
		});

		chooser.addChoosableFileFilter(new FileFilter()
		{
			private String	venn	= ".venn";	//$NON-NLS-1$

			public boolean accept(File f)
			{
				if (f != null)
					if (f.toString() != null
							&& f.toString().toLowerCase().endsWith(venn)
							|| f.isDirectory())
						return true;
				
				return false;
			}

			public String getDescription()
			{
				return venn;
			}
		});


		if (filter != null)
		{
			chooser.addChoosableFileFilter(new FileFilter()
			{
				public boolean accept(File f)
				{
					if (f != null)
					{
						if (f.getName().toLowerCase().endsWith(filter)
								|| f.isDirectory())
						{
							return true;
						}
					}
					return false;
				}

				public String getDescription()
				{
					return filter;
				}
			});
		}

		if (chooser.showOpenDialog(VennMaker.getInstance()) == JFileChooser.APPROVE_OPTION)
		{
			File choosenFile = chooser.getSelectedFile();
			if (choosenFile != null)
			{
				this.lastVisitedDirectory = choosenFile.getParent();
			}
			else
			{
				JOptionPane.showMessageDialog(VennMaker.getInstance(),
						Messages.getString("OpenFileDialog.ChooseFileError")); //$NON-NLS-1$
				return;
			}

			if ((choosenFile.getName().endsWith(".venn")) || choosenFile.getName().endsWith(".vennEn"))//$NON-NLS-1$
			{
				if (!FileOperations.checkSubfolders(choosenFile.getParent()))
				{
					/**
					 * venn file without project specific folders (e.g audio,
					 * export...)
					 */

					this.vennFile = true;
					File tmp = new File(VMPaths.VENNMAKER_TEMPDIR
							+ "projects" + VMPaths.SEPARATOR + "temp"); //$NON-NLS-1$//$NON-NLS-2$
					FileOperations.deleteFolder(tmp);

					tmp.mkdir();
					FileOperations.createSubfolders(tmp.getAbsolutePath());

					File newVennFile = new File(
							VMPaths.VENNMAKER_TEMPDIR
									+ "projects" + VMPaths.SEPARATOR + "temp" + VMPaths.SEPARATOR + choosenFile.getName()); //$NON-NLS-1$ //$NON-NLS-2$

					try
					{
						FileOperations.copyFile(choosenFile, newVennFile, 4096, true);
					} catch (IOException exn)
					{
						ErrorCenter
								.manageException(
										exn,
										"File not copied successfully", ErrorCenter.ERROR, false, true); //$NON-NLS-1$
					}

					rootFolder = VMPaths.VENNMAKER_TEMPDIR
							+ "projects" + VMPaths.SEPARATOR + "temp"; //$NON-NLS-1$ //$NON-NLS-2$
					fileToOpen = newVennFile.getAbsolutePath();
				}
				else
				{
					rootFolder = choosenFile.getParent();
					fileToOpen = choosenFile.getAbsolutePath();
				}
			}
			else if (choosenFile.getName().endsWith(".vmp")) //$NON-NLS-1$
			{
				/**
				 * If the selected file is a *.vmp file it will be extracted to the
				 * temp folder of the operating system
				 * 
				 * rootFolder will change to e.g /tmp/VennMaker/Project/Projectname/
				 */
				vmpFile = choosenFile;
				String[] name = choosenFile.getName().split(".vmp"); //$NON-NLS-1$

				rootFolder = VMPaths.VENNMAKER_TEMPDIR
						+ "projects" + VMPaths.SEPARATOR + name[0]; //$NON-NLS-1$

				File tmp = new File(rootFolder);

				if (tmp.exists())
				{
					tmp.delete();
					tmp.mkdir();
				}
				else
					tmp.mkdir();
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

				Vector<String> files = FileOperations.getFolderContents(rootFolder,
						".venn"); //$NON-NLS-1$

				Vector<ListFile> filesToList = new Vector<ListFile>();

				for (String str : files)
				{
					filesToList.add(new ListFile(this.rootFolder
							+ VMPaths.SEPARATOR + str));
				}

				SelectInterviewDialog interviewDialog = new SelectInterviewDialog(
						filesToList, vmpFile.getAbsolutePath());

				File file = interviewDialog.getFileToOpen();

				if (file != null)
					fileToOpen = file.getAbsolutePath();
			}
			else
			{
				JOptionPane.showMessageDialog(chooser, "Filetype not supported!", "Could not load project!", JOptionPane.ERROR_MESSAGE);
			}
		}
		else
		{
			canceled = true;
		}
	}
	
	/**
	 * Sets the file filter for this dialog
	 * @param filter the file filter for this dialog
	 */
	public void setFilter(String filter)
	{
		this.filter = filter;
	}
	
	/**
	 * Return the name of the venn file to open from the vmp file
	 * @return  the name of the venn file to open from the vmp file
	 */
	public String getFilename()
	{
		return this.fileToOpen;
	}
	
	/**
	 * Returns the folder in which the vmp file is located
	 * @return the folder in which the vmp file is located
	 */
	public String getRootFolder()
	{
		return this.rootFolder;
	}
	
	/**
	 * Returns <code>true</code> if the dialog was canceld
	 * @return <code>true</code> if the dialog was canceld
	 */
	public boolean wasCanceled()
	{
		return this.canceled;
	}
	
	/**
	 * Return <code>true</code> if the file to open is a old venn File
	 * @return  <code>true</code> if the file to open is a old venn File
	 */
	public boolean isVennFile()
	{
		return this.vennFile;
	}
	
	/**
	 * Returns the vmp file to open. <code>null</code> if the file to open is a old
	 * venn file
	 * @return the vmp file to open. <code>null</code> if the file to open is a old
	 * venn file
	 */
	public File getVmpFile()
	{
		return this.vmpFile;
	}
	
	/**
	 * Returns the last visited directory by the user
	 * @return the last visited directory by the user
	 */
	public String getLastVisitedDirectory()
	{
		return this.lastVisitedDirectory;
	}
}
