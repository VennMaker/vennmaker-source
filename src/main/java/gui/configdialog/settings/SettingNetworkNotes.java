package gui.configdialog.settings;

import data.Netzwerk;

/**
 * Setting to change the interview meta data (interview notes)
 * 
 * 
 *
 */
public class SettingNetworkNotes implements ConfigDialogSetting
{
	private String notes;
	private Netzwerk net;
	
	public SettingNetworkNotes(String notes, Netzwerk net)
	{
		this.notes = notes;
		this.net = net;
	}
	
	@Override
	public void set()
	{
		net.setMetaInfo(notes);
	}
}
