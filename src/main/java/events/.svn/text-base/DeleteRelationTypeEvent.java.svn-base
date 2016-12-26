/**
 * 
 */
package events;

import data.RelationTyp;

/**
 * Dieses Event zeigt das Löschen eines Relationstyps aus der Datenbasis an.
 * 
 *
 */
public class DeleteRelationTypeEvent extends RelationTypeEvent
{

	/**
	 * Erzeugt ein neues Event zur Löschung von <code>typ</code>.
	 * @param typ Der zu löschende Relationstyp.
	 * 
	 */
	public DeleteRelationTypeEvent(RelationTyp typ)
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
		return new NewRelationTypeEvent(getRelationtyp())  {
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
		return Messages.getString("DeleteRelationTypeEvent.desc"); //$NON-NLS-1$
	}
	
	

}
