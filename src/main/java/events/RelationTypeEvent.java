/**
 * 
 */
package events;

import data.RelationTyp;

/**
 * Ein übergeordnetes Event, das für Änderungen an Relationstypen
 * missbraucht werden kann.
 * 
 * 
 *
 */
public abstract class RelationTypeEvent extends VennMakerEvent
{
	private RelationTyp typ;
	
	/**
	 * Erzeugt ein neues Event, das mit dem angegebenen AkteurTyp zusammenhängt.
	 * @param typ Ein gültiger Akteurtyp
	 */
	public RelationTypeEvent(RelationTyp typ)
	{
		assert (typ != null);
		this.typ = typ;
	}
	
	/**
	 * Liefert den betreffenden Relationstyp zurück. 
	 * @return Ein gültiger Relationstyp.
	 */
	public RelationTyp getRelationtyp()
	{
		return this.typ;
	}


}
