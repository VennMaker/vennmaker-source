/**
 * 
 */
package data;

import events.VennMakerEvent;

/**
 * Dieses Interface repräsentiert einen Eventlistener für einen bestimmten 
 * Typ an Events. Jeder Listener wird über alle Events eines Typs (auch Spezialisierungen)
 * informiert.
 * 
 * @param <T> Der Eventtyp auf den reagiert werden soll.
 *
 */
public interface EventListener<T extends VennMakerEvent>
{
	/**
	 * Aufgerufen wenn ein Event vom Typ T verzeichnet wurde.
	 * @param event Das Event das aufgetreten ist. Kann nach <code>T</code> konvertiert werden.
	 */
	public void eventOccured(VennMakerEvent event);
	
	/**
	 * Der Typ an Events für den dieser Listener geeignet ist. 
	 * @return Eine Klassenrepräsentation des von diesem Listener erkannten Eventtyps.
	 */
	public Class<T> getEventType();
}
