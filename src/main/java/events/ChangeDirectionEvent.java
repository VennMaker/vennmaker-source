/**
 * 
 */
package events;

import data.Akteur;
import data.Netzwerk;
import data.Relation;

/**
 * Dieses Event zeigt an, dass die Richtung einer Relation geändert wurde.
 * Vergleiche auch <code>ChangeTypeOfRelationEvent</code>. Nach Abarbeiten
 * dieses Events wird eine Relation Start- und Endknoten vertauscht haben.
 * 
 *
 */
public class ChangeDirectionEvent extends RelationEvent
{
	/**
	 * Speichert den original-Ziel-Akteur für die Relation. Da das Relations-
	 * objekt möglicherweise geändert wird, kann es sonst zu falschen Zuständen
	 * kommen.
	 */
	private Akteur originalSource;
	
	/**
	 * Erzeugt ein neues Richtungsänderungsevent.
	 * @param akteur Der Akteur, von dem die Relation ursprünglich ausgeht.
	 * @param netz Das Netzwerk, in dem sich die Relation befindet.
	 * @param relation Die Relation selbst.
	 */
	public ChangeDirectionEvent(Akteur akteur, Netzwerk netz, Relation relation)
	{
		super(akteur,netz,relation);
		this.originalSource = relation.getAkteur();
	}
	
	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return "Change direction of relation";
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getRepeatEvent()
	 */
	@Override
	public VennMakerEvent getRepeatEvent()
	{
		// Kann man so nicht wiederholen.
		return null;
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getUndoEvent()
	 */
	@Override
	public VennMakerEvent getUndoEvent()
	{
		// Selbst-Inverse.
		return new ChangeDirectionEvent(originalSource,getNetzwerk(),getRelation()) {

			/* (non-Javadoc)
			 * @see events.VennMakerEvent#isUndoevent()
			 */
			@Override
			public boolean isUndoevent()
			{
				return true;
			} };
	}

}
