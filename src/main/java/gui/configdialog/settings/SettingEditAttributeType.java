/**
 * 
 */
package gui.configdialog.settings;

import gui.VennMaker;
import data.AttributeType;
import data.Netzwerk;

/**
 * Immidiate-Setting which edits an attribute-type.
 * 
 * 
 *
 */
public class SettingEditAttributeType implements ImmidiateConfigDialogSetting
{
	private AttributeType 	oldAType;
	
	private AttributeType 	editedAType;
	
	public SettingEditAttributeType(AttributeType oldAType,AttributeType editedAType)
	{
		this.oldAType = oldAType;
		this.editedAType = editedAType;
	}
	
	@Override
	public void set()
	{
		AttributeType copy = oldAType.clone();
		oldAType.changeTo(editedAType);
		editedAType = copy;
		updateActors();
	}
	
	@Override
	public void undo()
	{
		set();
	}
	
	private void updateActors()
	{
		//Acteure anpassen
		for(Netzwerk n : VennMaker.getInstance().getProject().getNetzwerke())
			n.updateAkteure(oldAType);
	}

	/**
	 * @return the oldAType
	 */
	public AttributeType getOldAType()
	{
		return oldAType;
	}

	/**
	 * @return the editedAType
	 */
	public AttributeType getEditedAType()
	{
		return editedAType;
	}
}
