/**
 * 
 */
package events;

import data.AkteurTyp;

/**
 * Dieses Event zeigt an, dass ein angegebener Akteurstyp geändert wurde. Es
 * werden nicht die Änderungen gespeichert sondern eine Kopie des Objekts vor
 * der Änderung und das geänderte AkteursTypObjekt. Natürlich kann sich dieses
 * Objekt bereits erneut geändert haben - daher ist dieses Event nur für
 * UndoZwecke geeignet.
 * 
 * Dieses Event benötigt zwei Kopien des Akteurstyps um die Daten zu speichern.
 * 
 * 
 * 
 * @version 0.6
 */
@Deprecated
public class ChangeActorTypeEvent extends ActorTypeEvent
{
	/**
	 * Eine Kopie des AkteurTypObjektes bevor es geändert wurde.
	 */
	final private AkteurTyp	oldCopy;

	/**
	 * Eine Kopie des AkteurTypObjektes nachdem es geändert wurde. Wird für undo
	 * benötigt - damit ist Differenzenbildung möglich, wird bisher allerdings
	 * nüscht benutzt.
	 */
	final private AkteurTyp	newCopy;

	/**
	 * Erzeugt ein neues Event.
	 * 
	 * @param typ
	 *           Das Objekt, das veraendert wurde.
	 * @param newCopy
	 *           Eine Kopie des Inhalts nach der Änderung
	 * @param oldCopy
	 *           Eine Kopie des Objekts, unmittelbar bevor es geändert wurde.
	 */
	public ChangeActorTypeEvent(AkteurTyp typ, AkteurTyp newCopy,
			AkteurTyp oldCopy)
	{
		super(typ);
		this.oldCopy = oldCopy;
		this.newCopy = newCopy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see events.VennMakerEvent#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return "Change actor type";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see events.VennMakerEvent#getRepeatEvent()
	 */
	@Override
	public VennMakerEvent getRepeatEvent()
	{
		// Kann man nicht wiederholen
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
		return new ChangeActorTypeEvent(super.getAkteurtyp(), this.oldCopy,
				this.newCopy)
		{

			/*
			 * (non-Javadoc)
			 * 
			 * @see events.VennMakerEvent#isUndoevent()
			 */
			@Override
			public boolean isUndoevent()
			{
				return true;
			}
		};
	}

	/**
	 * Liefert den Wert des Akteurtyps zurück, der vor Auftreten des Events galt.
	 * 
	 * @return Ein Kopie des Wertes vor der Ausführung des Events.
	 */
	public final AkteurTyp getOldCopy()
	{
		return oldCopy;
	}

	/**
	 * Liefert den Wert des Akteurtyps (als Kopie) zurück, der nach dem Auftreten
	 * des Events aktuell sein sollte.
	 * 
	 * @return Eine Kopie des Zustands nach Ausführung des Events.
	 */
	public final AkteurTyp getNewCopy()
	{
		return newCopy;
	}

}
