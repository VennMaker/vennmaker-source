/**
 * 
 */
package gui.configdialog.settings;

import gui.VennMaker;

import java.util.List;

import data.AttributeType;

/**
 * Setting to apply changes related to actor label.
 * 
 * 
 */
public class SettingRelationLabel implements ConfigDialogSetting
{
	private List<AttributeType> 	labelList;
	
	private List<AttributeType> 	tooltipList;
	
	public SettingRelationLabel(List<AttributeType> labelList, List<AttributeType> tooltipList)
	{
		this.labelList = labelList;
		this.tooltipList = tooltipList;
	}
	
	@Override
	public void set()
	{
		VennMaker.getInstance().getProject().setDisplayedAtRelation(labelList);
		VennMaker.getInstance().getProject().setDisplayedAtRelationTooltip(tooltipList);
	}
}
