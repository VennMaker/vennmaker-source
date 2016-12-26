package gui.configdialog.settings;

import gui.VennMaker;

/**
 * Setting to change the interview meta data (interview notes)
 * 
 * 
 *
 */
public class SettingInterviewNotes implements ConfigDialogSetting
{
	private String notes;
	
	public SettingInterviewNotes(String notes)
	{
		this.notes = notes;
	}
	
	@Override
	public void set()
	{
		VennMaker.getInstance().getProject().setMetaInformation(notes);
	}
}
