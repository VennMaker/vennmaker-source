package events;

/**
 * This event is fired if a interview template was loaded sucessfully
 * 
 *
 */
public class LoadTemplateEvent extends VennMakerEvent
{
	
	
	public LoadTemplateEvent()
	{
		setIsLogEvent(false);
	}
	
	@Override
	public VennMakerEvent getUndoEvent()
	{
		return new LoadTemplateEvent();
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
		return "This Event calls EventListeners if a template was loaded sucessfull";
	}
}
