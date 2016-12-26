/**
 * 
 */
package gui.configdialog.settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to pack multiple immidiate-settings as one immidiate-setting. 
 * 
 * 
 */
public class ImmidiateConfigDialogMacroSetting implements ImmidiateConfigDialogSetting
{
	private List<ImmidiateConfigDialogSetting> settings;
	
	public ImmidiateConfigDialogMacroSetting()
	{
		settings = new ArrayList<ImmidiateConfigDialogSetting>();
	}
	
	/**
	 * Adds a setting to this macro-setting
	 */
	public void addSetting(ImmidiateConfigDialogSetting s)
	{
		settings.add(s);
	}
	
	/**
	 * Removes a setting from this macro-setting
	 */
	public void removeSetting(ImmidiateConfigDialogSetting s)
	{
		settings.remove(s);
	}

	@Override
	public void set()
	{
		for(ConfigDialogSetting s : settings)
			s.set();
	}

	@Override
	public void undo()
	{
		for(int i=settings.size()-1; i>=0; i--)
			settings.get(i).undo();
	}
}
