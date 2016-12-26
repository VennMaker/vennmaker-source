package files;

import gui.Messages;
import gui.SaveFileDialog;
import gui.VennMaker;
import gui.configdialog.ConfigDialog;

import java.io.File;
import java.util.concurrent.Semaphore;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import data.OpenFileInBackground;
import data.TemplateBackgroundOperations;
import data.TemplateBackgroundOperations.TemplateOperation;
import data.VennMakerActions;

/**
 * Input/Output class to collect save/load routines in one classfile whenever
 * input or output is needed, it should be here
 * 
 * 
 * 
 */
public class IO
{
	public static final String	TEMPLATE_SUFFIX	= ".vmt";	//$NON-NLS-1$


	/** Constant indicating an operation (save/load) succeeded */
	public static final int							OPERATION_SUCCEEDED		= 1;
	/** Constant indicating an operation (save/load) was canceled by user */
	public static final int							OPERATION_CANCELED		= 0;
	/** Constant indicating an operation (save/load) failed */
	public static final int							OPERATION_FAILED			= -1;

	/**
	 * saves the current network, when filename and vmp file are given
	 */
	public static int save()
	{
		if (VMPaths.getLastFileName() != null
				&& VMPaths.getVmpFile() != null)
		{
			if (VennMaker
					.getInstance()
					.getProject()
					.save(VMPaths.getLastFileName(),
							VMPaths.getVmpFile(), true) == false)
			{

				JOptionPane.showMessageDialog(VennMaker.getInstance(),
						Messages.getString("VennMaker.ErrorWriting") //$NON-NLS-1$
								+ VMPaths.getLastFileName(),
						Messages.getString("VennMaker.Error"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				VennMaker
						.getInstance()
						.getConfig()
						.save(VMPaths.getLastFileName()
								.substring(
										0,VMPaths.getLastFileName()
												.indexOf(".venn")) //$NON-NLS-1$
								+ ".vennEn"); //$NON-NLS-1$
				VennMaker.getInstance().setChangesSaved(true);

				if (VMPaths.getVmpFile() != null)
				{
					String[] projectName = VMPaths.getVmpFile()
							.getName().split(".vmp"); //$NON-NLS-1$
					String projectFolder = VMPaths.VENNMAKER_TEMPDIR
							+ "projects" + VMPaths.SEPARATOR + projectName[0] + VMPaths.SEPARATOR; //$NON-NLS-1$

					/**
					 * If the tmpDir is the current working directory, the a new vmp
					 * file will be created
					 */
					if (VMPaths.getCurrentWorkingDirectory()
							.equals(projectFolder))
					{
						OpenFileInBackground openfileinbackground = new OpenFileInBackground(new File(projectFolder), VMPaths.getVmpFile(), new Semaphore(1),
								VennMaker.getInstance(), OpenFileInBackground.ZIP);
						openfileinbackground.startAction();
					}

				}

			}
		}
		else
		{
			return saveRoutine();
		}
		return OPERATION_SUCCEEDED;
	}

	/**
	 * Method for loading a template
	 * 
	 * @param withEvents
	 *           <code>true</code> if <code>LoadTemplateEvent</code> should be
	 *           fired (used for loading a template an immediately starting the
	 *           <code>InterviewController</code>
	 * @return 
	 * <code>IO.OPERATION_SUCESSFULL</code> if operation was sucessfull <br/>
	 * <code>IO.OPERATION_FAILED</code> if operation failed <br/>
	 * <code>IO.OPERATION_CANCELED</code> if operation was canceled </br>
	 */
	public static int loadTemplate(boolean withEvents)
	{
		/**
		 * Control if unsaved changes
		 * yes --> ask user to save (if save routine is canceled, cancel load of template too)
		 * no --> go on
		 */
//		if(!VennMaker.getInstance().isChangesSaved())
//		{
//			int ret_Confirm = JOptionPane
//					.showConfirmDialog(
//							VennMaker.getInstance(),
//							Messages.getString("VennMaker.SaveChanges"), //$NON-NLS-1$
//							Messages.getString("VennMaker.SaveChangesTitel"), JOptionPane.YES_NO_CANCEL_OPTION); //$NON-NLS-1$;
//
//			// if user canceled --> return
//			if( ret_Confirm == JOptionPane.CANCEL_OPTION )
//				return OPERATION_CANCELED;
//			// if user pressed okay --> save
//			else if (ret_Confirm == JOptionPane.YES_OPTION)
//			{
//				int ret_saveRoutine = IO.saveRoutine();
//				// if save was canceled --> return
//				if (ret_saveRoutine == OPERATION_CANCELED)
//					return OPERATION_CANCELED;
//				// if save failed --> error msg and return
//				else if(ret_saveRoutine == OPERATION_FAILED)
//				{
//					JOptionPane.showMessageDialog(VennMaker.getInstance(),
//							Messages.getString("VennMaker.ErrorWriting") //$NON-NLS-1$
//									+ VMPaths.getLastFileName(),
//							Messages.getString("VennMaker.Error"), //$NON-NLS-1$
//							JOptionPane.ERROR_MESSAGE);
//					return OPERATION_FAILED;
//				}
//			}
//		}
		

		JFileChooser chooser = new JFileChooser(new File(ConfigDialog
				.getInstance().getLastTemplateLocation() != null ? ConfigDialog
				.getInstance().getLastTemplateLocation()
				: System.getProperty("user.home"))); //$NON-NLS-1$

		chooser.setFileFilter(new FileFilter()
		{
			@Override
			public boolean accept(File file)
			{
				if (file == null || file.toString() == null)
					return false;

				if (file.toString().endsWith(TEMPLATE_SUFFIX) || file.isDirectory())
					return true;

				return false;
			}

			@Override
			public String getDescription()
			{
				return ".vmt - VennMaker Template"; //$NON-NLS-1$
			}

		});

		if (!(chooser.showOpenDialog(ConfigDialog.getInstance()) == JFileChooser.APPROVE_OPTION))
			return OPERATION_FAILED;
		
		
		if(VennMakerActions.newProjekt() == IO.OPERATION_CANCELED)
			return OPERATION_FAILED;
		
		
		ConfigDialog.getInstance().setLastTemplateLocation(
				chooser.getSelectedFile().getParentFile().getAbsolutePath());
		
		
		TemplateBackgroundOperations template = new TemplateBackgroundOperations(chooser.getSelectedFile(),
				withEvents ? VennMaker.getInstance() : ConfigDialog.getInstance(),
				withEvents, TemplateOperation.LOAD);
		
		template.startAction();

		return OPERATION_SUCCEEDED;
	}
	
	/**
	 * Method for saving a template
	 * @return <code>IO.OPERATION_SUCESSFULL if operation was sucessfull or <code>IO.OPERATION_FAILED</code> if operation was not sucessfull
	 */
	public static int saveTemplate()
	{
		JFileChooser chooser = new JFileChooser(new File(ConfigDialog
				.getInstance().getLastTemplateLocation() != null ? ConfigDialog
				.getInstance().getLastTemplateLocation()
				: System.getProperty("user.home"))); //$NON-NLS-1$

		chooser.setFileFilter(new FileFilter()
		{
			@Override
			public boolean accept(File file)
			{
				if (file == null || file.toString() == null)
					return false;

				if (file.toString().endsWith(IO.TEMPLATE_SUFFIX)
						|| file.isDirectory())
					return true;

				return false;
			}

			@Override
			public String getDescription()
			{
				return ".vmt - VennMaker Template"; //$NON-NLS-1$
			}

		});

		boolean exists = true;

		while (exists)
		{
			if (!(chooser.showSaveDialog(ConfigDialog.getInstance()) == JFileChooser.APPROVE_OPTION)
					|| chooser.getSelectedFile() == null)
				return OPERATION_FAILED;
			
			ConfigDialog.getInstance().setLastTemplateLocation(
					chooser.getSelectedFile().getParentFile().getAbsolutePath());
			
			File selectedFile = chooser.getSelectedFile();

			if (!selectedFile.toString().endsWith(IO.TEMPLATE_SUFFIX))
			{
				selectedFile = new File(selectedFile.toString()
						+ IO.TEMPLATE_SUFFIX);
				chooser.setSelectedFile(selectedFile);
			}

			if (!chooser.getSelectedFile().exists()
					|| JOptionPane
							.showConfirmDialog(
									ConfigDialog.getInstance(),
									Messages.getString("ConfigSaveListener.Exists"), //$NON-NLS-1$
									Messages
											.getString("ConfigSaveListener.Exists-Title"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) //$NON-NLS-1$
				exists = false;
		}

		TemplateBackgroundOperations template = new TemplateBackgroundOperations(chooser.getSelectedFile(),
				ConfigDialog.getInstance(), false, TemplateOperation.SAVE);

		template.startAction();
		
		return OPERATION_SUCCEEDED;
	}

	/**
	 * Fasst den Speicherungsablauf zusammen
	 * 
	 * @return 
	 * <bold>IO.SAVED</bold> if successfully saved <br/>
	 * <bold>IO.SAVE_FAILED</bold> if error while saving <br/>
	 * <bold>IO.CANCELED</bold> if save routine was canceled by user
	 */
	public static int saveRoutine()
	{
		String rootDir = null;

		if (VMPaths.getLastVisitedDirectory() != null)
			rootDir = VMPaths.getLastVisitedDirectory();
		else
			rootDir = System.getProperty("user.home"); //$NON-NLS-1$

		if (new File(rootDir).equals(new File("./projects/temp"))) //$NON-NLS-1$
			rootDir = "./projects/"; //$NON-NLS-1$

		File vmpFile = VMPaths.getVmpFile();
		SaveFileDialog chooser = new SaveFileDialog(rootDir);

		chooser.setFilter(Messages.getString("VennMaker.Suffix")); //$NON-NLS-1$
		chooser.setVmpFile(vmpFile);
		chooser.showDialog();

		String filename = chooser.getFilename();

		if (filename != null && !chooser.wasCanceled())
		{
			filename = filename.replace("\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$

			if (!filename.endsWith(Messages.getString("VennMaker.Suffix")))//$NON-NLS-1$
				filename += Messages.getString("VennMaker.Suffix"); //$NON-NLS-1$

			File file = new File(filename);

			FileOperations.changeRootFolder(chooser.getRootFolder());

			// von WasVmp h�ngt ab ob die Bilder, Audio-Files usw kopiert
			// werden...
			// workaround ... bleibt erstmal false, damit immer kopiert wird.
			boolean wasVmp = false;
			
			File choosenVmpFile = new File(chooser.getVmpPath());
			boolean success = VennMaker.getInstance().getProject().save(filename, choosenVmpFile, wasVmp);
			if (!success)
				JOptionPane
						.showMessageDialog(
								VennMaker.getInstance(),
								Messages.getString("VennMaker.ErrorWriting") //$NON-NLS-1$
										+ filename,
								Messages.getString("VennMaker.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			else
			{
				VMPaths.setCurrentWorkingDirectory(chooser.getRootFolder());
				VMPaths.setLastFileName(filename);
				VMPaths.setVmpFile(choosenVmpFile);
				VennMaker.getInstance().getConfig().save(VMPaths.getLastFileName()
						.substring(
								0,
								VMPaths.getLastFileName()
										.indexOf(".venn")) //$NON-NLS-1$
						+ ".vennEn"); //$NON-NLS-1$

				VennMaker.getInstance().setChangesSaved(true);

				File destination = new File(chooser.getDestinationFolder());
				File source = new File(chooser.getRootFolder());

				// FileOperations.zip(source, destination);
				Semaphore sem = new Semaphore(1);
				OpenFileInBackground openfileinbackground = new OpenFileInBackground(source, destination, sem, VennMaker.getInstance(),
						OpenFileInBackground.ZIP);

				openfileinbackground.startAction();
				
				VMPaths.setLastVisitedDirectory(destination.getAbsolutePath());
				
				VennMaker
						.getInstance()
						.setTitle(
								Messages.getString("VennMaker.VennMaker") + VennMaker.VERSION//$NON-NLS-1$
										+ " [" + destination.getAbsolutePath() + VMPaths.SEPARATOR + source.getName() + ".vmp" + " - " + file.getName() + " ]");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				
				VennMaker.getInstance().refresh();
				return OPERATION_SUCCEEDED;
			}

		}
		else if (chooser.wasCanceled())
		{
			return OPERATION_CANCELED;
		}

		return OPERATION_FAILED;
	}
}
