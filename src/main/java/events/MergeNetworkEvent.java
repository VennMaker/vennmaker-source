package events;

import data.Netzwerk;

/**
 * Zeigt an, dass zwei Netzwerke zusammengefuehrt wurden
 * 
 *
 */
public class MergeNetworkEvent extends NetworkEvent
{

	/**
	 * Erzeugt ein Event für ein neues Netzwerk.
	 * @param netz Ein gültiges Netzwerk.
	 */
	public MergeNetworkEvent(Netzwerk netz)
	{
		super(netz);
	}
	
	public MergeNetworkEvent(Netzwerk netz, boolean logEvent)
	{
		super(netz);
		this.logEvent = logEvent;
	}
	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return "Merge Network"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getRedoEvent()
	 */
	@Override
	public VennMakerEvent getRepeatEvent()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getUndoEvent()
	 */
	@Override
	public VennMakerEvent getUndoEvent()
	{
		
		DeleteNetworkEvent evt = new DeleteNetworkEvent(getNetzwerk())
		{
			public boolean isUndoevent()
			{
				return true;
			}
		};

		evt.setIsLogEvent(logEvent);
		
		return evt;
	}



}
