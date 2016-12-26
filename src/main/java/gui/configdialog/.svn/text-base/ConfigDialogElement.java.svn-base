/**
 * 
 */
package gui.configdialog;
import gui.Messages;
import gui.configdialog.elements.CDialogNetworkClone;
import gui.configdialog.save.SaveElement;
import gui.configdialog.settings.ConfigDialogSetting;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import data.Netzwerk;

/**
 *	Ein Element im Baum des Config-Dialogs. Ein Panel, indem es dem Benutzer
 * möglich ist Veränderungen an den Einstellungen von VennMaker vorzunehmen.
 * Jedes Element dient einer bestimmten Einstellung bzw. Reihe von Einstellungen.
 * 
 * 
 */
public abstract class ConfigDialogElement extends JPanel
{
	protected static final long	serialVersionUID	= 1L;
	/**
	 * Panel eines jeden Dialogs
	 */
	protected JPanel dialogPanel;
	/**
	 * Netzwerk des jeweiligen Elements</br>
	 * {@code null} - falls Projekt-Dialog oder Unique-Dialog
	 */
	protected Netzwerk net = null;
	
	//template Methoden
	/**
	 * Template Methode um das Panel zu liefern.</br>
	 * Bevor es zur Rueckgabe kommt wird noch die Hook-Methode "refreshBeforeGetDialog"</br>
	 * aufgerufen, die ein Element ueberschreien kann um sich auf dem aktuellen Stand 
	 * zu halten
	 * @return dialogPanel - Panel das im ConfigDialog dargestellt werden soll
	 */
	public final JPanel getDialog()
	{
		refreshBeforeGetDialog();
		return dialogPanel;
	}
	
	/**
	 * Methode um bei Netzwerk weiten Elementen das Netzwerk zu setzen.</br>
	 * Muss nach dem instanzieren gesetzt werden, da das erzeugen eines Elements anhand von</br>
	 * Reflections implementiert ist und dies nur mit leerem Standardkonstruktor möglich ist.</br>
	 * @see Class#newInstance()
	 */
	public final void setNet(Netzwerk net)
	{
		this.net = net;
	}
	
	/**
	 * Methode um das eingestellte Netzwerk zurueckzugeben. Wenn es ein Projektweites oder einzigartiges
	 * Element ist wird {@code null} zurueckgegeben
	 * @return network
	 */
	public final Netzwerk getNet()
	{
		return this.net;
	}
	
	/**
	 * Liefert den Titel des Dialog-Elements
	 * Es wird in den jeweiligen Properties nach "Klassenname.Title" gesucht.</br>
	 * Bsp.:</br>{@code Messages.getString("CDialogSector.Title")}
	 */
	public final String getTitle()
	{
		return Messages.getString(getClass().getSimpleName()+".Title");
	}	
	/**
	 * Liefert die Beschreibung des Dialog-Elements
	 * Es wird in den jeweiligen Properties nach "Klassenname.Description" gesucht.</br>
	 * Bsp.:</br>{@code Messages.getString("CDialogSector.Description")}
	 */
	public final String getDescription()
	{
		return Messages.getString(getClass().getSimpleName()+".Description");
	}
	/**
	 * Liefert den String-Wert des Dialog-Elements
	 * Es wird in den jeweiligen Properties nach "Klassenname.ToString" gesucht.</br>
	 * Bsp.:</br>{@code Messages.getString("CDialogSector.ToString")}
	 */
	@Override
	public final String toString()
	{
		return Messages.getString(getClass().getSimpleName()+".ToString");
	}
	
	//von jedem Dialog zu implementieren
	/**
	 * Methode erzeugt das Panel {@code dialogPanel} und fuellt es mit den noetigen 
	 * Elementen.
	 */
	public abstract void buildPanel();
	/**
	 * Methode liefert die Einstellungen eines Elements zurueck.</br>
	 * Wird vom ConfigDialogTempCache verwendet um von jedem Dialog (der aktiv war)</br>
	 * die des Benutzers Einstellungen abzufragen.</br>
	 * @return {@code null} - wenn es ein Dialog ist der ausschließlich {@code ImmidiateConfigDialogSetting} erzeugt.
	 */
	public abstract ConfigDialogSetting getFinalSetting();
	
	// (optionale Methoden) Hooks
	/**
	 * Diese Methode kann verwendet werden um das ConfigDialogElement vor dem zurueckliefern </br>
	 * seines Panels auf den neusten Stand zu bringen.</br> 
	 * Nötig z.B. bei Dialogen die von anderen Immidiate Einstellungen abhaengig sind. </br>Im Moment nur Attribut-abhängige Dialoge.
	 */
	public void refreshBeforeGetDialog()
	{
	}
	
	/**
	 * Kann ueberschrieben werden wenn ein Dialog-Element ein Icon besitzen soll.</br>
	 * @see CDialogNetworkClone#getIcon()
	 */
	public ImageIcon getIcon()
	{
		return null;
	}
	
	/**
	 * Sets the Attributes of the ConfigDialogElement from the SaveElement
	 * @param setting SaveElement 
	 */
	public abstract void setAttributesFromSetting(SaveElement setting);
	
	/**
	 * Returns a SaveElement, wich contains all necessary Attributes to save
	 * @return SaveElement to save
	 */
	public abstract SaveElement getSaveElement();
}
