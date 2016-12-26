/**
 * 
 */
package events;

import data.Akteur;
import data.Netzwerk;
import data.Relation;

/**
 * Dieses Ereignis repräsentiert die Tatsache, dass eine Relation zu einem Netzwerk
 * hinzugefügt wurde.
 * 
 *
 */
public class AddRelationEvent extends RelationEvent
{

	/**
	 * Erzeugt ein neues Ereignis.
	 * @param akteur Der Akteur, von dem die Relation ausgeht.
	 * @param netz Das Netzwerk, zu dem die Relation hinzugefügt wird.
	 * @param relation Die Relation, die hinzugefügt wird.
	 */
	public AddRelationEvent(Akteur akteur, Netzwerk netz, Relation relation)
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
		return new RemoveRelationEvent(getAkteur(),getNetzwerk(),getRelation())  {
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
		return Messages.getString("AddRelationEvent.desc"); //$NON-NLS-1$
	}

}
