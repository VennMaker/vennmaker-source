/**
 * 
 */
package events;

import data.Akteur;
import data.Netzwerk;

/**
 * Diese Klasse dient als Oberklasse von allen Events, die unmittelbar mit
 * Veränderungen von Akteuren in einem konkreten Netzwerk zu tun haben.
 * 
 * 
 *
 */
public abstract class ActorInNetworkEvent extends VennMakerEvent
{
	/**
	 * Der Akteur auf den sich das Event beziehen soll.
	 */
	private Akteur akteur;
	
	/**
	 * Das Netzwerk in dem das Ereignis aufgetreten ist.
	 */
	private Netzwerk netz;
	
	/**
	 * Erstellt ein neues Akteur-Event.
	 * @param akteur Der Akteur auf den sich das Event bezieht. Darf
	 * nicht <code>null</code> sein.
	 * @param netz Das Netzwerk in dem das Event aufgetreten ist. Darf nicht <code>null</code> sein.
	 */
	public ActorInNetworkEvent(Akteur akteur, Netzwerk netz)
	{
		super();
		
		assert (akteur != null);
		assert (netz   != null);
		
		this.akteur = akteur;
		this.netz = netz;
	}
	
	/**
	 * Liefert den Akteur zurück, auf den sich dieses Event bezieht.
	 * @return Ein Akteur.
	 */
	public Akteur getAkteur()
	{
		return this.akteur;
	}
	
	/**
	 * Liefert das Netzwerk zurück, in dem das Event passiert ist.
	 * @return Ein gültiges Netzwerk.
	 */
	public Netzwerk getNetzwerk()
	{
		return this.netz;
	}
}
