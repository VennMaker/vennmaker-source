/**
 * 
 */
package gui.configdialog.settings;

import gui.VennMaker;

import java.util.Vector;

import data.AttributeType;
import data.Projekt;

/**
 * Immidiate-Setting which adds a new Attribute.
 * 
 * 
 */
public class SettingAddAttributeType implements ImmidiateConfigDialogSetting
{
	private AttributeType aType;
	
	public SettingAddAttributeType(AttributeType aType)
	{
		this.aType = aType;
	}
	
	@Override
	public void set()
	{
		Projekt p = VennMaker.getInstance().getProject();
		Vector<AttributeType> aTypes = p.getAttributeTypes();
		aTypes.add(aType);
		p.setAttributeTypes(aTypes);
	}
	
	@Override
	public void undo()
	{
		Projekt p = VennMaker.getInstance().getProject();
		Vector<AttributeType> aTypes = p.getAttributeTypes();
		aTypes.remove(aType);
		p.setAttributeTypes(aTypes);
	}
}
