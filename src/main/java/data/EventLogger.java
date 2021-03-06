/**
 * 
 */
package data;

import java.util.NoSuchElementException;

import events.VennMakerEvent;


public interface EventLogger
{

	/**
	 * Jedes aufgetretene Event, dass einen Einfluss auf das Interview hat(te), sollte
	 * über diese Methode gespeichert werden.
	 * @param event Ein gültiges Event
	 * @param clearUndoCache zeigt an, ob das Event ein neues Event war. Ist dieser Parameter
	 * <code>false</code>, wird der UndoCache nicht gelöscht, etwa nach Ausführung eines Events,
	 * dass ein UndoEvent ist.
	 */
	public void logEvent(VennMakerEvent event, boolean clearUndoCache);

	/**
	 * Undo. Das zuletzt ereignete Event wird aus der Queue entfernt. Nach außen erscheint
	 * es so, als ob es nie stattgefunden hätte.
	 */
	public void undoLastEvent();
	
	/**
	 * Redo. Fügt das zuletzt rückgängig gemachte Event wieder in die normale Queue. Nach außen
	 * erscheint es so, als ob es nie rückgängig gemacht wurde.
	 */
	public void redoLastEvent();
	
	/**
	 * Liefert das zuletzt rückgäng gemachte Ereignis zurück. Ist kein solches Ereignis vorhanden, oder
	 * seitdem bereits ein neues Event aufgetreten, das kein UndoEvent ist, so 
	 * wird eine NoSuchElementException erzeugt.
	 * @return Das Event, dass zuletzt umgekehrt wurde.
	 * @throws NoSuchElementException wenn kein Event zuletzt rückgängig gemacht wurde 
	 */
	public VennMakerEvent getLastUndoEvent() throws NoSuchElementException;
	

	/**
	 * Gibt das letzte Event-Objekt der Queue zurück (zuletzt ausgeführt), dass auch
	 * rückgängig gemacht werden kann.
	 * @return Ein Event-Objekt.
	 * @throws NoSuchElementException Wenn kein Event in der Queue gespeichert ist.
	 */
	public VennMakerEvent getLastEvent() throws NoSuchElementException;

	/**
	 * Liefert <code>true</code> wenn bisher kein Event im Log gespeichert wurde.
	 * @return <code>false</code> sonst.
	 */
	public boolean isEmpty();
}
