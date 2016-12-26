/**
 * 
 */
package events;

import data.Akteur;

/**
 * Dieses Event wird ausgelöst, wenn ein Akteur endgültig gelöscht wurde,
 * er also nicht mehr im Datenbestand vorhanden sein soll.
 * 
 * 
 *
 */
public class DeleteActorEvent extends ActorEvent
{

	/**
	 * Erzeugt ein neues Lösch-Event. Zeigt an, dass der entsprechende Akteur aus
	 * der Datenbasis entfernt wurde.
	 * @param akteur
	 */
	public DeleteActorEvent(Akteur akteur)
	{
		super(akteur);
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getRedoEvent()
	 */
	@Override
	public VennMakerEvent getRepeatEvent()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getUndoEvent()
	 */
	@Override
	public VennMakerEvent getUndoEvent()
	{
		return new NewActorEvent(getAkteur())  {
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
		return Messages.getString("DeleteActorEvent.desc"); //$NON-NLS-1$
	}
}
