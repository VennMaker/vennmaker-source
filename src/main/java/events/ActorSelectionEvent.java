package events;

import java.awt.geom.Rectangle2D;

public class ActorSelectionEvent extends VennMakerEvent
{
	private Rectangle2D.Double newSelection;
	
	public ActorSelectionEvent(Rectangle2D.Double newSelection)
	{
		this.newSelection = newSelection;
	}
	@Override
	public VennMakerEvent getUndoEvent()
	{
		return new ActorSelectionEvent(null)  {
			@Override
			public boolean isUndoevent()
			{
				return true;
			}  };
	}

	public Rectangle2D.Double getSelection()
	{
		return newSelection;
	}
	
	@Override
	public VennMakerEvent getRepeatEvent()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription()
	{
		// TODO Auto-generated method stub
		return Messages.getString("ActorSelectionEvent.desc");
	}

}
