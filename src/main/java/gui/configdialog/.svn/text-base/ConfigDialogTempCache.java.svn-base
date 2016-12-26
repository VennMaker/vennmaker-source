/**
 * 
 */
package gui.configdialog;

import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.ImmidiateConfigDialogSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasse die sich um die Einstellungen die in den Config Dialogen gemacht
 * werden kuemmert.</br> Beinhaltet eine Liste aller Settings und den aktiven
 * Dialogen.
 * 
 * 
 */
public class ConfigDialogTempCache
{
	/**
	 * Liste der Settings die eingestellt wurden.
	 */
	private List<ConfigDialogSetting>		settings			= new ArrayList<ConfigDialogSetting>();

	/**
	 * Liste der mindestens einmal aktiven Dialoge.</br> Diese Liste wird am
	 * Schluss durchlaufen um die finalen Einstellungen der Dialoge zu bekommen.
	 */
	private List<ConfigDialogElement>		activeElements	= new ArrayList<ConfigDialogElement>();

	/**
	 * Referenz auf sich selbst (Singleton Pattern)
	 */
	private static ConfigDialogTempCache	cdTempCache;

	/**
	 * Benutzen um Instanz zu erzeugen/erhalten.</br> Singleton Pattern
	 */
	public static ConfigDialogTempCache getInstance()
	{
		if (cdTempCache == null)
		{
			cdTempCache = new ConfigDialogTempCache();
		}
		return cdTempCache;
	}

	/**
	 * Ein Setting in die Liste einfuegen.</br> Nur fuer Immidiate-Settings
	 * benoetigt, da beim druecken von "Ok" im ConfigDialog</br> alle sonstigen
	 * Settings bei den einzelnen Elementen abgeholt werden.
	 */
	public void addSetting(ConfigDialogSetting cdSetting)
	{
		if (cdSetting == null)
			return;
		if (cdSetting instanceof ImmidiateConfigDialogSetting)
			cdSetting.set();
		settings.add(cdSetting);
	}

	/**
	 * Fuehrt alle Settings durch.
	 */
	public void setAllSettings()
	{
		// Von allen Dialog die mal Aktiv waren die finalen Settings "ziehen"
		for (ConfigDialogElement e : activeElements)
		{
			ConfigDialogSetting s = e.getFinalSetting();
			if (s != null)
				settings.add(s);
		}
		// Alle Settings der Reihe nach ausfuehren
		for (ConfigDialogSetting s : settings)
		{
			// ImmidiateSettings wurden schon gesetzt
			if (!(s instanceof ImmidiateConfigDialogSetting))
				s.set();
		}
		reset();
	}

	/**
	 * Setzt alles auf Ursprung zurueck.
	 */
	private void reset()
	{
		settings.clear();
		activeElements.clear();
		ConfigDialogLayer.getInstance().resetActiveElements();
	}

	/**
	 * Verwirft alle unausgefuehrten Settings und macht die ausgefuehrten
	 * (Immidiate Settings) rueckgaengig.
	 */
	public void cancelAllSettings()
	{ // von hinten nach vorne laufen um den Zeitlichen ablauf beizubehalten
		for (int i = settings.size() - 1; i >= 0; i--)
		{
			if (settings.get(i) instanceof ImmidiateConfigDialogSetting)
				((ImmidiateConfigDialogSetting) settings.get(i)).undo();
		}
		reset();
	}

	/**
	 * Um einen Dialog in die Liste der aktiven Elemente einzufuegen.</br> Beim
	 * einfuegen (passiert nur einmal) wird buildPanel() ausgefuehrt.
	 */
	public void addActiveElement(ConfigDialogElement elem)
	{
		if (!activeElements.contains(elem))
		{
			activeElements.add(elem);
			elem.buildPanel();
		}
	}

	public void clearActiveElementsList()
	{
		activeElements.clear();
	}

	public List<ConfigDialogElement> getAllElements()
	{
		return activeElements;
	}
}
