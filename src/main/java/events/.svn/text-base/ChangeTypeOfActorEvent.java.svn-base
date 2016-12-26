/**
 * 
 */
package events;

import data.Akteur;
import data.AkteurTyp;

/**
 * Dieses Ereignis tritt auf, wenn man den Typ eines Akteurs ändert. Nicht zu
 * verwechseln mit einem ChangeActorTypeEvent, das bei Modifikationen an einem
 * AkteurTyp auftritt.
 * 
 * 
 * 
 */
@Deprecated
public class ChangeTypeOfActorEvent extends ActorEvent
{
	private AkteurTyp	previousActorTyp;

	private AkteurTyp	newActorTyp;

	/**
	 * Erzeugt ein neues Event für den angebenen Akteur.
	 * 
	 * @param akteur
	 *           Der Akteur, der verändert wurde.
	 */
	public ChangeTypeOfActorEvent(final Akteur akteur,
			final AkteurTyp newActorType)
	{
		super(akteur);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see events.VennMakerEvent#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return "Type of actor changed";
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
		return new ChangeTypeOfActorEvent(getAkteur(), previousActorTyp)
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
	 * @see events.VennMakerEvent#isRedoable()
	 */
	@Override
	public boolean isRepeatable()
	{
		return false;
	}

	public AkteurTyp getActorTyp()
	{
		return newActorTyp;
	}

}
