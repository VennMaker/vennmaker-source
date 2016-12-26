/**
 * 
 */
package events;

import data.Akteur;
import data.Netzwerk;
import data.Relation;

/**
 * Erzeugt, wenn eine Relation aus einem Netzwerk entfernt wird.
 * 
 *
 */
public class RemoveRelationEvent extends RelationEvent
{

	/**
	 * Erzeugt ein neues Event.
	 * @param akteur Der Akteur, von dem die Relation ausging.
	 * @param netz Das Netzwerk, aus dem die Relation gelöscht wurde.
	 * @param relation Die Relation, die entfernt wird.
	 */
	public RemoveRelationEvent(Akteur akteur, Netzwerk netz, Relation relation)
	{
		super(akteur, netz, relation);
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
		return new AddRelationEvent(getAkteur(), getNetzwerk(), getRelation())  {
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
		return Messages.getString("RemoveRelationEvent.desc"); //$NON-NLS-1$
	}

}
