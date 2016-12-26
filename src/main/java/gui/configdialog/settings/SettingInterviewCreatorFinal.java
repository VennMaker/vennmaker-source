/**
 * 
 */
package gui.configdialog.settings;

import gui.VennMaker;
import gui.configdialog.elements.CDialogInterviewCreator;
import gui.configdialog.save.InterviewSaveElement;
import interview.InterviewLayer;

/**
 * 
 *
 */
public class SettingInterviewCreatorFinal implements ConfigDialogSetting
{
	private CDialogInterviewCreator creator;
	
	public SettingInterviewCreatorFinal(CDialogInterviewCreator creator)
	{
		this.creator = creator;
	}
	@Override
	public void set()
	{
		InterviewLayer layer = InterviewLayer.getInstance();
		layer.save();
		VennMaker
				.getInstance()
				.getProject()
				.setCurrentInterviewConfig(
						(InterviewSaveElement) creator.getSaveElement());
		
		creator.saveTreeNodesOnClose();
	}
}
