/**
 * 
 */
package gui.configdialog.settings;
import gui.configdialog.ConfigDialog;
import data.EventProcessor;
import data.Netzwerk;
import events.DeleteNetworkEvent;

/**
 * Immidiate-Setting to clone a network map with all properties.
 * 
 * 
 *
 */
public class SettingCloneNetwork implements ImmidiateConfigDialogSetting
{
	private Netzwerk	netToClone;

	private Netzwerk	clonedNetwork;
	
	private String 		name;
	
	public SettingCloneNetwork(Netzwerk netToClone, String name)
	{
		this.name = name;
		this.netToClone = netToClone;
	}
	
	@Override
	public void set()
	{
		clonedNetwork = netToClone.cloneNetwork(name,false);
		ConfigDialog.getInstance().updateTree();
	}

	@Override
	public void undo()
	{
//		EventProcessor.getInstance().undoEvent();
		DeleteNetworkEvent evt = new DeleteNetworkEvent(clonedNetwork, false);
		EventProcessor.getInstance().fireEvent(evt);
		ConfigDialog.getInstance().updateTree();
	}
}
