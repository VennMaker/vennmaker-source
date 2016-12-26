/**
 * 
 */
package gui.configdialog.settings;

import gui.VennMaker;

import java.util.HashMap;

import data.AttributeType;

/**
 * Setting which changes the trigger-attributes.
 * 
 * 
 * 
 */
public class SettingChangeTrigger implements ConfigDialogSetting
{
	private HashMap<String, AttributeType>		mainGenAType;

	private AttributeType						mainAType;

	private String								getType;

	public SettingChangeTrigger(HashMap<String, AttributeType> mainGenAType,
			AttributeType mainAType, String getType)
	{
		this.mainGenAType = mainGenAType;
		this.mainAType = mainAType;
		this.getType = getType;
	}

	@Override
	public void set()
	{
		for (String collector : mainGenAType.keySet())
		{
			VennMaker.getInstance().getProject()
					.setMainGeneratorType(collector, mainGenAType.get(collector));
		}
		VennMaker.getInstance().getProject()
				.setMainAttributeType(getType, mainAType);
	}
}
