/**
 * 
 */
package gui.configdialog.settings;

import data.AttributeType;

/**
 * Immidiate-Setting which renames an attributetypegroup of one attributeType.
 * 
 * 
 * 
 */
public class SettingRenameAttributeGroup implements
		ImmidiateConfigDialogSetting
{
	private AttributeType	aType;

	private String				newName;

	private String				oldName;

	public SettingRenameAttributeGroup(AttributeType aType, String newName)
	{
		this.aType = aType;
		this.newName = newName;
		this.oldName = aType.getType();
	}

	@Override
	public void set()
	{
		aType.setType(newName);
	}

	@Override
	public void undo()
	{
		aType.setType(oldName);
	}

	/**
	 * @return the aType
	 */
	public AttributeType getAttributeType()
	{
		return aType;
	}

	public void setAttributeType(AttributeType aType)
	{
		if (aType == null)
			throw new IllegalArgumentException("AttributeType must not be null");

		this.aType = aType;
	}
}
