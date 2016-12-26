/**
 * 
 */
package events;

import data.Netzwerk;

/**
 * Eine abstrakte Oberklassen für Events rund um Netzwerke.
 * 
 *
 */
public abstract class NetworkEvent extends VennMakerEvent
{
	/**
	 * Das zugrundeliegende Netzwerk.
	 */
	private Netzwerk netz;
	
	/**
	 * Erzeugt ein neues Event, dass mit dem angegebenen Netzwerk zu tun hat.
	 * @param netz Ein gültiges Netzwerk.
	 */
	public NetworkEvent(Netzwerk netz)
	{
		super();
		
		assert (netz != null); 
		
		this.netz = netz;
	}
	
	/**
	 * Liefert das zugrundeliegende Netzwerk zurück.
	 * @return Ein gültiges Netzwerk.
	 */
	public Netzwerk getNetzwerk()
	{
		return this.netz;
	}

}
