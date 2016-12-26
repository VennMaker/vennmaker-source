/**
 * 
 */
package data;

import javax.swing.event.EventListenerList;

import events.MediaEvent;
import events.MenuEvent;

/**
 * 
 * Liste der MediaEvent-Objekte
 */
public class MenuEventList
{
	
	/** List of listeners. */
	private static EventListenerList	listeners	= new EventListenerList();

	/**
	 * Singleton: Referenz.
	 */
	private static MenuEventList		instance  = new MenuEventList();

	/**
	 * Singleton: Zugriff.
	 * 
	 * @return Die einzige Instanz in diesem Prozess.
	 */
	public static MenuEventList getInstance()
	{
		if (instance == null)
		{
			instance = new MenuEventList();
		}
		return instance;
	}

	private MenuEventList()
	{

	}

	/**
	 * Adds an {@code MediaListener}.
	 * 
	 * @param listener
	 *           the {@code MediaListener} to be added
	 */
	public void addListener(MenuListener listener)
	{
		listeners.add(MenuListener.class, listener);
	}

	/**
	 * Removes an {@code MediaListener}.
	 * 
	 * @param listener
	 *           the listener to be removed
	 */

	public void removeListener(MenuListener listener)
	{
		listeners.remove(MenuListener.class, listener);
	}

	/**
	 * Notifies all {@code MediaListener}s that have registered interest for
	 * notification on an {@code MediaEvent}.
	 * 
	 * @param event
	 *           the {@code MediaEvent} object
	 * @see MediaEvent
	 */
	public synchronized void notify(MenuEvent event)
	{
		for (MenuListener l : listeners.getListeners(MenuListener.class))
			l.action(event);

	}
}
