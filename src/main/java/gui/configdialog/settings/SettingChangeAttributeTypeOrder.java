/**
 * 
 */
package gui.configdialog.settings;

import gui.VennMaker;

import java.util.Vector;

import data.AttributeType;


/**
 * Immidiate-Setting which changes the order of the attribute-types.
 * 
 * 
 *
 */
public class SettingChangeAttributeTypeOrder implements ImmidiateConfigDialogSetting
{
	private int 		index1;
	
	private int 		index2;
	
	private String 		getType;
	
	public SettingChangeAttributeTypeOrder(int index1, int index2, String getType)
	{
		this.index1 = index1;
		this.index2 = index2;
		this.getType = getType;
	}
	
	@Override
	public void set()
	{
		Vector<AttributeType> aTypes = VennMaker.getInstance().getProject().getAttributeTypes(getType);
		AttributeType a = aTypes.get(index1);
		AttributeType b = aTypes.get(index2);
		
		Vector<AttributeType> aTypesAll = VennMaker.getInstance().getProject().getAttributeTypes();
		int all_index1 = aTypesAll.indexOf(a);
		int all_index2 = aTypesAll.indexOf(b);
		
		aTypesAll.set(all_index1, b);
		aTypesAll.set(all_index2, a);
		VennMaker.getInstance().getProject().setAttributeTypes(aTypesAll);
	}
	
	@Override
	public void undo()
	{
		set();
	}
}
