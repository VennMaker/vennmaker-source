package gui.configdialog.save;

import java.util.ArrayList;
import java.util.List;

import data.AttributeType;

public class AttributeTypeSaveElement extends SaveElement
{
	private List<AttributeType>		attributesToAdd;

	private List<AttributeType>	attributesToDelete;


	public AttributeTypeSaveElement()
	{
		this.attributesToAdd = new ArrayList<AttributeType>();
		this.attributesToDelete = new ArrayList<AttributeType>();
	}

	/**
	 * @return the attributesToAdd
	 */
	public List<AttributeType> getAttributesToAdd()
	{
		return attributesToAdd;
	}

	/**
	 * @return the attributesToDelete
	 */
	public List<AttributeType> getAttributesToDelete()
	{
		return attributesToDelete;
	}

	
	/**
	 * Adds an @see gui.configdialog.settings.SettingAddAttributeType
	 * @param setting
	 */
	public void addAttribute(AttributeType setting)
	{
		if (setting == null)
			error();

		if(attributesToAdd.contains(setting))
			attributesToAdd.remove(setting);
		
		this.attributesToAdd.add(setting);
	}
	
	/**
	 * Adds an @see gui.configdialog.settings.SettingDeleteAttributeType
	 * @param setting 
	 */
	public void addAttributeToDelete(AttributeType setting)
	{
		if (setting == null)
			error();

		this.attributesToDelete.add(setting);
	}
	

	private void error()
	{
		throw new IllegalArgumentException("Setting must not be null");
	}
}
