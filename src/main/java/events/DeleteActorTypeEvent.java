/**
 * 
 */
package events;

import data.AkteurTyp;

/**
 * Dieses Event zeigt das Löschen eines Akteurstyps aus der Datenbasis an.
 * 
 * 
 * 
 */
@Deprecated
public class DeleteActorTypeEvent extends ActorTypeEvent
{

	/**
	 * Erzeugt ein neues Event zur Löschung von <code>typ</code>.
	 * 
	 * @param typ
	 *           Der zu löschende Akteurstyp.
	 * 
	 */
	public DeleteActorTypeEvent(AkteurTyp typ)
	{
		super(typ);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see events.VennMakerEvent#getRedoEvent()
	 */
	@Override
	public VennMakerEvent getRepeatEvent()
	{
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
		return new NewActorTypeEvent(getAkteurtyp())
		{
			@Override
			public boolean isUndoevent()
			{
				return true;
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see events.VennMakerEvent#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return Messages.getString("DeleteActorTypeEvent.desc"); //$NON-NLS-1$
	}

}
