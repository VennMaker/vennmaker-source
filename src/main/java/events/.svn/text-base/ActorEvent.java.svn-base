/**
 * 
 */
package events;

import data.Akteur;

/**
 * Ein Event, das konkret mit einem Akteur in Verbindung mit der Datenbasis zu tun hat.
 * Änderungen an einem Netzwerk werden hiermit nicht erfasst.
 * 
 * 
 *
 */
public abstract class ActorEvent extends VennMakerEvent
{
	/**
	 * 
	 */
	private Akteur akteur;
	
	/**
	 * Erzeugt einen neues Event. 
	 * @param akteur Der Akteur, mit dem das Ereignis zu tun hat.
	 */
	public ActorEvent(Akteur akteur)
	{
		this.akteur = akteur;
	}
	

	/**
	 * Liefert den Akteur zurück, mit dem das Ereignis zu tun hat.
	 * @return the akteur
	 */
	public Akteur getAkteur()
	{
		return akteur;
	}

}
