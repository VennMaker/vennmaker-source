package gui.configdialog.settings;

import data.EventProcessor;
import data.Netzwerk;
import events.DeleteNetworkEvent;
import events.NewNetworkEvent;

public class DeleteNetworkSetting implements ImmidiateConfigDialogSetting
{
	private Netzwerk network;
	private boolean logEvent;
	
	public DeleteNetworkSetting(Netzwerk network)
	{
		this.network = network;
	}
	
	public DeleteNetworkSetting(Netzwerk network, boolean logEvent)
	{
		this.network = network;
		this.logEvent = logEvent;
	}
	
	@Override
	public void set()
	{
		
		DeleteNetworkEvent evt = new DeleteNetworkEvent(network);
		evt.setIsLogEvent(logEvent);
		
		EventProcessor.getInstance().fireEvent(evt);
	}

	@Override
	public void undo()
	{
		NewNetworkEvent event = new NewNetworkEvent(network);
		event.setIsLogEvent(logEvent);
		
		EventProcessor.getInstance().fireEvent(event);
	}
}
