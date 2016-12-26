/**
 * 
 */
package data;

import events.VennMakerEvent;

/**
 * Dieses Interface müssen alle Listener für Events implementieren.
 * Solche Listener können sich an <code>EventProcessor</code> anmelden und
 * werden so über jedes aufgetretene Event informiert, sobald seine Auswirkungen
 * im Datenmodell umgesetzt wurden. GUI-Elemente können sich so darauf verlassen,
 * dass sie zum Zeitpunkt des Aufrufs von <code>eventConsumed</code> bereits auf 
 * das aktualisierte Datenmodell zugreifen. Die Reihenfolge des Aufrufs der Listener
 * ist jedoch nicht näher spezifiziert.
 * 
 *
 */
public interface EventPerformedListener
{
	/**
	 * Aufgerufen, sobald das angegebene Event im Datenmodell umgesetzt wurde.
	 * Bei komplexen Events wird diese Methode nur einmal mit einem Event vom Typ
	 * <code>ComplexEvent</code> aufgerufen. Diese Methode wird auch bei Undo-Events
	 * aufgerufen. Die Unterscheidung kann mit Hilfe von <code>event.isUndoevent()</code>
	 * vorgenommen werden.
	 * @param event Das zuletzt bearbeitete Event.
	 */
	public void eventConsumed(VennMakerEvent event);
}
