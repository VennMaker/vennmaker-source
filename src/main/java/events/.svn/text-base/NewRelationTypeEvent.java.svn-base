/**
 * 
 */
package events;

import data.RelationTyp;

/**
 * Wird erzeugt, wenn ein neuer Relationstyp definiert wurde.
 * 
 * 
 *
 */
public class NewRelationTypeEvent extends RelationTypeEvent
{

	/**
	 * Erzeugt ein neues Event, welches anzeigt,
	 * dass <code>typ</code> neu in die Datenbasis hinzugefügt wurde.
	 * @param typ Ein gültiger Typ.
	 */
	public NewRelationTypeEvent(RelationTyp typ)
	{
		super(typ);
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getRedoEvent()
	 */
	@Override
	public VennMakerEvent getRepeatEvent()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getUndoEvent()
	 */
	@Override
	public VennMakerEvent getUndoEvent()
	{
		return new DeleteRelationTypeEvent(getRelationtyp())  {
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
		return Messages.getString("NewRelationTypeEvent.desc"); //$NON-NLS-1$
	}
	
}
