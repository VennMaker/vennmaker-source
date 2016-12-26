/**
 * 
 */
package events;

import java.awt.geom.Point2D;

import data.Akteur;
import data.Netzwerk;

/**
 * Wird erzeugt, wenn ein Akteur auf der Zeichenfläche bewegt wird.
 * 
 *
 */
public class MoveActorEvent extends ActorInNetworkEvent
{
	/**
	 * Der Bewegungsvektor, um den der Akteur verschoben wurde.
	 * Alte_position + Bewegung = Neue_position
	 */
	private Point2D move;

	/**
	 * Zeigt an, dass der angegebene Akteur im angebenen Netzwerk verschoben wurde.
	 * Und zwar auf die angegebene Position.
	 * @param akteur Der verschobene Akteur
	 * @param netz Das Netzwerk in dem das Ereignis passierte
	 * @param move Die Verschiebung des Akteurs, als Differenz von alter und neuer Position.
	 */
	public MoveActorEvent(Akteur akteur, Netzwerk netz, Point2D move)
	{
		super(akteur, netz);
		this.move = move;
	}

	/*
	 * (non-Javadoc)
	 * @see events.VennMakerEvent#getRedoEvent()
	 */
	@Override
	public VennMakerEvent getRepeatEvent()
	{
		return new MoveActorEvent(getAkteur(),getNetzwerk(),getMove());
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getUndoEvent()
	 */
	@Override
	public VennMakerEvent getUndoEvent()
	{
		return new MoveActorEvent(getAkteur(),getNetzwerk(),new Point2D.Double(-getMove().getX(),-getMove().getY()))  {
			@Override
			public boolean isUndoevent()
			{
				return true;
			}  };
	}

	/**
	 * Die Differenz, um die der Akteur verschoben wurde.
	 * @return Die Differenz zwischen alter und neuer Position.
	 */
	public Point2D getMove()
	{
		return move;
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return Messages.getString("MoveActorEvent.desc"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see events.VennMakerEvent#isRedoable()
	 */
	@Override
	public boolean isRepeatable()
	{
		return true;
	}

}
