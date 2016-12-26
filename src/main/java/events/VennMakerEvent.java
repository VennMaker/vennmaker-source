/**
 * 
 */
package events;

/**
 * Eine abstrakte Oberklasse für alle Ereignisse, die in VennMaker auftreten
 * können. Diese Klasse speichert ihren Erstellungszeitpunkt, so dass ein
 * genaues replay von Ereignissen durchgeführt werden kann.
 * 
 * 
 * 
 */
public abstract class VennMakerEvent
{
	/**
	 * Der Zeitpunkt der Erstellung.
	 */
	private final long	timestamp;
	
	
	protected boolean  logEvent = true;

	/**
	 * Erzeugt ein neues Event und speichert den aktuellen Zeitpunkt.
	 */
	public VennMakerEvent()
	{
		this.timestamp = System.currentTimeMillis();
	}
	
	/**
	 * Erstellt ein Undo-Event. 
	 * @return Ein Event, dass die Änderungen des aktuellen Events rückgängig machen würde. Ist niemals
	 * <code>null</code>.
	 */
	public abstract VennMakerEvent getUndoEvent();
	
	/**
	 * Erstellt ein Wiederholungs-Event.
	 * @return Ein Event, dass die Änderungen des aktuellen Events nocheinmal durchführen würde,
	 * oder <code>null</code>, wenn es kein solches Event geben kann oder es keinen Sinn machte.
	 */
	public abstract VennMakerEvent getRepeatEvent();
	
	/**
	 * Liefert eine textuelle Beschreibung des Events. Dies wird für eine benutzerfreundliche
	 * Event-Steuerung benötigt. Die Beschreibung sollte möglichst knapp sein.
	 * @return ein String, der den Inhalt des Events repräsentiert.
	 */
	public abstract String getDescription();
	
	/**
	 * Liefert <code>false</code>, wenn das Event originär ist, d.h. es kann vom Benutzer rückgängig gemacht werden.
	 * Ein Event, dass lediglich deswegen ausgelöst wurde, weil der Benutzer etwas rückgängig machen wollte, soll
	 * nicht mehr rückgängig zu machen sein, sondern es soll in den alten Zustand überführen. Um dies zu erkennen
	 * wird diese Funktion benötigt.
	 * @return <code>true</code>, wenn das Event nicht mehr umkehrbar sein soll, weil es schon eine Umkehrung ist.
	 */
	public boolean isUndoevent()
	{
		return false;
	}
	
	/**
	 * Liefert <code>false</code>, wenn das Event nicht wiederholbar ist. Dies ist der Standard. Events die wiederholbar
	 * sein wollen, müssen explizit diese Methode überlagern um entsprechend erkannt zu werden.
	 * @return <code>true</code>, wenn das Event wiederholbar ist.
	 */
	public boolean isRepeatable()
	{
		return false;
	}
	
	/**
	 * Bequemlichkeitsmethode zum besseren Debuggen.
	 */
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.getClass() + "@" + this.timestamp; //$NON-NLS-1$
	}
	
	/**
	 * Liefert den Zeitpunkt der Instantiierung des Events zurück.
	 * 
	 * @return Die Anzahl der Millisekunden seit 1. Januar 1970 und Erzeugung des
	 *         Objekts.
	 */
	public long getTimestamp(){
		return timestamp;
	}
	
	/**
	 * If this Method returns true, the Event can not be undone anymore
	 * Default value is <code> false </code>
	 * @return true if this Event should not undoable, false otherwise
	 * 
	 */
	public boolean isLogEvent()
	{
		return logEvent;
	}
	
	public void setIsLogEvent(boolean logEvent)
	{
		this.logEvent = logEvent;
	}
	
}
