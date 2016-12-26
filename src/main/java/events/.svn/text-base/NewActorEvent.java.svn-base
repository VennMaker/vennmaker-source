/**
 * 
 */
package events;

import data.Akteur;

/**
 * Dieses Event markiert den Zeitpunkt, zu dem ein Akteur neu erfasst wird. Dies
 * kann durch Wizard-Abfrage oder das explizite spätere hinzufügen passiert sein.
 * Ein solcher Akteur gehört anfangs zu keinem Netzwerk, weshalb dies hier nicht
 * eingetragen wird.
 * 
 *
 */
public class NewActorEvent extends ActorEvent
{

	/**
	 * Erzeugt ein neues Event, dass ausgelöst wird, wenn ein neuer Akteur zur
	 * Datenbasis hinzugefügt wurde.
	 * @param akteur
	 */
	public NewActorEvent(Akteur akteur)
	{
		super(akteur);
	}


	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getRedoEvent()
	 */
	@Override
	public VennMakerEvent getRepeatEvent()
	{
		// Ein Akteur kann nur einmal hinzugefügt werden.
		return null;
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getUndoEvent()
	 */
	@Override
	public VennMakerEvent getUndoEvent()
	{
		return new DeleteActorEvent(getAkteur())  {
			@Override
			public boolean isUndoevent()
			{
				return true;
			}  };
	}


	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return Messages.getString("NewActorEvent.desc"); //$NON-NLS-1$
	}
}
