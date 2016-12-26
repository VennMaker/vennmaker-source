/**
 * 
 */
package events;

import data.Akteur;
import data.Kommentar;
import data.Netzwerk;

/**
 * Dieses Ereignis tritt auf, wenn der Kommentar eines Akteurs verändert wurde.
 * 
 * 
 * 
 */
@Deprecated
public class ChangeActorCommentEvent extends ActorInNetworkEvent
{
	/**
	 * Die Änderung des Kommentars in zwei Strings gespeichert
	 */
	private final Kommentar	oldComment;

	private final Kommentar	newComment;

	/**
	 * Erzeugt ein neues Event für den angebenen Akteur im angegebenen Netzwerk.
	 * Die Änderung des Kommentars muss ebenfalls angegeben werden.
	 * 
	 * @param akteur
	 *           Der Akteur, der verändert wurde.
	 * @param netz
	 *           Das Netzwerk, in dem gearbeitet wurde.
	 * @param oldComment
	 *           Der alte Kommentar
	 * @param newComment
	 *           Der neue Kommentar
	 */
	public ChangeActorCommentEvent(final Akteur akteur, final Netzwerk netz,
			final Kommentar oldComment, final Kommentar newComment)
	{
		super(akteur, netz);

		this.oldComment = oldComment;
		this.newComment = newComment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see events.VennMakerEvent#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return Messages.getString("ChangeActorCommentEvent.desc"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see events.VennMakerEvent#getRedoEvent()
	 */
	@Override
	public VennMakerEvent getRepeatEvent()
	{
		return new ChangeActorCommentEvent(getAkteur(), getNetzwerk(),
				this.oldComment, this.newComment);
	}

	/**
	 * Gibt den neuen Kommentar zurueck
	 * 
	 * @return Den neuen Kommentar
	 */
	public Kommentar getNewComment()
	{
		return this.newComment;
	}

	/**
	 * Gibt den alten Kommentar zurueck
	 * 
	 * @return Den alten Kommentar
	 */
	public Kommentar getOldComment()
	{
		return this.oldComment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see events.VennMakerEvent#getUndoEvent()
	 */
	@Override
	public VennMakerEvent getUndoEvent()
	{
		return new ChangeActorCommentEvent(getAkteur(), getNetzwerk(), this
				.getNewComment(), this.getOldComment())
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
	 * @see events.VennMakerEvent#isRedoable()
	 */
	@Override
	public boolean isRepeatable()
	{
		return true;
	}

}
