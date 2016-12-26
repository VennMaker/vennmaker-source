/**
 * 
 */
package gui.configdialog.settings;

import gui.VennMaker;

import java.util.List;

import data.AttributeType;
import data.Config.LabelBehaviour;

/**
 * Setting to apply changes related to actor label.
 * 
 * 
 */
public class SettingActorLabel implements ConfigDialogSetting
{
	private List<AttributeType> 	labelList;
	
	private List<AttributeType> 	tooltipList;
	
	private LabelBehaviour 			labelBehaviour;
	
	public SettingActorLabel(List<AttributeType> labelList, List<AttributeType> tooltipList, LabelBehaviour labelBehaviour)
	{
		this.labelList = labelList;
		this.tooltipList = tooltipList;
		this.labelBehaviour = labelBehaviour;
	}
	
	@Override
	public void set()
	{
		VennMaker.getInstance().getConfig().setLabelBehaviour(labelBehaviour);
		VennMaker.getInstance().getProject().setDisplayedAtActor(labelList);
		VennMaker.getInstance().getProject().setDisplayedAtActorTooltip(tooltipList);
	}
}
