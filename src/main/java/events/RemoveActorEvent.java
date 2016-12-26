/**
 * 
 */
package events;

import java.awt.geom.Point2D;

import data.Akteur;
import data.Netzwerk;

/**
 * Wird erzeugt, wenn ein Akteur aus einem Netz wieder entfernt wird.
 * 
 *
 */
public class RemoveActorEvent extends ActorInNetworkEvent
{
	/**
	 * Erzeugt ein neues Event.
	 * @param akteur Der Akteur der entfernt wurde.
	 * @param netz Das Netzwerk aus dem er entfernt wurde.+
	 * @param lastLoc Die Position, an der sich der Akteur im Netz befand
	 */
	public RemoveActorEvent(Akteur akteur, Netzwerk netz, Point2D lastLoc)
	{
		super(akteur,netz);
		this.lastLoc = lastLoc;
	}
	
	/**
	 * Die Position an der sich der Akteur befand, bevor er entfernt wurde.
	 */
	private Point2D lastLoc;
	
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
		return new AddActorEvent(getAkteur(), getNetzwerk(), lastLoc)  {
			@Override
			public boolean isUndoevent()
			{
				return true;
			}  };
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return Messages.getString("RemoveActorEvent.desc"); //$NON-NLS-1$
	}

}
