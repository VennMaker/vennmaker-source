/**
 * 
 */
package events;

import data.Akteur;
import data.Netzwerk;
import data.Relation;

/**
 * Eine abstrakte Oberklasse für Ereignisse rund um Relationen.
 * 
 * 
 *
 */
public abstract class RelationEvent extends ActorInNetworkEvent
{
	/**
	 * Die betrachtete Relation
	 */
	private Relation relation;
	
	/**
	 * Erzeugt ein neues Relations-Ereignis.
	 * @param akteur Der Akteur, von dem die Relation ausgeht.
	 * @param netz Das Netzwerk in dem die Relation vorhanden ist.
	 * @param relation Die betreffende Relation.
	 */
	public RelationEvent(Akteur akteur, Netzwerk netz, Relation relation)
	{
		super(akteur,netz);
		this.relation = relation;
	}

	/**
	 * Liefert die zugrundeliegende Relation zurück.
	 * @return the relation
	 */
	public Relation getRelation()
	{
		return relation;
	}
}
