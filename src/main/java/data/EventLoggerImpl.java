/**
 * 
 */
package data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import events.VennMakerEvent;

/**
 * This class is a default implementation of the <code>EventLogger</code>-Interface.
 * It can be used in any object that requires a event logging mechanism.
 * 
 *
 */
public class EventLoggerImpl implements EventLogger, Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * A list of events that have already occured. Events that are undone are
	 * removed from the list rather than simply appending undo events. That way
	 * undo is not registered for event replay. 
	 */
	private final LinkedList<VennMakerEvent>		loggedEventQueue = new LinkedList<VennMakerEvent>(); 
	
	/**
	 * Keeps the sequence of events that were marked as undo-events. This 
	 * list is used to return reasonable redo-events and is purged after the occurence
	 * of a non-undo-event.
	 */
	private final LinkedList<VennMakerEvent>				undoCache = new LinkedList<VennMakerEvent>();

	/* (non-Javadoc)
	 * @see data.EventLogger#getLastEvent()
	 */
	@Override
	public VennMakerEvent getLastEvent() throws NoSuchElementException
	{
		return loggedEventQueue.getLast();
	}

	/* (non-Javadoc)
	 * @see data.EventLogger#getLastUndoEvent()
	 */
	@Override
	public VennMakerEvent getLastUndoEvent() throws NoSuchElementException
	{
	
		return this.undoCache.getLast();
	}

	/* (non-Javadoc)
	 * @see data.EventLogger#isEmpty()
	 */
	@Override
	public boolean isEmpty()
	{
		return loggedEventQueue.isEmpty();
	}

	/* (non-Javadoc)
	 * @see data.EventLogger#logEvent(events.VennMakerEvent, boolean)
	 */
	@Override
	public void logEvent(VennMakerEvent event, boolean clearUndoCache)
	{
		
		if(!event.isLogEvent())
			return;
			
		this.loggedEventQueue.addLast(event);
		if (clearUndoCache)
			this.undoCache.clear();
	}

	/* (non-Javadoc)
	 * @see data.EventLogger#redoLastEvent()
	 */
	@Override
	public void redoLastEvent()
	{
		this.undoCache.removeLast();
	}

	/* (non-Javadoc)
	 * @see data.EventLogger#undoLastEvent()
	 */
	@Override
	public void undoLastEvent()
	{
		VennMakerEvent evt = this.loggedEventQueue.removeLast();
		
		if(!evt.isLogEvent())
			return;
		
		this.undoCache.addLast(evt);
	}

}
