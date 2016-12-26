/**
 * 
 */
package wizards;

import files.IO;
import gui.VennMaker;

/**
 * Dieser Wizard speichert das aktuelle Projekt in einer Datei ab, nach dem
 * Dateinamen wurde nicht gefragt - jetzt schon!
 * 
 * 
 */
public class SaveProjectWizard extends VennMakerWizard
{
	/**
	 * Soll VennMaker nach dem Speichern der Datei beendet werden?
	 */
	private boolean	closeAfterSave	= true;

	@Override
	public void invoke()
	{
		// /**
		// * Suche 6-stellige Zufallszahl als Dateiname.
		// */
		// File file;
		// do
		// {
		// int code = 100000 + (int) Math.round(Math.random() * 99999);
		// String filename = VennMaker.getInstance().getCurrentWorkingDirectory()
		//			+ "/" + code + Messages.getString("VennMaker.Suffix"); //$NON-NLS-1$ //$NON-NLS-2$
		// file = new File(filename);
		// } while (file.exists());
		// if (VennMaker.getInstance().getProjekt().save(file.getAbsolutePath())
		// == false)
		// JOptionPane
		// .showMessageDialog(VennMaker.getInstance(), Messages
		//					.getString("VennMaker.ErrorWriting") //$NON-NLS-1$
		// + file.getAbsolutePath(), Messages
		//					.getString("VennMaker.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$

		/*
		 * User zwingen zu speichern - Abbruch führt zum erneuten Aufruf des
		 * Speicherdialogs
		 */
		while (IO.saveRoutine() == IO.OPERATION_CANCELED);
		WizardController.getInstance().perform();

	}

	/*
	 * Beendet VennMaker, wenn gewünscht.
	 * 
	 * @see wizards.VennMakerWizard#shutdown()
	 */
	@Override
	public void shutdown()
	{
		if (closeAfterSave)
			VennMaker.getInstance().exit();
	}

	/**
	 * Legt fest ob VennMaker nach dem Speichern durch en Wizard beendet werden
	 * soll.
	 * 
	 * @param closeAfterSave
	 */
	public void setCloseAfterSave(boolean closeAfterSave)
	{
		this.closeAfterSave = closeAfterSave;
	}
}
