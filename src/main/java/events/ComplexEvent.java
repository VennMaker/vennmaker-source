/**
 * 
 */
package events;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Dieses Ereignis entspricht nicht einem normalen Ereignis sondern eine Folge von Ereignissen,
 * die atomar (das heißt, nach außen gleichzeitig) auftreten. Die gesamte Eventverarbeitung eines
 * komplexen Ereignisses geschieht ohne Unterbrechung. Die Interfaces für VennMakerEvent sind 
 * entsprechend angepasst und funktionieren wie gewohnt.
 * 
 *
 */
public class ComplexEvent extends VennMakerEvent implements Iterable<VennMakerEvent>
{
	/**
	 * Speichert alle Events, die gleichzeitig auftreten sollen.
	 */
	private LinkedList<VennMakerEvent> events;
	
	/**
	 * Die textuelle Beschreibung des Events (damit sind aussagekräftigere Hinweise möglich).
	 */
	private String description;
	
	/**
	 * Speichert, ob alle Operationen wiederholbar sind um möglicherweise auch als komplexes
	 * Event wiederholbar zu sein.
	 */
	private boolean redoable;
	
	/**
	 * Erzeugt ein neues Event mit einer angebenen Beschreibung.
	 * @param desc Eine Beschreibung des Events.
	 */
	public ComplexEvent(String desc)
	{
		this.events = new LinkedList<VennMakerEvent>();
		this.description = desc;
	}
	
	/**
	 * Fügt dieses Event am Ende der Eventfolge an.
	 * @param event Anzufügendes Ereignis.
	 */
	public void addEvent(VennMakerEvent event)
	{
		if(!this.isLogEvent())
			event.setIsLogEvent(false);
		
		this.events.addLast(event);
		if (!event.isRepeatable())
			redoable = false;
	}
	
	/**
	 * Gibt die Anzahl der Events in dem Event an. Sollten
	 * in diesem Event weitere komplexe Events vorhanden sein, so werden diese
	 * als ein einziges Event gewertet.
	 * @return Die Anzahl der Events in diesem komplexen Event.
	 */
	public int length()
	{
		return this.events.size();
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return description;
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getRedoEvent()
	 */
	@Override
	public VennMakerEvent getRepeatEvent()
	{
		if (redoable == false)
			return null;
		
		// Erzeuge wiederholbares Ereignis.
		ComplexEvent redoEvent = new ComplexEvent(description);
		
		for (VennMakerEvent event : this.events)
		{
			// Exakte (deep) Kopie aufbauen.
			VennMakerEvent redoAtomEvent = event.getRepeatEvent();
			assert (redoAtomEvent != null);
			redoEvent.addEvent(redoAtomEvent);
		}
		
		assert (redoEvent.length() == length());
		
		return redoEvent;
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getUndoEvent()
	 */
	@Override
	public VennMakerEvent getUndoEvent()
	{
		// Erzeuge wiederholbares Ereignis.
		ComplexEvent undoEvent = new ComplexEvent(description) {
			@Override
			public boolean isUndoevent()
			{
				return true;
			} };
		
		for (VennMakerEvent event : this.events)
		{
			// Umgekehrte (deep) Kopie aufbauen.
			VennMakerEvent undoAtomEvent = event.getUndoEvent();
			assert (undoAtomEvent != null);
			undoEvent.events.addFirst(undoAtomEvent);
		}
		
		assert (undoEvent.length() == length());
		
		return undoEvent;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<VennMakerEvent> iterator()
	{
		return this.events.iterator();
	}

}
