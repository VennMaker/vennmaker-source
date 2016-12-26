/**
 * 
 */
package wizards;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;

import data.EventProcessor;
import events.StartWizardEvent;

/**
 * Der WizardController bildet die Schnittstelle von VennMaker zu den Wizards;
 * er startet und steuert sie. Als Singleton existiert nur ein einziger
 * WizardController.
 * 
 * Der WizardController wird bei jedem perform()-Aufruf aktiv und veranlasst die
 * passenden Aktionen, etwa das Starten eines Wizards.
 * 
 * 
 * @deprecated
 * 
 */
public class WizardController
{
	/**
	 * Sollen debug-Ausgaben auf stdout ausgegeben werden?
	 */
	private static final boolean				debug		= false;

	/**
	 * Singleton: Referenz.
	 */
	private static WizardController			instance;

	/**
	 * Liste nacheinander abzuarbeitender Wizards.
	 */
	private LinkedList<VennMakerWizard>		wizards;

	/**
	 * Der Iterator zur Navigation durch die Wizardliste. Null wenn Liste leer.
	 */
	private ListIterator<VennMakerWizard>	iterator;

	/**
	 * Momentan aktiver Wizard. Null wenn kein Wizard läuft.
	 */
	private VennMakerWizard						running	= null;

	/**
	 * Sind wir gerade am rückwärtslaufen?
	 */
	private boolean								previous	= false;

	/**
	 * Singleton: Zugriff.
	 * 
	 * @return Die einzige WizardController-Instanz in diesem Prozess.
	 */
	public static WizardController getInstance()
	{
		if (instance == null)
		{
			instance = new WizardController();
		}
		return instance;
	}

	/**
	 * Initialisiert den WizardController mit der Liste konfigurierter Wizards.
	 * 
	 * @param wizards
	 */
	public void init(Vector<VennMakerWizard> wizards)
	{
		this.wizards = new LinkedList<VennMakerWizard>(wizards);
		iterator = this.wizards.listIterator();
	}

	/**
	 * Startet einen Wizard.
	 * 
	 * @param wizard
	 */
	private void invokeWizard(VennMakerWizard wizard)
	{
		if (debug)
			System.out.println("[WizardController] Starte " + wizard);
		running = wizard;
		EventProcessor.getInstance().fireEvent(new StartWizardEvent(wizard));
		wizard.invoke();
	}

	/**
	 * Beendet einen Wizard.
	 * 
	 * @param wizard
	 */
	private void shutdownWizard(VennMakerWizard wizard)
	{
		if (debug)
			System.out.println("[WizardController] Beende " + wizard);
		wizard.shutdown();
		running = null;
	}

	/**
	 * Die Methode perform() wird immer dann aufgerufen, wenn eine Aktion des
	 * WizardController erforderlich oder möglich ist. Dies ist beim Start des
	 * VennMakers der Fall sowie immer dann, wenn der Anwender auf den
	 * "Next"-Button in der VennMaker-Oberfläche klickt.
	 * 
	 * Als Folge wird ein eventuell laufender Wizard mittels shutdown() beendet
	 * und -- sofern vorhanden -- der nächste Wizard aufgerufen.
	 */
	public void perform()
	{
		if (debug)
			System.out.println("[WizardController] perform()");

		previous = false;

		/**
		 * Läuft ein Wizard? Dann beenden.
		 */
		if (running != null)
			shutdownWizard(running);

		/**
		 * Nächsten Wizard starten, wenn vorhanden.
		 */
		if (iterator != null && iterator.hasNext())
			invokeWizard(iterator.next());
	}

	/**
	 * Die Methode previous() beendet den aktuell laufenden Wizard und startet
	 * den vorigen.
	 */
	public void previous()
	{
		if (debug)
			System.out.println("[WizardController] previous()");

		previous = true;

		/**
		 * Läuft ein Wizard? Dann beenden.
		 */
		if (running != null)
			shutdownWizard(running);

		/**
		 * Vorigen Wizard starten, wenn vorhanden.
		 */
		if (iterator.hasPrevious())
		{
			iterator.previous();
			if (iterator.hasPrevious())
				iterator.previous();

			invokeWizard(iterator.next());
		}
	}

	/**
	 * Wenn der Wizard nichts (mehr) zu tun hat ruft er proceed() auf. Abhängig
	 * davon, ob wir gerade vorwärts oder rückwärts unterwegs sind, wird der
	 * nächste oder vorige Wizard gestartet.
	 */
	public void proceed()
	{
		if (debug)
			System.out.println("[WizardController] cancel()");

		/**
		 * Läuft ein Wizard? Dann beenden.
		 */
		if (running != null)
			shutdownWizard(running);

		if (previous)
		{
			/**
			 * Vorigen Wizard starten, wenn vorhanden.
			 */
			if (iterator.hasPrevious())
			{
				iterator.previous();
				if (iterator.hasPrevious())
					iterator.previous();

				invokeWizard(iterator.next());
			}
		}
		else
		{
			/**
			 * Nächsten Wizard starten, wenn vorhanden.
			 */
			if (iterator != null && iterator.hasNext())
				invokeWizard(iterator.next());
		}
	}

	/**
	 * Prüft, ob der übergebene Wizard als erstes gestartet wird/wurde.
	 * 
	 * @param wizard
	 * @return
	 */
	protected boolean isFirst(VennMakerWizard wizard)
	{
		return (wizards.getFirst() == wizard);
	}

}
