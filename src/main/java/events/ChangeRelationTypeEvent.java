/**
 * 
 */
package events;

import data.RelationTyp;

/**
 * Dieses Event zeigt an, dass ein angegebener Relationstyp geändert wurde.
 * Es werden nicht die Änderungen gespeichert sondern eine Kopie des Objekts
 * vor der Änderung und das geänderte RelationsTypObjekt. Natürlich kann
 * sich dieses Objekt bereits erneut geändert haben - daher ist dieses Event
 * nur für UndoZwecke geeignet.
 * 
 * Dieses Event benötigt zwei Kopien des Relationstyps um die Daten zu speichern.
 * 
 * 
 * 
 * @version 0.6
 */
public class ChangeRelationTypeEvent extends RelationTypeEvent
{
	/**
	 * Eine Kopie des RelTypObjektes bevor es geändert wurde.
	 */
	final private RelationTyp oldCopy;
	
	/**
	 * Eine Kopie des RelTypObjektes nachdem es geändert wurde. Wird 
	 * für undo benötigt - damit ist Differenzenbildung möglich, wird bisher
	 * allerdings nüscht benutzt.
	 */
	final private RelationTyp newCopy;
	
	
	/**
	 * Erzeugt ein neues Event.
	 * @param typ Das Objekt, das verändert wurde.
	 * @param newCopy Eine Kopie des Inhalts nach der Änderung
	 * @param oldCopy Eine Kopie des Objekts, unmittelbar bevor es geändert wurde.
	 */
	public ChangeRelationTypeEvent(RelationTyp typ, RelationTyp newCopy, RelationTyp oldCopy)
	{
		super(typ);
		this.oldCopy = oldCopy;
		this.newCopy = newCopy;
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return "Change relation type";
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getRepeatEvent()
	 */
	@Override
	public VennMakerEvent getRepeatEvent()
	{
		// Kann man nicht wiederholen
		return null;
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getUndoEvent()
	 */
	@Override
	public VennMakerEvent getUndoEvent()
	{
		return new ChangeRelationTypeEvent(super.getRelationtyp(),this.oldCopy,this.newCopy) {

			/* (non-Javadoc)
			 * @see events.VennMakerEvent#isUndoevent()
			 */
			@Override
			public boolean isUndoevent()
			{
				return true;
			} };
	}

	/**
	 * Liefert den Wert des Relationstyps zurück, der vor
	 * Auftreten des Events galt.
	 * 
	 * @return Ein Kopie des Wertes vor der Ausführung des Events.
	 */
	public final RelationTyp getOldCopy()
	{
		return oldCopy;
	}
	
	/**
	 * Liefert den Wert des Relationstyps (als Kopie) zurück,
	 * der nach dem Auftreten des Events aktuell sein sollte. 
	 * 
	 * @return Eine Kopie des Zustands nach Ausführung des Events.
	 */
	public final RelationTyp getNewCopy()
	{
		return newCopy;
	}

}
