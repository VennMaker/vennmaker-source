/**
 * 
 */
package events;

import data.AkteurTyp;

/**
 * Ein übergeordnetes Event, das für Änderungen an Akteurstypen missbraucht
 * werden kann.
 * 
 * 
 * 
 */
@Deprecated
public abstract class ActorTypeEvent extends VennMakerEvent
{
	private AkteurTyp	typ;

	/**
	 * Erzeugt ein neues Event, das mit dem angegebenen AkteurTyp zusammenhängt.
	 * 
	 * @param typ
	 *           Ein gültiger Akteurtyp
	 */
	public ActorTypeEvent(AkteurTyp typ)
	{
		assert (typ != null);
		this.typ = typ;
	}

	/**
	 * Liefert den betreffenden Akteurtyp zurück.
	 * 
	 * @return Ein gültiger Akteurtyp.
	 */
	public AkteurTyp getAkteurtyp()
	{
		return this.typ;
	}

}
