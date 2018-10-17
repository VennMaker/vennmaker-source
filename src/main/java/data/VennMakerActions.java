package data;

import exception.VennMakerMapNotFoundException;
import files.FileOperations;
import files.IO;
import files.ListFile;
import files.VMPaths;
import files.SystemOperations;
import gui.AudiorecorderWindow;
import gui.ExportAudioDialog;
import gui.ExportDialog;
import gui.FilterDialog;
import gui.FilterDialog.MySearchListener;
import gui.Messages;
import gui.OpenFileDialog;
import gui.SelectInterviewDialog;
import gui.TestVersion;
import gui.VennMaker;
import gui.utilities.VennMakerUIConfig;
import interview.InterviewController;
import interview.InterviewLayer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * Wichtige VennMaker Funktionen gebuendelt in einer Klasse um auch Zugriff von
 * "aussen" zu gewaehrleisten.
 * 
 * 
 */
public class VennMakerActions
{
	/**
	 * VennMaker auf Initialzustand (neues Projekt, neue Config usw...) bringen.
	 */
	public static int newProjekt()
	{
		if (!VennMaker.getInstance().isChangesSaved())
		{
			int ret = JOptionPane.showConfirmDialog(
					VennMaker.getInstance(),
					Messages.getString("VennMaker.SaveChanges"), //$NON-NLS-1$
					Messages.getString("VennMaker.SaveChangesTitel"),
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (ret == JOptionPane.YES_OPTION)
			{
				if (IO.saveRoutine() == IO.OPERATION_CANCELED) // cancel
					return IO.OPERATION_CANCELED;
			}
			else if (ret == JOptionPane.CANCEL_OPTION)
				return IO.OPERATION_CANCELED;
		}

		return createNewProject();
	}

	/**
	 * Forces the creating of a new <code>Project</code> without asking the user
	 * to save the current <code>Project</code>
	 * 
	 * @return <code>IO.OPERATION_SUCCEEDED if the creating of the new <code>Project</code>
	 *         was successfull
	 */
	public static int forceNewProject()
	{
		return createNewProject();
	}

	/**
	 * Creates a new <code>Project</code>
	 * 
	 * @return <code>IO.OPERATION_SUCCEEDED if the creating of the new <code>Project</code>
	 *         was successfull
	 */
	private static int createNewProject()
	{
		EventProcessor.getInstance().resetEventListener();
		VennMaker.getInstance().setProject(new Projekt());
		VennMaker.getInstance().setConfig(new Config());
		VennMaker.getInstance().resetEventListenerIsSetFlag();
		try
		{
			VennMaker.getInstance().createViewersTabbedPane();
		} catch (VennMakerMapNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		VennMaker.getInstance().resetUndoRedoControls();
		VennMaker.getInstance().setChangesSaved(true);
		VMPaths.reset();
		InterviewLayer.getInstance().reset();

		// ConfigDialogLayer.getInstance().resetActiveElements();
		// ConfigDialogTempCache.getInstance().cancelAllSettings();
		// ConfigDialogTempCache.getInstance().clearActiveElementsList();

		VennMaker.getInstance().getContentPane()
				.remove(VennMaker.getInstance().getVisPanel());

		VennMaker
				.getInstance()
				.setTitle(
						Messages.getString("VennMaker.VennMaker") + VennMaker.getInstance().VERSION); //$NON-NLS-1$
		VennMaker.getInstance().refresh();

		return IO.OPERATION_SUCCEEDED;
	}

	/**
	 * Interview aus aktuellem Projekt laden
	 */
	public static void openCurrentProject()
	{
		boolean backToVennMaker = false;
		if (!VennMaker.getInstance().isChangesSaved())
			switch (JOptionPane.showConfirmDialog(null,
					Messages.getString("VennMaker.ConfirmOpen"), Messages //$NON-NLS-1$
							.getString("VennMaker.ConfirmOpenTitel"), //$NON-NLS-1$
					JOptionPane.YES_NO_CANCEL_OPTION))
			{
			// zurueck zu Vennmaker
				case JOptionPane.CANCEL_OPTION:
					backToVennMaker = true;
					break;

				// Neues Projekt ohne Speichern laden
				case JOptionPane.NO_OPTION:
					break;

				// Speicherdialog vorm Laden aufrufen
				default:
					// wenn speichern fehlschlaegt, ohne laden
					// zu
					// vennmaker
					// zurueckkehren.
					if (IO.save() == IO.OPERATION_FAILED) // SAVE_FAILED
						backToVennMaker = true;
			}
		if (!backToVennMaker)
		{
			File[] files = new File(VMPaths.getCurrentWorkingDirectory())
					.listFiles();

			Vector<ListFile> vennFiles = new Vector<ListFile>();

			if (files != null)
			for (File f : files)
			{
				if (f.getName().endsWith("venn")) //$NON-NLS-1$
					vennFiles.add(new ListFile(f.getAbsolutePath()));
			}

			String projectName = null;

			if (VMPaths.getVmpFile() != null)
			{
				projectName = VMPaths.getVmpFile().getAbsolutePath();
			}

			SelectInterviewDialog diag = new SelectInterviewDialog(vennFiles,
					projectName);

			File fileToOpen = diag.getFileToOpen();

			if (fileToOpen == null)
				return;

			VennMaker.getInstance().performLoadProject(fileToOpen);
		}
	}

	/**
	 * Interview aus einem anderen Projekt laden
	 */
	public static void openOtherProject()
	{
		OpenFileDialog chooser = new OpenFileDialog(
				VMPaths.getLastVisitedDirectory());

		chooser.show();

		if (!chooser.wasCanceled())
		{
			if (chooser.getFilename() != null)
			{
				boolean backToVennMaker = false;
				if (!VennMaker.getInstance().isChangesSaved())
					switch (JOptionPane.showConfirmDialog(null,
							Messages.getString("VennMaker.ConfirmOpen"), Messages //$NON-NLS-1$
									.getString("VennMaker.ConfirmOpenTitel"), //$NON-NLS-1$
							JOptionPane.YES_NO_CANCEL_OPTION))
					{
					// zurueck zu Vennmaker
						case JOptionPane.CANCEL_OPTION:
							backToVennMaker = true;
							break;

						// Neues Projekt ohne Speichern laden
						case JOptionPane.NO_OPTION:
							break;

						// Speicherdialog vorm Laden aufrufen
						default:
							// wenn speichern fehlschlaegt, ohne laden
							// zu
							// vennmaker
							// zurueckkehren.
							if (IO.save() == -1) // SAVE_FAILED
								backToVennMaker = true;
					}

				if (!backToVennMaker)
				{
					FileOperations.openVmpFile(chooser.getVmpFile(),
							chooser.getFilename(), chooser.getLastVisitedDirectory());
				}
			}
			else
				JOptionPane.showMessageDialog(VennMaker.getInstance(),
						"File failed to load");
		}
	}

	/**
	 * VennMaker mit Specherabfrage beenden
	 */
	public static void exitAndSave()
	{
		// Bei ungespeicherten Daten nachfragen, ob gespeichert werden
		// soll
		if (!VennMaker.getInstance().isChangesSaved())
			switch (JOptionPane.showConfirmDialog(null,
					Messages.getString("VennMaker.ConfirmQuit"), Messages //$NON-NLS-1$
							.getString("VennMaker.ConfirmQuitTitel"), //$NON-NLS-1$
					JOptionPane.YES_NO_CANCEL_OPTION))
			{

			// zurueck zu Vennmaker
				case JOptionPane.CANCEL_OPTION:
					break;

				case JOptionPane.CLOSED_OPTION:
					break;

				// VennMaker ohne Speichern verlassen
				case JOptionPane.NO_OPTION:
					VennMaker.getInstance().exitVennMaker();
					break;

				// Speicherdialog vorm Beenden aufrufen
				default:
					if (VMPaths.getLastFileName() != null)
					{
						if (VennMaker.getInstance().getProject()
								.save(VMPaths.getLastFileName()) == false)
							JOptionPane.showMessageDialog(VennMaker.getInstance(),
									Messages.getString("VennMaker.ErrorWriting") //$NON-NLS-1$
											+ VMPaths.getLastFileName(),
									Messages.getString("VennMaker.Error"), //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
						else
						{
							VennMaker
									.getInstance()
									.getProject()
									.save(VMPaths.getLastFileName().substring(0,
											VMPaths.getLastFileName().lastIndexOf(".venn")) //$NON-NLS-1$
											+ ".vennEn"); //$NON-NLS-1$

						}
						VennMaker.getInstance().exitVennMaker();
					}
					else
					{
						if (IO.saveRoutine() == IO.OPERATION_SUCCEEDED)
							VennMaker.getInstance().exitVennMaker();
					}
			}
		else
			VennMaker.getInstance().exitVennMaker();
	}

	public static void aboutUpdate()
	{
		try
		{
			String link;
			if (Locale.getDefault() == Locale.GERMAN)
				link = "http://www.vennmaker.com";
			else
				link = "http://www.vennmaker.com";
			SystemOperations.openUrl(link);

		} catch (URISyntaxException | IOException exn)
		{
			exn.printStackTrace();
		}
	}

	/**
	 * Einstellte Filter deaktivieren
	 */
	public static void deactiveFilter()
	{
		String filter = VennMaker.getInstance().getProject().getFilter();
		HashMap<Integer, Vector<Filterparameter>> filters = VennMaker.getInstance().getProject().getFilters();
				

		if (((filter != null) && (!filter.equals("") )) || ((filters != null) && (filters.size() > 0)))//$NON-NLS-1$
		{
			VennMaker.getInstance().getActualVennMakerView().deactivateFilter();
		}
		else
		{
			MySearchListener msl = new FilterDialog(false).new MySearchListener(
					filter);
			msl.filter();
		}
	}

	public static void refreshFilter()
	{
		if (!VennMaker.isInitialized())
			return;
		HashMap<Integer, Vector<Filterparameter>> filters = VennMaker.getInstance().getProject().getFilters();
		if ((filters != null)
				&& filters.size() != 0
				&& VennMaker.getInstance().getActualVennMakerView()
						.isFilterActive())
		{
			MySearchListener f = new FilterDialog(false).new MySearchListener(
					filters);
			f.filter();
		}
	}

	/**
	 * Audio Records exportieren
	 */
	public static void exportAudio()
	{
		String audioPath = VMPaths.getCurrentWorkingDirectory() + "/audio/";
		File dir = new File(audioPath);
		
		
		if (dir != null && dir.exists() && dir.listFiles().length > 0)
			new ExportAudioDialog();
		else
			JOptionPane.showMessageDialog(VennMaker.getInstance(),
					Messages.getString("AudioExportDialog.No_AudioFiles"),
					Messages.getString("AudioExportDialog.No_AudioFiles_Title"),
					JOptionPane.WARNING_MESSAGE,
					new ImageIcon(Messages.getString("VennMaker.Icon_Kommentar")));
	}

	/**
	 * Aktuelle Netzwerkkarte Klonen
	 */
	public static void cloneMap()
	{
		Netzwerk net = VennMaker.getInstance().getProject().getCurrentNetzwerk();
		String name = JOptionPane.showInputDialog(Messages
				.getString("ConfigDialog.30")); //$NON-NLS-1$
		net.cloneNetwork(name);
	}

	/**
	 * Neue, leere Netzwerkkarte hinzufuegen
	 */
	public static void addNewMap()
	{
		Netzwerk netzwerk = VennMaker.getInstance().getProject()
				.getCurrentNetzwerk()
				.getNewNetwork(Messages.getString("VennMaker.Network_New"));
		VennMaker.getInstance().getProject().setCurrentNetzwerk(netzwerk);
	}

	/**
	 * Interview beginnen
	 */
	public static void startInterview()
	{
		if (InterviewController.getInstance() != null)
		{
			InterviewController.getInstance().setCalledFromConfigDialog(false);
			InterviewController.getInstance().start();
		}
		else
		{
			InterviewController controller = new InterviewController();
			controller.setCalledFromConfigDialog(false);
			controller.start();
		}
	}

	/**
	 * Ein-/Ausschalten des Audio Recorders
	 */
	public static void toggleAudioRecorder()
	{
		if (Audiorecorder.getInstance().isRunning() == true)
		{
			Audiorecorder.getInstance().stopRecording();
		}
		else if (Audiorecorder.getInstance().isRunning() == false)
		{
			Audiorecorder.getInstance().startRecording();
		}
	}

	/**
	 * Anzeige des Audio Recorders
	 */
	public static void showAudioRecorder()
	{
		AudiorecorderWindow.showAudioWindow();
	}

	/**
	 * Daten exportieren
	 */
	public static void exportData()
	{
		if ((TestVersion.getEndDate() > 0) //$NON-NLS-1$
				&& (TestVersion.isExport() == false))
			TestVersion.infoDialog();
		else
		{
			ExportDialog export = new ExportDialog(VennMaker.getInstance()
					.getProject());
		}
	}

	/**
	 * Speichern unter neuem Project
	 */
	public static void saveAsProject()
	{
		if (IO.saveRoutine() == IO.OPERATION_FAILED)
			JOptionPane.showMessageDialog(VennMaker.getInstance(),
					Messages.getString("VennMaker.ErrorWriting") //$NON-NLS-1$
							+ VMPaths.getLastFileName(),
					Messages.getString("VennMaker.Error"), //$NON-NLS-1$
					JOptionPane.ERROR_MESSAGE);
	}
}
