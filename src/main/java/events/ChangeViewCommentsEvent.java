/**
 * 
 */
package events;

/**
 * Dieses Event wird erzeugt, wenn die Kommentare an- oder ausgeschaltet werden
 * 
 * 
 */
public class ChangeViewCommentsEvent extends VennMakerEvent
{
	/**
	 * Erzeugt ein neues Event, wenn die Kommentare an/ausgeschaltet wurden
	 * 
	 * @param newState
	 *           boolsche Angabe, ob Kommentare angezeigt werden, oder nicht
	 */
	public ChangeViewCommentsEvent(boolean newState)
	{
		this.state = newState;
	}

	/**
	 * Kommentare anzeigen?
	 */
	private boolean	state;

	/**
	 * Liefert den Zustand, ob Kommentare angezeigt werden, oder nicht
	 * 
	 * @return den neuen Zustand.
	 */
	public boolean getState()
	{
		return state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see events.VennMakerEvent#getRedoEvent()
	 */
	@Override
	public VennMakerEvent getRepeatEvent()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see events.VennMakerEvent#getUndoEvent()
	 */
	@Override
	public VennMakerEvent getUndoEvent()
	{
		return new ChangeViewCommentsEvent(!getState())
		{
			@Override
			public boolean isUndoevent()
			{
				return true;
			}
		};

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see events.VennMakerEvent#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return Messages.getString("ChangeViewCommentsEvent.desc"); //$NON-NLS-1$
	}

}
