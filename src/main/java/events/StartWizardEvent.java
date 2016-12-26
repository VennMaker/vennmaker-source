/**
 * 
 */
package events;

import wizards.VennMakerWizard;

/**
 * Dieses Event repräsentiert den Start eines Wizards, der WizardController löst
 * es aus.
 * 
 * Dieses Event hat direkt keinen Einfluss auf das Datenmodell, da die
 * Änderungen, die durch einen Wizard vorgenommen werden, wieder neue Events
 * verursachen. Dieses Event ist nur dafür da, beim späteren Replay
 * nachvollziehen zu können, wann es passiert ist. Das Event liefert als
 * undo-Event sich selbst zurück. Bei einem Undo-Versuch sollte dann das Event
 * aus der Event-Queue gelöscht werden und da kein Modell-Objekt auf ein
 * StartWzardEvent reagiert auch keine Änderung verursachen. Wahlweise könnte
 * man auch noch einführen, dass Events von diesem Typ nicht rückgängig gemacht
 * werden können und auch nicht im Undo-Button zu "sehen" sind.
 * 
 * 
 */
public class StartWizardEvent extends VennMakerEvent
{
	private VennMakerWizard	wizard;

	/**
	 * Erzeugt ein neues Event.
	 * 
	 * @param wizard
	 *           Ein gültiger Wizard
	 */
	public StartWizardEvent(VennMakerWizard wizard)
	{
		assert (wizard != null);
		this.wizard = wizard;
	}

	/**
	 * Liefert den gestarteten Wizard zurück.
	 * 
	 * @return Der Wizard.
	 */
	public VennMakerWizard getWizard()
	{
		return this.wizard;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see events.VennMakerEvent#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return "Start Wizard";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see events.VennMakerEvent#getRepeatEvent()
	 */
	@Override
	public VennMakerEvent getRepeatEvent()
	{
		// Kann man nicht wiederholen
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see events.VennMakerEvent#getUndoEvent()
	 */
	@Override
	public VennMakerEvent getUndoEvent()
	{
		StartWizardEvent undoEvent = new StartWizardEvent(this.wizard)
		{
			@Override
			public boolean isUndoevent()
			{
				return true;
			}
		};
		return undoEvent;
	}

}
