package gui.configdialog.settings;

import interview.InterviewLayer;

public class SettingLoadInterview implements ImmidiateConfigDialogSetting
{

	@Override
	public void set()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void undo()
	{
		InterviewLayer.getInstance().reset();
		InterviewLayer.getInstance().saveInterviewTree(null);
	}
}
