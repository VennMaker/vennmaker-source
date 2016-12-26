package gui.configdialog.settings;

import interview.InterviewLayer;
import data.EventProcessor;
import data.Netzwerk;
import events.DeleteNetworkEvent;

public class SettingCloneVirtualNetwork implements ImmidiateConfigDialogSetting
{
	
	private Netzwerk network;
	
	public SettingCloneVirtualNetwork(Netzwerk network)
	{
		this.network = network;
	}
	
	@Override
	public void set()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void undo()
	{
		EventProcessor.getInstance().fireEvent(new DeleteNetworkEvent(network, false));
		InterviewLayer.getInstance().reset();
	}

}
