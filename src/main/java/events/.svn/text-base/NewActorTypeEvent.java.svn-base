/**
 * 
 */
package events;

import data.AkteurTyp;

/**
 * Wird erzeugt, wenn ein neuer Akteurstyp definiert wurde.
 * 
 * 
 * 
 */
@Deprecated
public class NewActorTypeEvent extends ActorTypeEvent
{

	/**
	 * Erzeugt ein neues Event, welches anzeigt, dass <code>typ</code> neu in die
	 * Datenbasis hinzugefügt wurde.
	 * 
	 * @param typ
	 *           Ein gültiger Typ.
	 */
	public NewActorTypeEvent(AkteurTyp typ)
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
		return new DeleteActorTypeEvent(getAkteurtyp())
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
		return Messages.getString("NewActorTypeEvent.desc"); //$NON-NLS-1$
	}

}
