/**
 * 
 */
package events;

import data.Akteur;
import data.Netzwerk;

/**
 * Dieses Ereignis tritt auf, wenn die Größe eines Akteurs verändert wurde.
 * 
 * 
 * 
 */
@Deprecated
public class SizeActorEvent extends ActorInNetworkEvent
{
	/**
	 * Die Änderung der Größe in Prozent (aus Kompatibilitätsgründen)
	 */
	private final double	sizeChange;

	/**
	 * Erzeugt ein neues Event für den angebenen Akteur im angegebenen Netzwerk.
	 * Die Änderung der Größe muss ebenfalls angegeben werden.
	 * 
	 * @param akteur
	 *           Der Akteur, der verändert wurde.
	 * @param netz
	 *           Das Netzwerk, in dem gearbeitet wurde.
	 * @param sizeChange
	 *           Die Größenänderung des Akteurs (in Prozent der vorgängergröße).
	 *           Ein Wert von <code>1.0</code> entspricht keiner Änderung. Darf
	 *           nicht negativ und nicht <code>0.0</code> sein.
	 */
	public SizeActorEvent(final Akteur akteur, final Netzwerk netz,
			final double sizeChange)
	{
		super(akteur, netz);

		assert (sizeChange > 0.0) : sizeChange;

		this.sizeChange = sizeChange;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see events.VennMakerEvent#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return Messages.getString("SizeActorEvent.desc"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see events.VennMakerEvent#getRedoEvent()
	 */
	@Override
	public VennMakerEvent getRepeatEvent()
	{
		return new SizeActorEvent(getAkteur(), getNetzwerk(), this.sizeChange);
	}

	/**
	 * Gibt die prozentuale Änderung der Größenänderung relativ zur Anfangsgröße
	 * an. (0.0 = unsichtbar, 1.0 = keine Änderung, 2.0 = doppelte Größe, 0.5 =
	 * halbe Größe, usw.)
	 * 
	 * @return Ein Wert größer <code>0.0</code>.
	 */
	public double getSizeChange()
	{
		return this.sizeChange;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see events.VennMakerEvent#getUndoEvent()
	 */
	@Override
	public VennMakerEvent getUndoEvent()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see events.VennMakerEvent#isRedoable()
	 */
	@Override
	public boolean isRepeatable()
	{
		return true;
	}

}
