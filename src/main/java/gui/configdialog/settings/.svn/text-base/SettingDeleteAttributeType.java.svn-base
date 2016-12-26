/**
 * 
 */
package gui.configdialog.settings;

import gui.VennMaker;

import java.util.Vector;

import data.AttributeType;
import data.Projekt;

/**
 * Immidiate-Setting which deletes an attribute type.
 * 
 * 
 *
 */
public class SettingDeleteAttributeType implements ImmidiateConfigDialogSetting
{
	private AttributeType aType;
	
	public SettingDeleteAttributeType(AttributeType aType)
	{
		this.aType = aType;
	}
	
	@Override
	public void set()
	{
		Projekt p = VennMaker.getInstance().getProject();
		Vector<AttributeType> aTypes = p.getAttributeTypes();
		aTypes.remove(aType);
		p.setAttributeTypes(aTypes);
	}
	
	@Override
	public void undo()
	{
		Projekt p = VennMaker.getInstance().getProject();
		Vector<AttributeType> aTypes = p.getAttributeTypes();
		aTypes.add(aType);
		p.setAttributeTypes(aTypes);
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
		if(aType == null)
			throw new IllegalArgumentException("AttributeType must not be null");
		
		this.aType = aType;
	}
}
