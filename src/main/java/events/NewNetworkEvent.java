/**
 * 
 */
package events;

import data.Netzwerk;

/**
 * Dieses Event zeigt an, dass ein neues Netzwerk in die Datenbasis hinzugefügt wurde.
 * 
 *
 */
public class NewNetworkEvent extends NetworkEvent
{
	/**
	 * Wird benötigt um Einstellungen wie (hideLegend usw...)
	 * auch beim Clone zu übernehmen
	 * 
	 * <code>null</code> wenn es sich um ein "neues, leeres Netzwerk" handelt
	 */
	private Netzwerk 		clonedFrom = null;

	/**
	 * Erzeugt ein Event für ein neues Netzwerk.
	 * @param netz Ein gültiges Netzwerk.
	 */
	public NewNetworkEvent(Netzwerk netz)
	{
		super(netz);
	}
	
	public NewNetworkEvent(Netzwerk netz, boolean logEvent)
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
		return Messages.getString("NewNetworkEvent.desc"); //$NON-NLS-1$
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

	public boolean isClone()
	{
		return clonedFrom != null;
	}
	
	public Netzwerk getCloneFather()
	{
		return clonedFrom;
	}

	public void setCloneFather(Netzwerk clonedFrom)
	{
		this.clonedFrom = clonedFrom;
	}
}
