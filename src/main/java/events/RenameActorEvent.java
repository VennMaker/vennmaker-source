/**
 * Dieses Event wird ausgelöst wenn ein Akteur umbenannt wird
 */
package events;

import data.Akteur;

/**
 * 
 * 
 */
public class RenameActorEvent extends ActorEvent
{
	private String	oldName;

	private String	newName;

	public RenameActorEvent(Akteur akteur, String newName)
	{
		super(akteur);
		this.newName = newName;
		this.oldName = akteur.getName();
	}

	@Override
	public String getDescription()
	{
		return "Rename actor"; //$NON-NLS-1$
	}

	@Override
	public VennMakerEvent getRepeatEvent()
	{
		return null;
	}

	public String getNewName()
	{
		return newName;
	}

	@Override
	public VennMakerEvent getUndoEvent()
	{
		return new RenameActorEvent(getAkteur(), oldName)
		{
			@Override
			public boolean isUndoevent()
			{
				return true;
			}
		};
	}
}
