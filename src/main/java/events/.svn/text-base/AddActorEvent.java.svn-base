/**
 * 
 */
package events;

import java.awt.geom.Point2D;

import data.Akteur;
import data.Netzwerk;

/**
 * Dieses Event wird erzeugt, wenn ein neuer Akteur zu einem Netzwerk
 * hinzugefügt wurde. Dies impliziert, dass es den Akteur bereits gibt!
 * 
 * 
 */
public class AddActorEvent extends ActorInNetworkEvent
{
	
	/**
	 * Die Position auf der der Akteur positioniert werden soll.
	 */
	private Point2D loc;

	/**
	 * Erzeugt ein neues Event. Dieses Event symbolisiert das Hinzufügen des
	 * angegebenen Akteurs zum gegebenen Netzwerk.
	 * 
	 * @param akteur
	 *           Der Akteur der hinzugefügt wurde.
	 * @param netz
	 *           Das Netzwerk zu dem er hinzugefügt wurde.
	 * @param loc
	 * 			 Die Position, auf die der Akteur positioniert werden soll.
	 */
	public AddActorEvent(Akteur akteur, Netzwerk netz, Point2D loc)
	{
		super(akteur, netz);
		this.loc = loc;
	}
	
	
	/**
	 * Liefert die Position auf der der Akteur sichtbar werden soll.
	 * @return Die neue Position des Akteurs.
	 */
	public Point2D getLocation()
	{
		return this.loc;
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
		return new RemoveActorEvent(getAkteur(), getNetzwerk(), loc)
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
		return Messages.getString("AddActorEvent.desc"); //$NON-NLS-1$
	}

}
