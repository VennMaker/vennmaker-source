/**
 * 
 */
package gui.configdialog.settings;

import gui.VennMaker;
import data.EventProcessor;
import data.Netzwerk;
import events.DeleteNetworkEvent;

/**
 * Immidiate-Setting which adds a new empty network map.
 * 
 * 
 * 
 */
public class SettingNewNetwork implements ImmidiateConfigDialogSetting
{
	private String		name;

	private Netzwerk	netzwerk;

	public SettingNewNetwork(String name)
	{
		this.name = name;
	}

	@Override
	public void set()
	{
		netzwerk = VennMaker.getInstance().getProject().getCurrentNetzwerk()
				.getNewNetwork(name, false);

		// if (netzwerk != null && !name.equals(""))

	}

	@Override
	public void undo()
	{
		// EventProcessor.getInstance().undoEvent();
		DeleteNetworkEvent evt = new DeleteNetworkEvent(netzwerk, false);

		EventProcessor.getInstance().fireEvent(evt);
	}

}
