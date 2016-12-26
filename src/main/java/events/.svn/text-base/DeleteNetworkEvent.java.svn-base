/**
 * 
 */
package events;

import data.Netzwerk;

/**
 * Wird erzeugt, wenn ein Netzwerk aus dem Projekt gelöscht wird.
 * 
 *
 */
public class DeleteNetworkEvent extends NetworkEvent
{

	/**
	 * Erzeugt ein neues NetzwerkEvent zum Löschen eines Netzwerkes
	 * @param netz Ein gültiges Netzwerk
	 */
	public DeleteNetworkEvent(Netzwerk netz)
	{
		super(netz);
	}
	
	public DeleteNetworkEvent(Netzwerk netz, boolean logEvent)
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
		return Messages.getString("DeleteNetworkEvent.desc"); //$NON-NLS-1$
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
		
		NewNetworkEvent evt = new NewNetworkEvent(getNetzwerk())
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
