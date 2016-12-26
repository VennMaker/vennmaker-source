/**
 * 
 */
package events;

import data.Akteur;
import data.Netzwerk;
import data.Relation;
import data.RelationTyp;

/**
 * Dieses Event zeigt an, dass der Typ einer Relation geändert wurde.
 * 
 * 
 *
 */
public class ChangeTypeOfRelationEvent extends RelationEvent
{

	/**
	 * Der Relationstyp der vorher ausgewählt war (benötigt für undo)
	 */
	private RelationTyp oldType;
	
	/**
	 * Der Relationstyp der jetzt für die Relation maßgeblich ist (benötigt für Modelländerung)
	 */
	private RelationTyp newType;

	/**
	 * Erzeugt ein neues Event, dass anzeigt, dass die Relation die vom angegebenen Akteur im Netz abgeht
	 * einen neuen Typ erhalten soll.
	 * @param akteur Der Akteur, von dem die Relation ausgeht.
	 * @param netz Das Netzwerk in dem die Relation vorkommt 
	 * @param relation Die betreffende Relation
	 * @param oldType Der Relationstyp der vorher ausgewählt war (explizit, damit Datenbestand konsistent bleibt)
	 * @param newType Der Relationstyp der jetzt für die Relation maßgeblich ist.
	 */
	public ChangeTypeOfRelationEvent(Akteur akteur, Netzwerk netz,
			Relation relation, RelationTyp oldType, RelationTyp newType)
	{
		super(akteur, netz, relation);
		this.oldType = oldType;
		this.newType = newType;
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return "Change relation type";
	}
	
	/**
	 * Liefert den Typ zurück, denn die angegebene Relation nach Ausführung des
	 * Events haben soll.
	 * @return Ein gültiger Eventtyp
	 */
	public RelationTyp getNewType()
	{
		return this.newType;
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getRepeatEvent()
	 */
	@Override
	public VennMakerEvent getRepeatEvent()
	{
		// Kann nicht wiederholt werden
		return null;
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getUndoEvent()
	 */
	@Override
	public VennMakerEvent getUndoEvent()
	{
		// Erzeuge neues Event und gebe es zurück.
		return new ChangeTypeOfRelationEvent(getAkteur(),getNetzwerk(),getRelation(),this.newType, this.oldType)
		{

			/* (non-Javadoc)
			 * @see events.VennMakerEvent#isUndoevent()
			 */
			@Override
			public boolean isUndoevent()
			{
				return true;
			}
			
		};
	}
	
	
	
}
