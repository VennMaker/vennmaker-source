/**
 * 
 */
package data;

import javax.swing.event.EventListenerList;

import events.MediaEvent;

/**
 * 
 * Liste der MediaEvent-Objekte
 */
public class MediaEventList
{

	/** List of listeners. */
	private static EventListenerList	listeners	= new EventListenerList();

	/**
	 * Singleton: Referenz.
	 */
	private static MediaEventList		instance;

	/**
	 * Singleton: Zugriff.
	 * 
	 * @return Die einzige Instanz in diesem Prozess.
	 */
	public static MediaEventList getInstance()
	{
		if (instance == null)
		{
			instance = new MediaEventList();
		}
		return instance;
	}

	private MediaEventList()
	{

	}

	/**
	 * Adds an {@code MediaListener}.
	 * 
	 * @param listener
	 *           the {@code MediaListener} to be added
	 */
	public void addListener(MediaListener listener)
	{
		listeners.add(MediaListener.class, listener);
	}

	/**
	 * Removes an {@code MediaListener}.
	 * 
	 * @param listener
	 *           the listener to be removed
	 */

	public void removeListener(MediaListener listener)
	{
		listeners.remove(MediaListener.class, listener);
	}

	/**
	 * Notifies all {@code MediaListener}s that have registered interest for
	 * notification on an {@code MediaEvent}.
	 * 
	 * @param event
	 *           the {@code MediaEvent} object
	 * @see MediaEvent
	 */
	public synchronized void notify(MediaEvent event)
	{
		for (MediaListener l : listeners.getListeners(MediaListener.class))
			l.action(event);

	}
}
